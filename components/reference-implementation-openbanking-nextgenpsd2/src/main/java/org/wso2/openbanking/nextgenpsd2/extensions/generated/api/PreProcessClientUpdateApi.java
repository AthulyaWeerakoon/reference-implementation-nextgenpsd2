package org.wso2.openbanking.nextgenpsd2.extensions.generated.api;

import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.ClientProcessRequestBody;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.ErrorResponse;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.Response200ForClientProcess;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import io.swagger.annotations.*;

import javax.validation.constraints.*;
import javax.validation.Valid;

/**
* Represents a collection of functions to interact with the API endpoints.
*/
@Path("/pre-process-client-update")
@Api(description = "the pre-process-client-update API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-05-05T12:44:23.299724+05:30[Asia/Colombo]", comments = "Generator version: 7.12.0")
public class PreProcessClientUpdateApi {

    @POST
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "handle pre validations & obtain  custom data to store in dynamic client update step", notes = "", response = Response200ForClientProcess.class, authorizations = {
        @Authorization(value = "OAuth2", scopes = {
             }),
        
        @Authorization(value = "BasicAuth")
         }, tags={ "Client" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Ok", response = Response200ForClientProcess.class),
        @ApiResponse(code = 400, message = "Bad Request", response = ErrorResponse.class),
        @ApiResponse(code = 500, message = "Server Error", response = ErrorResponse.class)
    })
    public Response preProcessClientUpdatePost(@Valid @NotNull ClientProcessRequestBody clientProcessRequestBody) {
        return Response.ok().entity("magic!").build();
    }
}
