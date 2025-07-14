package org.wso2.openbanking.nextgenpsd2.extensions.model.generated;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.wso2.openbanking.nextgenpsd2.extensions.model.generated.SuccessResponsePopulateConsentAuthorizeScreenDataConsentData;
import org.wso2.openbanking.nextgenpsd2.extensions.model.generated.SuccessResponsePopulateConsentAuthorizeScreenDataConsumerDataInner;
import javax.validation.constraints.*;
import javax.validation.Valid;

import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;



@JsonTypeName("SuccessResponsePopulateConsentAuthorizeScreenData")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-14T12:25:16.039700400+05:30[Asia/Colombo]", comments = "Generator version: 7.12.0")
public class SuccessResponsePopulateConsentAuthorizeScreenData   {
  private SuccessResponsePopulateConsentAuthorizeScreenDataConsentData consentData;
  private @Valid List<@Valid SuccessResponsePopulateConsentAuthorizeScreenDataConsumerDataInner> consumerData = new ArrayList<>();

  public SuccessResponsePopulateConsentAuthorizeScreenData() {
  }

  /**
   **/
  public SuccessResponsePopulateConsentAuthorizeScreenData consentData(SuccessResponsePopulateConsentAuthorizeScreenDataConsentData consentData) {
    this.consentData = consentData;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("consentData")
  @Valid public SuccessResponsePopulateConsentAuthorizeScreenDataConsentData getConsentData() {
    return consentData;
  }

  @JsonProperty("consentData")
  public void setConsentData(SuccessResponsePopulateConsentAuthorizeScreenDataConsentData consentData) {
    this.consentData = consentData;
  }

  /**
   * List of all user accounts/resources selectable in the UI
   **/
  public SuccessResponsePopulateConsentAuthorizeScreenData consumerData(List<@Valid SuccessResponsePopulateConsentAuthorizeScreenDataConsumerDataInner> consumerData) {
    this.consumerData = consumerData;
    return this;
  }

  
  @ApiModelProperty(value = "List of all user accounts/resources selectable in the UI")
  @JsonProperty("consumerData")
  @Valid public List<@Valid SuccessResponsePopulateConsentAuthorizeScreenDataConsumerDataInner> getConsumerData() {
    return consumerData;
  }

  @JsonProperty("consumerData")
  public void setConsumerData(List<@Valid SuccessResponsePopulateConsentAuthorizeScreenDataConsumerDataInner> consumerData) {
    this.consumerData = consumerData;
  }

  public SuccessResponsePopulateConsentAuthorizeScreenData addConsumerDataItem(SuccessResponsePopulateConsentAuthorizeScreenDataConsumerDataInner consumerDataItem) {
    if (this.consumerData == null) {
      this.consumerData = new ArrayList<>();
    }

    this.consumerData.add(consumerDataItem);
    return this;
  }

  public SuccessResponsePopulateConsentAuthorizeScreenData removeConsumerDataItem(SuccessResponsePopulateConsentAuthorizeScreenDataConsumerDataInner consumerDataItem) {
    if (consumerDataItem != null && this.consumerData != null) {
      this.consumerData.remove(consumerDataItem);
    }

    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SuccessResponsePopulateConsentAuthorizeScreenData successResponsePopulateConsentAuthorizeScreenData = (SuccessResponsePopulateConsentAuthorizeScreenData) o;
    return Objects.equals(this.consentData, successResponsePopulateConsentAuthorizeScreenData.consentData) &&
        Objects.equals(this.consumerData, successResponsePopulateConsentAuthorizeScreenData.consumerData);
  }

  @Override
  public int hashCode() {
    return Objects.hash(consentData, consumerData);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SuccessResponsePopulateConsentAuthorizeScreenData {\n");
    
    sb.append("    consentData: ").append(toIndentedString(consentData)).append("\n");
    sb.append("    consumerData: ").append(toIndentedString(consumerData)).append("\n");
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

