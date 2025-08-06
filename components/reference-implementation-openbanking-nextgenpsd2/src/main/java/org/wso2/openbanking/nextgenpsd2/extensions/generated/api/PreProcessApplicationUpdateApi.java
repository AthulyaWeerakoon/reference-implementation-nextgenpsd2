package org.wso2.openbanking.nextgenpsd2.extensions.generated.api;

import io.swagger.annotations.*;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.ErrorResponse;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.mcr.AppUpdateProcessRequestBody;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.mcr.Response200ForApplicationUpdate;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.mcr.SuccessResponseApplicationUpdate;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.mcr.SuccessResponseApplicationUpdateData;

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
@Path("/pre-process-application-update")
@Api(description = "the pre-process-application-update API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-06-02T10:40:43.095685+05:30[Asia/Colombo]", comments = "Generator version: 7.13.0")
public class PreProcessApplicationUpdateApi {

    @POST
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "handle pre validations & changes to the consumer application update", notes = "", response = Response200ForApplicationUpdate.class, authorizations = {
        @Authorization(value = "OAuth2", scopes = {
             }),
        
        @Authorization(value = "BasicAuth")
         }, tags={ "Application" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Ok", response = Response200ForApplicationUpdate.class),
        @ApiResponse(code = 400, message = "Bad Request", response = ErrorResponse.class),
        @ApiResponse(code = 500, message = "Server Error", response = ErrorResponse.class)
    })
    public Response preProcessApplicationUpdatePost(@Valid @NotNull AppUpdateProcessRequestBody appUpdateProcessRequestBody) {
        SuccessResponseApplicationUpdateData successResponseData = new SuccessResponseApplicationUpdateData();
        Map<String, String> additionalData = new HashMap<>();
        additionalData.put("test1", "test1");
        successResponseData.setAdditionalAppData(additionalData);

        SuccessResponseApplicationUpdate successResponse = new SuccessResponseApplicationUpdate();
        successResponse.setStatus(SuccessResponseApplicationUpdate.StatusEnum.SUCCESS);
        successResponse.setResponseId(appUpdateProcessRequestBody.getRequestId());
        successResponse.setData(successResponseData);
        return Response.ok().entity(successResponse).build();
    }
}
