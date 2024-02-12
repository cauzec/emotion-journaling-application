package software.amazon.serverless.apprepo.api.impl;

import com.sun.tools.javac.resources.CompilerProperties;
import io.swagger.api.TherapistApi;
import io.swagger.model.Therapist;
import io.swagger.model.TherapistList;
import io.swagger.model.TherapistSummary;
import io.swagger.model.BadRequestException;
import io.swagger.model.ConflictException;
import io.swagger.model.InternalServerErrorException;
import io.swagger.model.NotFoundException;
import io.swagger.model.TooManyRequestsException;
import io.swagger.model.UnauthorizedException;

import Notes.*;

import java.time.Clock;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import javax.inject.Inject;
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

import software.amazon.serverless.apprepo.api.exception.BadRequestApiException;
import software.amazon.serverless.apprepo.api.exception.ConflictApiException;
import software.amazon.serverless.apprepo.api.exception.NotFoundApiException;
import software.amazon.serverless.apprepo.api.exception.InternalServerApiException;
import software.amazon.serverless.apprepo.api.impl.pagination.InvalidTokenException;
import software.amazon.serverless.apprepo.api.impl.pagination.TokenSerializer;
import software.amazon.serverless.apprepo.container.config.ConfigProvider;

/**
 * TherapistService implements {@link TherapistApi}.
 *
 * <p>It interacts with DynamoDB when processing each API.
 */
@Slf4j
@RequiredArgsConstructor
public class TherapistService implements TherapistApi {
  static final Integer DEFAULT_LIST_THERAPIST_LIMIT = 10;
  private final TokenSerializer<Map<String, AttributeValue>> paginationTokenSerializer;
  private final DynamoDbClient dynamodb;
  // DynamoDBClient creates a client for the dynanmoDB service provided by AWS which already has all the required format on
  // how does dynamoDB service take input from this code.
  // Hence, dynamoDBClient helps us interact with dynamoDB in an interface that is easily understandable by us as a client.
  // Similarly, every service provider for the ease of integrating with the service provides a similar client.
  private final ModelMapper modelMapper;
  // We know that an API is just a handshake contract that has 3 well-defined things i.e., input, processing, and output.
  // Now the inputs and outputs in these APIs are data containers whose definition has to be represented in some class
  // and a collection of such classes is called a model.
  // These models are used in the MVC (Model-View-Controller) Architecture.
  // This architecture divides our code into 3 parts mentioned in the name.
  // Model as we discussed earlier contains the data containers that transfer data between client and service as i/p and o/p.
  // View is used to define objects when they are viewed on a UI.
  // Controller is the component that acts as an intermediate between model and view and processes all the data logic.
  // To talk in simpler terms, we can simplify the above 3 into API(model), client(view) and processing in the server behind the API(controller)
  // Now, these 3 components have different properties and are not directly compatible with each other,
  // but to keep a workflow in which a client request goes to API and then the API request goes to the controller,
  // we need a way to make the 3 different models from these 3 different components compatible with each other.
  // Hence we use the ModelMapper.
  // ModelMapper is a library that allows the user to translate different models with one line of code
  // if the user follows a certain format while creating these model classes.
  // A newer version of MVC states that we should create another model for databases also.
  //
  // In this code, we haven't created a client side model and we have only used model(API), controller(server-side processor) and database
  // In these 3 components used in our codes, the models used by API and controller both are of type "therapist"
  // and a model used for database is specified in "TherapistRecord"
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
          // Configurations are variables that a system needs to decide its behavior
          // and these variables can be modified over time to change the system behavior the way we want
          // For eg., configprovider for the purpose of hitting a zerodha api can have things like endpoint url, timeout while hitting api, refresh rate etc.
          // Configuration is generally stored in a key-value pair
          // and aws has a service the generally does this very efficiently which is known as SSM(System Manager Agent)
          // SSM has many uses, configprovider is one of them. In this we give keys in the form of strings and it gives values in return
          // which can be integer, strings or complex JSON values. For eg., Key=zerodhaAPITimeout, Value=300
          // Here, they have used configProvider to get the current time zone of the system. Now as lambda boots, it takes all these configs
          // and its very time-consuming so configProvider has a built-in cache that stores the value their for fast access.
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
    // Now as we saw earlier, we use therapist type model for API and controller and TherapistRecord for databases
    // and in the above code, we are translating our therapist model of type API to TherapistRecord model for our database.

    String id = UUID.randomUUID().toString();
    // Creates a unique ID for the variable therapistId
    therapistRecord.setTherapistId(id);
    therapistRecord.setCreatedAt(Instant.now(clock));
    therapistRecord.setVersion(1L);
    therapistRecord.setUserId("Raj");

    try {
      dynamodb.putItem(
      // Here, putItem is an API call to dynamoDb that lets you put an item in your dynamo database.
      // Similarly, you can just use Ctrl+Space after the (.) to look at other API calls offered by the dynamoDb SDK.
        PutItemRequest
        // In the SDKs(service clients) offered by AWS, 95% of the time, SDK.(any method) is an individual API call
        // and it will take only 1 object as input whose name will be <API Name>Request
        // and will always return 1 object whose name will be <API Name>Response.
            .builder()
            // Builder method is used when the construction of an object
            // (here, PutItemRequest) is very complex and it simplifies it by using chaining with the (.) operator
            // and putting all the code in one line.
            .tableName(tableName)
            .item(therapistRecord.toAttributeMap())
            .conditionExpression(
                  String.format("attribute_not_exists(%s) AND attribute_not_exists(%s)",
                        TherapistRecord.USER_ID_ATTRIBUTE_NAME,
                        TherapistRecord.THERAPIST_ID_ATTRIBUTE_NAME))
            .build());
            // And then we pass different parameters required in the builder() to construct PutItemRequest,
            // now as we are done with that, we have all the parameters but as an object of the builder() function
            // but we want it as the object of the PutItemRequest function. Hence, in the end, we use build() function
            // which takes our parameters as builder object and converts it to PutItemRequest type.
    } catch (ConditionalCheckFailedException e) {
      throw new ConflictApiException(new ConflictException()
            .errorCode("TherapistAlreadyExist")
            .message(String.format("Therapist %s already exists.",
            therapist.getTherapistId())));
    }
    
    return modelMapper.map(therapistRecord, Therapist.class);
    // Now we have to return a therapist type model for the API response and we used database model of type TherapistRecord
    // so we again change it back to therapist type from TherapistRecord for API as we are returning a Therapist type object in this method.
  }

  public void deleteTherapist(final String therapistId) {
    log.info("Deleting therapist {}", therapistId);
    TherapistRecord therapistRecord = loadTherapist(therapistId);
    // Even thought in the dynamodb.deleteItem, we just need the tablename and a key to delete a therapist entry
    // we still loadTherapist because we have to check if the therapist exists or not and loadTherapist function will throw and error if it does not.
    Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
    expressionAttributeValues.put(":v", AttributeValue.builder()
          .n(therapistRecord.getVersion().toString())
          .build());
    // This takes out the version of the therapistId loaded in the TherapistRecord format.
    dynamodb.deleteItem(DeleteItemRequest.builder()
          .tableName(tableName)
          .key(toKeyRecord(therapistId))
          .conditionExpression(String.format("%s = :v", TherapistRecord.VERSION_ATTRIBUTE_NAME))
          .expressionAttributeValues(expressionAttributeValues)
            // Now after taking out the version of the therapist to be deleted, this compares it to version of the current TherapistRecord
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
      } catch (InvalidTokenException e) {
        throw new BadRequestApiException(new BadRequestException()
              .errorCode("InvalidRequest")
              .message(String.format("NextToken %s is invalid.", nextToken)));
      }
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
          } catch (InvalidTokenException e) {
            throw new BadRequestApiException(new BadRequestException()
                  .errorCode("InvalidRequest")
                  .message(String.format("NextToken %s is invalid.", nextToken)));
          }
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
          } catch (InvalidTokenException e) {
            throw new BadRequestApiException(new BadRequestException()
                  .errorCode("InvalidRequest")
                  .message(String.format("NextToken %s is invalid.", nextToken)));
          }
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

  public Therapist updateTherapist(final Therapist therapist,
                                       final String therapistId) {
    log.info("Updating therapist {} with input {}", therapistId, therapist);
    if (therapist.getTherapistName() == null
          && therapist.getTherapistMob() == null
          && therapist.getTherapistType() == null
          && therapist.getTherapistArea() == null) {
      throw new BadRequestApiException(new BadRequestException()
            .errorCode("InvalidRequest")
            .message("No update is present."));
    }

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
      throw new NotFoundApiException(new NotFoundException()
            .errorCode("TherapistNotFound")
            .message(String.format("Therapist %s can not be found.", therapistId)));
    }
    return new TherapistRecord(therapistMap);
    // TherapistRecord is a function thats a constructor to the TherapistRecord.class
    // which takes the dynamoDb response of the type therapistMap and converts it to TherapistRecord
  }

  private Map<String, AttributeValue> toKeyRecord(final String therapistId) {
    return TherapistRecord.builder()
          .userId("Raj")
          .therapistId(therapistId)
          .build()
          .toAttributeMap();
  }
}
