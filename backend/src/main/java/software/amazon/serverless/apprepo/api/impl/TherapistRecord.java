package software.amazon.serverless.apprepo.api.impl;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

/**
 * TherapistRecord represents a record in Therapist DynamoDB table.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TherapistRecord {
  public static final String USER_ID_ATTRIBUTE_NAME = "userId";
  public static final String THERAPIST_ID_ATTRIBUTE_NAME = "therapistId";
  public static final String CREATED_AT_ATTRIBUTE_NAME = "createdAt";
  public static final String THERAPIST_NAME_ATTRIBUTE_NAME = "therapistName";
  public static final String THERAPIST_AREA_ATTRIBUTE_NAME = "therapistArea";
  public static final String THERAPIST_TYPE_ATTRIBUTE_NAME = "therapistType";
  public static final String VERSION_ATTRIBUTE_NAME = "version";
  public static final String THERAPIST_MOBILE_ATTRIBUTE_NAME = "therapistMob";

  private String userId;
  private String therapistId;
  private Instant createdAt;
  private String therapistName;
  private String therapistArea;
  private String therapistType;
  private Long version;
  private Long therapistMob;

  /**
   * Construct the record from a map of DynamoDB {@link AttributeValue}.
   *
   * @param record a map of DynamoDB {@link AttributeValue}
   */
  public TherapistRecord(final Map<String, AttributeValue> record) {
    if (record.containsKey(USER_ID_ATTRIBUTE_NAME)) {
      this.userId = record.get(USER_ID_ATTRIBUTE_NAME).s();
    }
    if (record.containsKey(THERAPIST_ID_ATTRIBUTE_NAME)) {
      this.therapistId = record.get(THERAPIST_ID_ATTRIBUTE_NAME).s();
    }
    if (record.containsKey(THERAPIST_NAME_ATTRIBUTE_NAME)) {
      this.therapistName = record.get(THERAPIST_NAME_ATTRIBUTE_NAME).s();
    }
    if (record.containsKey(CREATED_AT_ATTRIBUTE_NAME)) {
      this.createdAt = Instant.parse(record.get(CREATED_AT_ATTRIBUTE_NAME).s());
    }
    if (record.containsKey(THERAPIST_TYPE_ATTRIBUTE_NAME)) {
      this.therapistType = record.get(THERAPIST_TYPE_ATTRIBUTE_NAME).s();
    }
    if (record.containsKey(THERAPIST_AREA_ATTRIBUTE_NAME)) {
      this.therapistArea = record.get(THERAPIST_AREA_ATTRIBUTE_NAME).s();
    }
    if (record.containsKey(THERAPIST_MOBILE_ATTRIBUTE_NAME)) {
      this.therapistMob = Long.parseLong(record.get(THERAPIST_MOBILE_ATTRIBUTE_NAME).n());
    }
    if (record.containsKey(VERSION_ATTRIBUTE_NAME)) {
      this.version = Long.parseLong(record.get(VERSION_ATTRIBUTE_NAME).n());
    }
  }

  /**
   * Convert TherapistRecord to a map of DynamoDB {@link AttributeValue}.
   *
   * @return a map of DynamoDB {@link AttributeValue}
   */
  public Map<String, AttributeValue> toAttributeMap() {
    Map<String, AttributeValue> therapistMap = new HashMap<>();
    if (userId != null) {
      therapistMap.put(USER_ID_ATTRIBUTE_NAME, AttributeValue.builder().s(userId).build());
    }
    if (therapistId != null) {
      therapistMap.put(THERAPIST_ID_ATTRIBUTE_NAME,
            AttributeValue.builder().s(therapistId).build());
    }
    if (therapistName != null) {
      therapistMap.put(THERAPIST_NAME_ATTRIBUTE_NAME,
            AttributeValue.builder().s(therapistName).build());
    }
    if (createdAt != null) {
      therapistMap.put(CREATED_AT_ATTRIBUTE_NAME,
            AttributeValue.builder().s(createdAt.toString()).build());
    }
    if (therapistType != null) {
      therapistMap.put(THERAPIST_TYPE_ATTRIBUTE_NAME,
            AttributeValue.builder().s(therapistType).build());
    }
    if (therapistArea != null) {
      therapistMap.put(THERAPIST_AREA_ATTRIBUTE_NAME,
            AttributeValue.builder().s(therapistArea).build());
    }
    if (therapistMob != null) {
      therapistMap.put(THERAPIST_MOBILE_ATTRIBUTE_NAME,
            AttributeValue.builder().n(therapistMob.toString()).build());
    }
    if (version != null) {
      therapistMap.put(VERSION_ATTRIBUTE_NAME,
            AttributeValue.builder().n(version.toString()).build());
    }
    return therapistMap;
  }
}
