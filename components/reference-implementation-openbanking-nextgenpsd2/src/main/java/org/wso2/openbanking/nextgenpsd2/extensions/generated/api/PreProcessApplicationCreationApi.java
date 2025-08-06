package org.wso2.openbanking.nextgenpsd2.extensions.generated.api;

import io.swagger.annotations.*;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.ErrorResponse;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.mcr.AppCreateProcessRequestBody;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.mcr.Response200ForApplicationCreation;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.mcr.SuccessResponseApplicationCreation;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.mcr.SuccessResponseApplicationCreationData;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
* Represents a collection of functions to interact with the API endpoints.
*/
@Path("/pre-process-application-creation")
@Api(description = "the pre-process-application-creation API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-06-02T10:40:43.095685+05:30[Asia/Colombo]", comments = "Generator version: 7.13.0")
public class PreProcessApplicationCreationApi {

    @POST
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "handle pre validations & changes to the consumer application creation", notes = "", response = Response200ForApplicationCreation.class, authorizations = {
        @Authorization(value = "OAuth2", scopes = {
             }),
        
        @Authorization(value = "BasicAuth")
         }, tags={ "Application" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Ok", response = Response200ForApplicationCreation.class),
        @ApiResponse(code = 400, message = "Bad Request", response = ErrorResponse.class),
        @ApiResponse(code = 500, message = "Server Error", response = ErrorResponse.class)
    })
    public Response preProcessApplicationCreationPost(@Valid @NotNull AppCreateProcessRequestBody appCreateProcessRequestBody) {
        SuccessResponseApplicationCreationData successResponseData = new SuccessResponseApplicationCreationData();
        successResponseData.setClientId("testClientIdToolkit");
        Map<String, String> additionalData = new HashMap<>();
        additionalData.put("test", "test");
        successResponseData.setAdditionalAppData(additionalData);

        SuccessResponseApplicationCreation successResponse = new SuccessResponseApplicationCreation();
        successResponse.setStatus(SuccessResponseApplicationCreation.StatusEnum.SUCCESS);
        successResponse.setResponseId(appCreateProcessRequestBody.getRequestId());
        successResponse.setData(successResponseData);
        return Response.ok().entity(successResponse).build();
    }
}
