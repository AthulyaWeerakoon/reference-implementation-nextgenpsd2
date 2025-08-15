package org.wso2.openbanking.nextgenpsd2.extensions.generated.api;

import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.ErrorResponse;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.EventPollingRequestBody;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.Response200ForEnrichEventPolling;

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
@Path("/enrich-event-polling-response")
@Api(description = "the enrich-event-polling-response API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-08-14T10:51:10.584795800+05:30[Asia/Colombo]", comments = "Generator version: 7.14.0")
public class EnrichEventPollingResponseApi {

    @POST
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "handle post event-polling response generation", notes = "", response = Response200ForEnrichEventPolling.class, authorizations = {
        @Authorization(value = "OAuth2", scopes = {
             }),
        
        @Authorization(value = "BasicAuth")
         }, tags={ "Event Polling" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Ok", response = Response200ForEnrichEventPolling.class),
        @ApiResponse(code = 400, message = "Bad Request", response = ErrorResponse.class),
        @ApiResponse(code = 500, message = "Server Error", response = ErrorResponse.class)
    })
    public Response enrichEventPollingResponsePost(@Valid @NotNull EventPollingRequestBody eventPollingRequestBody) {
        return Response.ok().entity("magic!").build();
    }
}
