package org.wso2.openbanking.nextgenpsd2.extensions.generated.api;

import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.ErrorResponse;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.EventSubscriptionRequestBody;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.Response200ForEventSubscriptionValidation;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import io.swagger.annotations.*;

import java.io.InputStream;
import java.util.Map;
import java.util.List;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
* Represents a collection of functions to interact with the API endpoints.
*/
@Path("/validate-event-subscription")
@Api(description = "the validate-event-subscription API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-08-14T10:51:10.584795800+05:30[Asia/Colombo]", comments = "Generator version: 7.14.0")
public class ValidateEventSubscriptionApi {

    @POST
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "handle event subscription validations & storing data", notes = "", response = Response200ForEventSubscriptionValidation.class, authorizations = {
        @Authorization(value = "OAuth2", scopes = {
             }),
        
        @Authorization(value = "BasicAuth")
         }, tags={ "Event Subscription" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Ok", response = Response200ForEventSubscriptionValidation.class),
        @ApiResponse(code = 400, message = "Bad Request", response = ErrorResponse.class),
        @ApiResponse(code = 500, message = "Server Error", response = ErrorResponse.class)
    })
    public Response validateEventSubscriptionPost(@Valid @NotNull EventSubscriptionRequestBody eventSubscriptionRequestBody) {
        return Response.ok().entity("magic!").build();
    }
}
