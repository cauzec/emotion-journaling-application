package software.amazon.serverless.apprepo.api.impl;

import io.swagger.api.TherapistApi;
import io.swagger.model.Therapist;
import io.swagger.model.TherapistList;
import io.swagger.model.TherapistSummary;

import java.time.Clock;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.serverless.apprepo.api.impl.pagination.InvalidTokenException;
import software.amazon.serverless.apprepo.api.impl.pagination.TokenSerializer;
import software.amazon.serverless.apprepo.container.config.ConfigProvider;

/**
 * ApplicationsService implements {@link TherapistApi}.
 *
 * <p>It interacts with DynamoDB when processing each API.
 */
@Slf4j
@RequiredArgsConstructor
public class TherapistService implements TherapistApi {
  static final Integer DEFAULT_LIST_THERAPIST_LIMIT = 10;
  private final TokenSerializer<Map<String, AttributeValue>> paginationTokenSerializer;
  private final DynamoDbClient dynamodb;
  private final ModelMapper modelMapper;
  private final String tableName;
  private final Clock clock;
  @Context
  @Setter
  private SecurityContext securityContext;

  @Inject
  public TherapistService(
        final TokenSerializer<Map<String, AttributeValue>> paginationTokenSerializer,
        final DynamoDbClient dynamodb, final ConfigProvider configProvider) {
    this(paginationTokenSerializer, dynamodb, configureModelMapper(),
          configProvider.getTherapistTableName(), Clock.systemUTC());
  }

  public TherapistService(
        final TokenSerializer<Map<String, AttributeValue>> paginationTokenSerializer,
        final DynamoDbClient dynamodb, final ConfigProvider configProvider,
        final Clock clock) {
    this(paginationTokenSerializer, dynamodb, configureModelMapper(),
          configProvider.getTherapistTableName(), clock);
  }

  private static ModelMapper configureModelMapper() {
    ModelMapper modelMapper = new ModelMapper();
    PropertyMap<TherapistRecord, Therapist> therapistMap =
        new PropertyMap<TherapistRecord, Therapist>() {
          protected void configure() {
            map(source.getCreatedAt()).setCreationTime(null);
          }
        };

    PropertyMap<TherapistRecord, TherapistSummary> therapistSummaryMap =
        new PropertyMap<TherapistRecord, TherapistSummary>() {
          protected void configure() {
            map(source.getCreatedAt()).setCreationTime(null);
          }
        };

    modelMapper.addMappings(therapistMap);
    modelMapper.addMappings(therapistSummaryMap);
    return modelMapper;
  }

  public Therapist addTherapist(final Therapist therapist) {
    log.info("Creating therapist with input {}", therapist);
    TherapistRecord therapistRecord = modelMapper.map(therapist,
          TherapistRecord.class);
    String id = UUID.randomUUID().toString();
    therapistRecord.setTherapistId(id);
    therapistRecord.setCreatedAt(Instant.now(clock));
    therapistRecord.setVersion(1L);
    therapistRecord.setUserId("Raj");
    dynamodb.putItem(PutItemRequest.builder()
            .tableName(tableName)
            .item(therapistRecord.toAttributeMap())
            .conditionExpression(
                  String.format("attribute_not_exists(%s) AND attribute_not_exists(%s)",
                        TherapistRecord.USER_ID_ATTRIBUTE_NAME,
                        TherapistRecord.THERAPIST_ID_ATTRIBUTE_NAME))
            .build());
    return modelMapper.map(therapistRecord, Therapist.class);
  }

  public void deleteTherapist(final String therapistId) {
    log.info("Deleting therapist {}", therapistId);
    TherapistRecord therapistRecord = loadTherapist(therapistId);
    Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
    expressionAttributeValues.put(":v", AttributeValue.builder()
          .n(therapistRecord.getVersion().toString())
          .build());
    dynamodb.deleteItem(DeleteItemRequest.builder()
          .tableName(tableName)
          .key(toKeyRecord(therapistId))
          .conditionExpression(String.format("%s = :v", TherapistRecord.VERSION_ATTRIBUTE_NAME))
          .expressionAttributeValues(expressionAttributeValues)
          .build());
  }

  public Therapist getTherapist(String therapistId) {
    log.info("Getting therapist {}", therapistId);
    TherapistRecord therapistRecord = loadTherapist(therapistId);
    return modelMapper.map(therapistRecord, Therapist.class);
  }

  public TherapistList getTherapistList(final String nextToken, final Integer maxItems) {
    log.info("Listing therapists with nextToken {} and maxItems {}", nextToken, maxItems);
    Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
    expressionAttributeValues.put(":u", AttributeValue.builder()
          .s("Raj")
          .build());

    QueryRequest.Builder requestBuilder = QueryRequest.builder()
          .consistentRead(true)
          .tableName(tableName)
          .keyConditionExpression(String.format("%s = :u",
                TherapistRecord.USER_ID_ATTRIBUTE_NAME))
          .expressionAttributeValues(expressionAttributeValues)
          .limit(maxItems == null ? DEFAULT_LIST_THERAPIST_LIMIT : maxItems);
    if (nextToken != null) {
      try {
        requestBuilder.exclusiveStartKey(paginationTokenSerializer.deserialize(nextToken));
      } catch (InvalidTokenException e){System.out.println(e);} 
    }
    QueryResponse queryResponse = dynamodb.query(requestBuilder.build());

    List<TherapistSummary> therapistSummaries = queryResponse.items()
          .stream()
          .map(TherapistRecord::new)
          .map(record -> modelMapper.map(record, TherapistSummary.class))
          .collect(Collectors.toList());

    TherapistList result = new TherapistList()
          .therapist(therapistSummaries);
    Map<String, AttributeValue> lastEvaluatedKey = queryResponse.lastEvaluatedKey();
    if (lastEvaluatedKey != null && !lastEvaluatedKey.isEmpty()) {
      result.nextToken(paginationTokenSerializer.serialize(lastEvaluatedKey));
    }
    return result;
  }

  public TherapistList getTherapistByNTA(final String therapistArea, final String nextToken, final String therapistType) {
      log.info("Listing therapists with therapistArea {} , nextToken {} and therapistType {}", therapistArea, nextToken, therapistType);

      if (therapistType == null)
      {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":therapistAreaValue", AttributeValue.builder()
                .s(therapistArea)
                .build());

        QueryRequest.Builder requestBuilder = QueryRequest.builder()
                .tableName(tableName)
                .indexName("areaTypeIndex")
                .keyConditionExpression("therapistArea = :therapistAreaValue")
                .expressionAttributeValues(expressionAttributeValues);

        if (nextToken != null) {
          try {
            requestBuilder.exclusiveStartKey(paginationTokenSerializer.deserialize(nextToken));
          } catch (InvalidTokenException e){System.out.println(e);}
        }

        QueryResponse queryResponse = dynamodb.query(requestBuilder.build());

        List<TherapistSummary> therapistSummaries = queryResponse.items()
                .stream()
                .map(TherapistRecord::new)
                .map(record -> modelMapper.map(record, TherapistSummary.class))
                .collect(Collectors.toList());

        TherapistList result = new TherapistList()
                .therapist(therapistSummaries);
        Map<String, AttributeValue> lastEvaluatedKey = queryResponse.lastEvaluatedKey();
        if (lastEvaluatedKey != null && !lastEvaluatedKey.isEmpty()) {
          result.nextToken(paginationTokenSerializer.serialize(lastEvaluatedKey));
        }
        return result;
      }
      else
      {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":therapistAreaValue", AttributeValue.builder()
                .s(therapistArea)
                .build());
        expressionAttributeValues.put(":therapistTypeValue", AttributeValue.builder()
                .s(therapistType)
                .build());

        QueryRequest.Builder requestBuilder = QueryRequest.builder()
                .tableName(tableName)
                .indexName("areaTypeIndex")
                .keyConditionExpression("therapistArea = :therapistAreaValue AND therapistType = :therapistTypeValue")
                .expressionAttributeValues(expressionAttributeValues);

        if (nextToken != null) {
          try {
            requestBuilder.exclusiveStartKey(paginationTokenSerializer.deserialize(nextToken));
          } catch (InvalidTokenException e){System.out.println(e);}
        }

        QueryResponse queryResponse = dynamodb.query(requestBuilder.build());

        List<TherapistSummary> therapistSummaries = queryResponse.items()
                .stream()
                .map(TherapistRecord::new)
                .map(record -> modelMapper.map(record, TherapistSummary.class))
                .collect(Collectors.toList());

        TherapistList result = new TherapistList()
                .therapist(therapistSummaries);
        Map<String, AttributeValue> lastEvaluatedKey = queryResponse.lastEvaluatedKey();
        if (lastEvaluatedKey != null && !lastEvaluatedKey.isEmpty()) {
          result.nextToken(paginationTokenSerializer.serialize(lastEvaluatedKey));
        }
        return result;
      }
    }

/* 
  public TherapistList getTherapistByNTA(final String therapistArea, final String nextToken, final String therapistType) {
      log.info("Listing therapists with therapistArea {} , nextToken {} and therapistType {}", therapistArea, nextToken, therapistType);
      Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
      expressionAttributeValues.put(":therapistAreaValue", AttributeValue.builder()
            .s(therapistArea)
            .build());
      expressionAttributeValues.put(":therapistTypeValue", AttributeValue.builder()
            .s(therapistType)
            .build());

      QueryRequest.Builder requestBuilder = QueryRequest.builder()
            .tableName(tableName)
            .indexName("areaTypeIndex")
            .keyConditionExpression("therapistArea = :therapistAreaValue AND therapistType = :therapistTypeValue")
            .expressionAttributeValues(expressionAttributeValues);

      if (nextToken != null) {
      try {
        requestBuilder.exclusiveStartKey(paginationTokenSerializer.deserialize(nextToken));
      } catch (InvalidTokenException e){System.out.println(e);}
      }

      QueryResponse queryResponse = dynamodb.query(requestBuilder.build());
  
      List<TherapistSummary> therapistSummaries = queryResponse.items()
            .stream()
            .map(TherapistRecord::new)
            .map(record -> modelMapper.map(record, TherapistSummary.class))
            .collect(Collectors.toList());
  
      TherapistList result = new TherapistList()
            .therapist(therapistSummaries);
      Map<String, AttributeValue> lastEvaluatedKey = queryResponse.lastEvaluatedKey();
      if (lastEvaluatedKey != null && !lastEvaluatedKey.isEmpty()) {
        result.nextToken(paginationTokenSerializer.serialize(lastEvaluatedKey));
      }
      return result;
    }
*/
  public Therapist updateTherapist(final Therapist therapist,
                                       final String therapistId) {
    log.info("Updating therapist {} with input {}", therapistId, therapist);

    TherapistRecord therapistRecord = loadTherapist(therapistId);
    Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
    String therapistName = therapist.getTherapistName();
    String therapistMob = therapist.getTherapistMob();
    String therapistType = therapist.getTherapistType();
    String therapistArea = therapist.getTherapistArea();
    List<String> updateExpressionList = new ArrayList<>();
    if (therapistName != null) {
      therapistRecord.setTherapistName(therapistName);
      expressionAttributeValues.put(":a", AttributeValue.builder().s(therapistName).build());
      updateExpressionList.add(String.format("%s = :a",
            TherapistRecord.THERAPIST_NAME_ATTRIBUTE_NAME));
    }
    if (therapistMob != null) {
      therapistRecord.setTherapistMob(therapistMob);
      expressionAttributeValues.put(":d", AttributeValue.builder().s(therapistMob).build());
      updateExpressionList.add(String.format("%s = :d",
              TherapistRecord.THERAPIST_MOBILE_ATTRIBUTE_NAME));
    }
    if (therapistType != null) {
      therapistRecord.setTherapistType(therapistType);
      expressionAttributeValues.put(":h", AttributeValue.builder().s(therapistType).build());
      updateExpressionList.add(String.format("%s = :h",
            TherapistRecord.THERAPIST_TYPE_ATTRIBUTE_NAME));
    }
    if (therapistArea != null) {
      therapistRecord.setTherapistArea(therapistArea);
      expressionAttributeValues.put(":l", AttributeValue.builder().s(therapistArea).build());
      updateExpressionList.add(String.format("%s = :l",
            TherapistRecord.THERAPIST_AREA_ATTRIBUTE_NAME));
    }

    long newVersion = therapistRecord.getVersion() + 1;
    expressionAttributeValues.put(":nv", AttributeValue.builder()
          .n(Long.toString(newVersion))
          .build());
    updateExpressionList.add(String.format("%s = :nv",
          TherapistRecord.VERSION_ATTRIBUTE_NAME));
    String updateExpression = String.format("SET %s", String.join(",", updateExpressionList));
    expressionAttributeValues.put(":v", AttributeValue.builder()
          .n(therapistRecord.getVersion().toString())
          .build());
    dynamodb.updateItem(UpdateItemRequest.builder()
          .tableName(tableName)
          .key(toKeyRecord(therapistId))
          .updateExpression(updateExpression)
          .expressionAttributeValues(expressionAttributeValues)
          .conditionExpression(String.format("%s = :v", TherapistRecord.VERSION_ATTRIBUTE_NAME))
          .build());
    return modelMapper.map(therapistRecord, Therapist.class);
  }

  private TherapistRecord loadTherapist(final String therapistId) {
    Map<String, AttributeValue> therapistMap = dynamodb.getItem(GetItemRequest.builder()
          .tableName(tableName)
          .consistentRead(Boolean.TRUE)
          .key(toKeyRecord(therapistId))
          .build()).item();
    if (therapistMap.isEmpty()) {
      throw new IllegalArgumentException(String.format("therapistId:%s not found", therapistId));
    }
    return new TherapistRecord(therapistMap);
  }

  private Map<String, AttributeValue> toKeyRecord(final String therapistId) {
    return TherapistRecord.builder()
          .userId("Raj")
          .therapistId(therapistId)
          .build()
          .toAttributeMap();
  }
}
