package org.wso2.openbanking.nextgenpsd2.extensions.generated.model;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;



@JsonTypeName("ErrorMapperRequestBody")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-05-05T12:44:23.299724+05:30[Asia/Colombo]", comments = "Generator version: 7.12.0")
public class ErrorMapperRequestBody   {
  private String requestId;
  private ErrorMapperData data;

  public ErrorMapperRequestBody() {
  }

  /**
   * A unique correlation identifier
   **/
  public ErrorMapperRequestBody requestId(String requestId) {
    this.requestId = requestId;
    return this;
  }

  
  @ApiModelProperty(example = "Ec1wMjmiG8", value = "A unique correlation identifier")
  @JsonProperty("requestId")
  public String getRequestId() {
    return requestId;
  }

  @JsonProperty("requestId")
  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  /**
   **/
  public ErrorMapperRequestBody data(ErrorMapperData data) {
    this.data = data;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("data")
  @Valid public ErrorMapperData getData() {
    return data;
  }

  @JsonProperty("data")
  public void setData(ErrorMapperData data) {
    this.data = data;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ErrorMapperRequestBody errorMapperRequestBody = (ErrorMapperRequestBody) o;
    return Objects.equals(this.requestId, errorMapperRequestBody.requestId) &&
        Objects.equals(this.data, errorMapperRequestBody.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId, data);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ErrorMapperRequestBody {\n");
    
    sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
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

