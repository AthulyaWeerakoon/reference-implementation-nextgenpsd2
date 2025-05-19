package com.wso2.openbanking.berlin.extensions.api;

import com.wso2.openbanking.berlin.extensions.datamodels.TPPMessage;
import com.wso2.openbanking.berlin.extensions.exceptions.FailedValidationException;
import com.wso2.openbanking.berlin.extensions.exceptions.ServerException;
import com.wso2.openbanking.berlin.extensions.model.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import com.wso2.openbanking.berlin.extensions.utils.*;
import io.swagger.annotations.*;
import io.swagger.annotations.Authorization;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Map;
import java.util.List;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
* Represents a collection of functions to interact with the API endpoints.
*/
@Path("/pre-process-consent-retrieval")
@Api(description = "the pre-process-consent-retrieval API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-05-05T12:44:23.299724+05:30[Asia/Colombo]", comments = "Generator version: 7.12.0")
public class PreProcessConsentRetrievalApi {

    @POST
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "handle pre-consent retrieval validations", notes = "", response = Response200ForResponseAlternation.class, authorizations = {
        @Authorization(value = "OAuth2", scopes = {
             }),
        
        @Authorization(value = "BasicAuth")
         }, tags={ "Consent" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Ok", response = Response200ForResponseAlternation.class),
        @ApiResponse(code = 400, message = "Bad Request", response = ErrorResponse.class),
        @ApiResponse(code = 500, message = "Server Error", response = ErrorResponse.class)
    })
    public Response preProcessConsentRetrievalPost(@Valid @NotNull PreProcessConsentRequestBody requestBody) {
        Log log = LogFactory.getLog(PreProcessConsentCreationApi.class);
        SuccessResponseForResponseAlternation validationResponse = new SuccessResponseForResponseAlternation();

        try {
            // Validate X-request-ID header
            // Enable forwarding of the specific header in accelerator configurations
            CommonConsentValidationUtil.validateIdempotencyHeader(requestBody.getData().getRequestHeaders());

            ConsentHandler consentHandler = CommonConsentValidationUtil.getConsentHandler(requestBody.getData()
                    .getConsentResourcePath());

            if (consentHandler != null) {
                consentHandler.handleRetrieval(requestBody, validationResponse);
            } else {
                throw new FailedValidationException(FailedValidationException.ErrorCode.NOT_FOUND,
                        ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR, null,
                                ErrorConstants.PATH_INVALID));
            }

        } catch (FailedValidationException e) {
            log.error("Validation failed for consent creation. Returning failed response.", e);
            return Response.ok().entity(e.getFormattedErrorAsString()).build();

        } catch (ServerException e) {
            log.error(e);
            return Response.status(e.getStatus()).entity(e.getFormattedErrorAsString()).build();

        } catch (JSONException e) {
            log.error(e);
            return Response.status(Response.Status.BAD_REQUEST).entity(new JSONObject(
                    ErrorUtil.getErrorResponse(ConsentExtensionConstants.INVALID_REQUEST, e.getMessage())
            ).toString()).build();

        } catch (Exception e) {
            log.error(e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new JSONObject(
                    ErrorUtil.getErrorResponse(ConsentExtensionConstants.SERVER_ERROR, e.getMessage())
            ).toString()).build();
        }

        return Response.ok().entity(new JSONObject(validationResponse).toString()).build();
    }
}
