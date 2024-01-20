package io.swagger.model;

import java.math.BigDecimal;
import javax.validation.constraints.*;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class TherapistSummary   {
  private @Valid String therapistId = null;
  private @Valid String therapistName = null;
  private @Valid BigDecimal therapistMob = null;
  private @Valid String therapistArea = null;
  private @Valid String therapistType = null;
  private @Valid String creationTime = null;

  /**
   * Unique ID of a therapist
   **/
  public TherapistSummary therapistId(String therapistId) {
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
  public TherapistSummary therapistName(String therapistName) {
    this.therapistName = therapistName;
    return this;
  }

  
  @ApiModelProperty(value = "Name of the therapist")
  @JsonProperty("therapistName")

  public String getTherapistName() {
    return therapistName;
  }
  public void setTherapistName(String therapistName) {
    this.therapistName = therapistName;
  }

  /**
   * Mobile number of the therapist
   **/
  public TherapistSummary therapistMob(BigDecimal therapistMob) {
    this.therapistMob = therapistMob;
    return this;
  }

  
  @ApiModelProperty(value = "Mobile number of the therapist")
  @JsonProperty("therapistMob")

  public BigDecimal getTherapistMob() {
    return therapistMob;
  }
  public void setTherapistMob(BigDecimal therapistMob) {
    this.therapistMob = therapistMob;
  }

  /**
   * Area of the place from where the therapists operates
   **/
  public TherapistSummary therapistArea(String therapistArea) {
    this.therapistArea = therapistArea;
    return this;
  }

  
  @ApiModelProperty(value = "Area of the place from where the therapists operates")
  @JsonProperty("therapistArea")

  public String getTherapistArea() {
    return therapistArea;
  }
  public void setTherapistArea(String therapistArea) {
    this.therapistArea = therapistArea;
  }

  /**
   * Type of the therapist
   **/
  public TherapistSummary therapistType(String therapistType) {
    this.therapistType = therapistType;
    return this;
  }

  
  @ApiModelProperty(value = "Type of the therapist")
  @JsonProperty("therapistType")

  public String getTherapistType() {
    return therapistType;
  }
  public void setTherapistType(String therapistType) {
    this.therapistType = therapistType;
  }

  /**
   **/
  public TherapistSummary creationTime(String creationTime) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TherapistSummary therapistSummary = (TherapistSummary) o;
    return Objects.equals(therapistId, therapistSummary.therapistId) &&
        Objects.equals(therapistName, therapistSummary.therapistName) &&
        Objects.equals(therapistMob, therapistSummary.therapistMob) &&
        Objects.equals(therapistArea, therapistSummary.therapistArea) &&
        Objects.equals(therapistType, therapistSummary.therapistType) &&
        Objects.equals(creationTime, therapistSummary.creationTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(therapistId, therapistName, therapistMob, therapistArea, therapistType, creationTime);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TherapistSummary {\n");
    
    sb.append("    therapistId: ").append(toIndentedString(therapistId)).append("\n");
    sb.append("    therapistName: ").append(toIndentedString(therapistName)).append("\n");
    sb.append("    therapistMob: ").append(toIndentedString(therapistMob)).append("\n");
    sb.append("    therapistArea: ").append(toIndentedString(therapistArea)).append("\n");
    sb.append("    therapistType: ").append(toIndentedString(therapistType)).append("\n");
    sb.append("    creationTime: ").append(toIndentedString(creationTime)).append("\n");
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
