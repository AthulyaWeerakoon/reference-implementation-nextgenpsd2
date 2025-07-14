package org.wso2.openbanking.nextgenpsd2.extensions.model.generated;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
import javax.validation.Valid;

import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;



@JsonTypeName("SuccessResponsePopulateConsentAuthorizeScreenData_consumerData_inner")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-14T12:25:16.039700400+05:30[Asia/Colombo]", comments = "Generator version: 7.12.0")
public class SuccessResponsePopulateConsentAuthorizeScreenDataConsumerDataInner   {
  private Boolean selected;
  private String displayName;

  public SuccessResponsePopulateConsentAuthorizeScreenDataConsumerDataInner() {
  }

  @JsonCreator
  public SuccessResponsePopulateConsentAuthorizeScreenDataConsumerDataInner(
    @JsonProperty(required = true, value = "selected") Boolean selected,
    @JsonProperty(required = true, value = "displayName") String displayName
  ) {
    this.selected = selected;
    this.displayName = displayName;
  }

  /**
   * Whether the account is selected by default
   **/
  public SuccessResponsePopulateConsentAuthorizeScreenDataConsumerDataInner selected(Boolean selected) {
    this.selected = selected;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "Whether the account is selected by default")
  @JsonProperty(required = true, value = "selected")
  @NotNull public Boolean getSelected() {
    return selected;
  }

  @JsonProperty(required = true, value = "selected")
  public void setSelected(Boolean selected) {
    this.selected = selected;
  }

  /**
   * Account display name
   **/
  public SuccessResponsePopulateConsentAuthorizeScreenDataConsumerDataInner displayName(String displayName) {
    this.displayName = displayName;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "Account display name")
  @JsonProperty(required = true, value = "displayName")
  @NotNull public String getDisplayName() {
    return displayName;
  }

  @JsonProperty(required = true, value = "displayName")
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SuccessResponsePopulateConsentAuthorizeScreenDataConsumerDataInner successResponsePopulateConsentAuthorizeScreenDataConsumerDataInner = (SuccessResponsePopulateConsentAuthorizeScreenDataConsumerDataInner) o;
    return Objects.equals(this.selected, successResponsePopulateConsentAuthorizeScreenDataConsumerDataInner.selected) &&
        Objects.equals(this.displayName, successResponsePopulateConsentAuthorizeScreenDataConsumerDataInner.displayName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(selected, displayName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SuccessResponsePopulateConsentAuthorizeScreenDataConsumerDataInner {\n");
    
    sb.append("    selected: ").append(toIndentedString(selected)).append("\n");
    sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }


}

