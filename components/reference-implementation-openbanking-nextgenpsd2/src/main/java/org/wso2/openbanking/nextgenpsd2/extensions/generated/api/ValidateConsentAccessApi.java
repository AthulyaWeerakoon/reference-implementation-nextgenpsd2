package org.wso2.openbanking.nextgenpsd2.extensions.generated.api;

import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.ErrorResponse;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.Response200;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.ValidateConsentAccessRequestBody;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import io.swagger.annotations.*;

import javax.validation.constraints.*;
import javax.validation.Valid;

/**
* Represents a collection of functions to interact with the API endpoints.
*/
@Path("/validate-consent-access")
@Api(description = "the validate-consent-access API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-05-05T12:44:23.299724+05:30[Asia/Colombo]", comments = "Generator version: 7.12.0")
public class ValidateConsentAccessApi {

    @POST
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "handle custom consent data validations before data access", notes = "", response = Response200.class, authorizations = {
        @Authorization(value = "OAuth2", scopes = {
             }),
        
        @Authorization(value = "BasicAuth")
         }, tags={ "Consent" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Ok", response = Response200.class),
        @ApiResponse(code = 400, message = "Bad Request", response = ErrorResponse.class),
        @ApiResponse(code = 500, message = "Server Error", response = ErrorResponse.class)
    })
    public Response validateConsentAccessPost(@Valid @NotNull ValidateConsentAccessRequestBody validateConsentAccessRequestBody) {
        return Response.ok().entity("magic!").build();
    }
}
