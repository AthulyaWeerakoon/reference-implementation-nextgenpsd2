package org.wso2.openbanking.nextgenpsd2.extensions.generated.model.mcr;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Defines the context data related to the application registration.
 **/
@ApiModel(description = "Defines the context data related to the application registration.")
@JsonTypeName("AppCreateProcessData")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-06-02T10:40:43.095685+05:30[Asia/Colombo]", comments = "Generator version: 7.13.0")
public class AppCreateProcessData   {
  private Object appData;
  private Object additionalProperties;

  public AppCreateProcessData() {
  }

  @JsonCreator
  public AppCreateProcessData(
    @JsonProperty(required = true, value = "appData") Object appData,
    @JsonProperty(required = true, value = "additionalProperties") Object additionalProperties
  ) {
    this.appData = appData;
    this.additionalProperties = additionalProperties;
  }

  /**
   * OAuth Application Data. Mandatory for pre-process-application-creation.
   **/
  public AppCreateProcessData appData(Object appData) {
    this.appData = appData;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "OAuth Application Data. Mandatory for pre-process-application-creation.")
  @JsonProperty(required = true, value = "appData")
  @NotNull public Object getAppData() {
    return appData;
  }

  @JsonProperty(required = true, value = "appData")
  public void setAppData(Object appData) {
    this.appData = appData;
  }

  /**
   * Additional properties reterived from devportal UI. Mandatory for pre-process-application-creation.
   **/
  public AppCreateProcessData additionalProperties(Object additionalProperties) {
    this.additionalProperties = additionalProperties;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "Additional properties reterived from devportal UI. Mandatory for pre-process-application-creation.")
  @JsonProperty(required = true, value = "additionalProperties")
  @NotNull public Object getAdditionalProperties() {
    return additionalProperties;
  }

  @JsonProperty(required = true, value = "additionalProperties")
  public void setAdditionalProperties(Object additionalProperties) {
    this.additionalProperties = additionalProperties;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AppCreateProcessData appCreateProcessData = (AppCreateProcessData) o;
    return Objects.equals(this.appData, appCreateProcessData.appData) &&
        Objects.equals(this.additionalProperties, appCreateProcessData.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(appData, additionalProperties);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AppCreateProcessData {\n");
    
    sb.append("    appData: ").append(toIndentedString(appData)).append("\n");
    sb.append("    additionalProperties: ").append(toIndentedString(additionalProperties)).append("\n");
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

