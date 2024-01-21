package io.swagger.model;

import io.swagger.model.TherapistSummary;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class TherapistList   {
  private @Valid List<TherapistSummary> therapist = new ArrayList<TherapistSummary>();
  private @Valid String nextToken = null;

  /**
   **/
  public TherapistList therapist(List<TherapistSummary> therapist) {
    this.therapist = therapist;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("therapist")

  public List<TherapistSummary> getTherapist() {
    return therapist;
  }
  public void setTherapist(List<TherapistSummary> therapist) {
    this.therapist = therapist;
  }

  /**
   **/
  public TherapistList nextToken(String nextToken) {
    this.nextToken = nextToken;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("nextToken")

  public String getNextToken() {
    return nextToken;
  }
  public void setNextToken(String nextToken) {
    this.nextToken = nextToken;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TherapistList therapistList = (TherapistList) o;
    return Objects.equals(therapist, therapistList.therapist) &&
        Objects.equals(nextToken, therapistList.nextToken);
  }

  @Override
  public int hashCode() {
    return Objects.hash(therapist, nextToken);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TherapistList {\n");
    
    sb.append("    therapist: ").append(toIndentedString(therapist)).append("\n");
    sb.append("    nextToken: ").append(toIndentedString(nextToken)).append("\n");
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
