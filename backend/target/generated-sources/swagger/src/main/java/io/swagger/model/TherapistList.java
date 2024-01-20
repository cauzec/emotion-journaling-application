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
  private @Valid List<TherapistSummary> therapists = new ArrayList<TherapistSummary>();
  private @Valid String nextToken = null;

  /**
   **/
  public TherapistList therapists(List<TherapistSummary> therapists) {
    this.therapists = therapists;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("therapists")

  public List<TherapistSummary> getTherapists() {
    return therapists;
  }
  public void setTherapists(List<TherapistSummary> therapists) {
    this.therapists = therapists;
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
    return Objects.equals(therapists, therapistList.therapists) &&
        Objects.equals(nextToken, therapistList.nextToken);
  }

  @Override
  public int hashCode() {
    return Objects.hash(therapists, nextToken);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TherapistList {\n");
    
    sb.append("    therapists: ").append(toIndentedString(therapists)).append("\n");
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
