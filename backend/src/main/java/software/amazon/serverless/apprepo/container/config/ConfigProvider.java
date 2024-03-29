package software.amazon.serverless.apprepo.container.config;

import java.time.Duration;

/**
 * Provides configuration value based on name.
 */
public interface ConfigProvider {
  /**
   * Get Therapist DynamoDB Table name.
   *
   * @return String
   */
  String getTherapistTableName();

  /**
   * Get KMS key id.
   *
   * @return key id String.
   */
  String getKmsKeyId();

  /**
   * Get pagination token ttl.
   *
   * @return ttl duration.
   */
  Duration getPaginationTokenTtl();
}
