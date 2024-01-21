package io.swagger.model;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * Represents a therapist entry
 **/
import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
@Schema(description = "Represents a therapist entry")

public class Therapist   {
  private @Valid String therapistId = null;
  private @Valid String therapistName = null;
  private @Valid String creationTime = null;
  private @Valid String therapistMob = null;
  private @Valid String therapistArea = null;
  private @Valid String therapistType = null;

  /**
   * Unique ID of a therapist
   **/
  public Therapist therapistId(String therapistId) {
    this.therapistId = therapistId;
    return this;
  }

  
  @ApiModelProperty(value = "Unique ID of a therapist")
  @JsonProperty("therapistId")

  public String getTherapistId() {
    return therapistId;
  }
  public void setTherapistId(String therapistId) {
    this.therapistId = therapistId;
  }

  /**
   * Name of the therapist
   **/
  public Therapist therapistName(String therapistName) {
    this.therapistName = therapistName;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "Name of the therapist")
  @JsonProperty("therapistName")
  @NotNull

  public String getTherapistName() {
    return therapistName;
  }
  public void setTherapistName(String therapistName) {
    this.therapistName = therapistName;
  }

  /**
   **/
  public Therapist creationTime(String creationTime) {
    this.creationTime = creationTime;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("creationTime")

  public String getCreationTime() {
    return creationTime;
  }
  public void setCreationTime(String creationTime) {
    this.creationTime = creationTime;
  }

  /**
   * Mobile number of the therapist
   **/
  public Therapist therapistMob(String therapistMob) {
    this.therapistMob = therapistMob;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "Mobile number of the therapist")
  @JsonProperty("therapistMob")
  @NotNull

  public String getTherapistMob() {
    return therapistMob;
  }
  public void setTherapistMob(String therapistMob) {
    this.therapistMob = therapistMob;
  }

  /**
   * Area of the place from where the therapists operates
   **/
  public Therapist therapistArea(String therapistArea) {
    this.therapistArea = therapistArea;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "Area of the place from where the therapists operates")
  @JsonProperty("therapistArea")
  @NotNull

  public String getTherapistArea() {
    return therapistArea;
  }
  public void setTherapistArea(String therapistArea) {
    this.therapistArea = therapistArea;
  }

  /**
   * Type of the therapist
   **/
  public Therapist therapistType(String therapistType) {
    this.therapistType = therapistType;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "Type of the therapist")
  @JsonProperty("therapistType")
  @NotNull

  public String getTherapistType() {
    return therapistType;
  }
  public void setTherapistType(String therapistType) {
    this.therapistType = therapistType;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Therapist therapist = (Therapist) o;
    return Objects.equals(therapistId, therapist.therapistId) &&
        Objects.equals(therapistName, therapist.therapistName) &&
        Objects.equals(creationTime, therapist.creationTime) &&
        Objects.equals(therapistMob, therapist.therapistMob) &&
        Objects.equals(therapistArea, therapist.therapistArea) &&
        Objects.equals(therapistType, therapist.therapistType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(therapistId, therapistName, creationTime, therapistMob, therapistArea, therapistType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Therapist {\n");
    
    sb.append("    therapistId: ").append(toIndentedString(therapistId)).append("\n");
    sb.append("    therapistName: ").append(toIndentedString(therapistName)).append("\n");
    sb.append("    creationTime: ").append(toIndentedString(creationTime)).append("\n");
    sb.append("    therapistMob: ").append(toIndentedString(therapistMob)).append("\n");
    sb.append("    therapistArea: ").append(toIndentedString(therapistArea)).append("\n");
    sb.append("    therapistType: ").append(toIndentedString(therapistType)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
