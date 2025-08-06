package org.wso2.openbanking.nextgenpsd2.extensions.generated.model;

import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;



@JsonTypeName("SuccessResponseForConsentSearchData")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-05-05T12:44:23.299724+05:30[Asia/Colombo]", comments = "Generator version: 7.12.0")
public class SuccessResponseForConsentSearchData   {
  private Object searchResult;

  public SuccessResponseForConsentSearchData() {
  }

  /**
   * Enriched search result
   **/
  public SuccessResponseForConsentSearchData searchResult(Object searchResult) {
    this.searchResult = searchResult;
    return this;
  }

  
  @ApiModelProperty(value = "Enriched search result")
  @JsonProperty("searchResult")
  public Object getSearchResult() {
    return searchResult;
  }

  @JsonProperty("searchResult")
  public void setSearchResult(Object searchResult) {
    this.searchResult = searchResult;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SuccessResponseForConsentSearchData successResponseForConsentSearchData = (SuccessResponseForConsentSearchData) o;
    return Objects.equals(this.searchResult, successResponseForConsentSearchData.searchResult);
  }

  @Override
  public int hashCode() {
    return Objects.hash(searchResult);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SuccessResponseForConsentSearchData {\n");
    
    sb.append("    searchResult: ").append(toIndentedString(searchResult)).append("\n");
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

