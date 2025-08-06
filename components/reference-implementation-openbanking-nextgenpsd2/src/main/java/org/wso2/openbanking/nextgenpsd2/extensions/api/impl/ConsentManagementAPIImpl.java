/**
 * Copyright (c) 2025, WSO2 LLC. (https://www.wso2.com).
 * <p>
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *     http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.openbanking.nextgenpsd2.extensions.api.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.openbanking.nextgenpsd2.extensions.constants.ErrorConstants;
import org.wso2.openbanking.nextgenpsd2.extensions.exceptions.ExtensionException;
import org.wso2.openbanking.nextgenpsd2.extensions.exceptions.ValidationFailureException;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.EnrichConsentCreationRequestBody;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.PreProcessConsentCreationRequestBody;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.PreProcessConsentRequestBody;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.SuccessResponseConsentRevocation;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.SuccessResponseForResponseAlternation;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.SuccessResponsePreProcessConsentCreation;
import org.wso2.openbanking.nextgenpsd2.extensions.handlers.ConsentManagementValidationHandler;
import org.wso2.openbanking.nextgenpsd2.extensions.model.TPPMessage;
import org.wso2.openbanking.nextgenpsd2.extensions.utils.CommonConsentValidationUtil;
import org.wso2.openbanking.nextgenpsd2.extensions.utils.ErrorUtil;

import javax.ws.rs.core.Response;

/**
 * Implementation class maintaining the methods for consent management extension APIs.
 */
public class ConsentManagementAPIImpl {
    public static Log log = LogFactory.getLog(ConsentManagementAPIImpl.class);

    /**
     * Method for returning the response for enriching consent creation request.
     *
     * @param requestBody request body from enrich consent creation request
     * @return built enrich consent creation response
     */
    public static Response enrichConsentCreationResponse(EnrichConsentCreationRequestBody requestBody) {
        String requestId = requestBody.getRequestId();
        try {
            ConsentManagementValidationHandler consentHandler = CommonConsentValidationUtil
                    .getConsentManagementResponseHandler(requestBody.getData().getConsentResourcePath());
            SuccessResponseForResponseAlternation validationResponse = consentHandler
                    .enrichCreationResponse(requestBody);

            return Response.ok().entity(new JSONObject(validationResponse).toString()).build();

        } catch (ExtensionException e) {
            log.error("[" + requestId + "] " + "An error occurred enriching consent creation response.", e);
            return Response.status(e.getStatus()).entity(e.getFormattedErrorAsString()).build();
        } catch (ValidationFailureException e) {
            // Should be unreachable since resource path is validated in consent creation
            // Thus bad request error is thrown
            log.error("[" + requestId + "] " + "Invalid resource path received when enriching consent " +
                    "creation response.", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(ErrorUtil.getErrorResponse(
                    "invalid_request", "Invalid resource path received when enriching " +
                            "consent creation response.")).build();
        }
    }

    /**
     * Method for returning the response for pre-processing consent creation request.
     *
     * @param requestBody request body from pre-process consent creation request
     * @return built pre-processed consent creation response
     */
    public static Response preProcessConsentCreation(PreProcessConsentCreationRequestBody requestBody) {
        String requestId = requestBody.getRequestId();
        try {
            // Validate X-request-ID header
            // Enable forwarding of the specific header in accelerator configurations
            CommonConsentValidationUtil.validateRequestIdentificationHeader(requestBody.getData().getRequestHeaders());

            // Extract consent initiation data
            JSONObject consentInitiationDataJSON;
            try {
                consentInitiationDataJSON = CommonConsentValidationUtil.convertObjectToJson(requestBody.getData()
                        .getConsentInitiationData());

                if (consentInitiationDataJSON.isEmpty()) {
                    // If payload is empty
                    log.error("[" + requestId + "] " + ErrorConstants.PAYLOAD_NOT_PRESENT_ERROR);
                    JSONObject errorResponse = ErrorUtil.getFormattedFailedResponse(400,
                            ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                                    TPPMessage.CodeEnum.FORMAT_ERROR, ErrorConstants.PAYLOAD_NOT_PRESENT_ERROR));
                    return Response.ok().entity(errorResponse.toString()).build();
                }

            } catch (JSONException e) {
                // If payload is not JSON
                log.error("[" + requestId + "] " + ErrorConstants.PAYLOAD_FORMAT_ERROR);
                JSONObject errorResponse = ErrorUtil.getFormattedFailedResponse(400,
                        ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                                TPPMessage.CodeEnum.FORMAT_ERROR, ErrorConstants.PAYLOAD_FORMAT_ERROR));
                return Response.ok().entity(errorResponse.toString()).build();
            }

            // Get validation response for consent creation based on consent type
            ConsentManagementValidationHandler consentManagementValidationHandler = CommonConsentValidationUtil
                    .getConsentManagementResponseHandler(requestBody.getData().getConsentResourcePath());
            SuccessResponsePreProcessConsentCreation validationResponse = consentManagementValidationHandler
                    .handleCreation(requestBody);

            return Response.ok().entity(new JSONObject(validationResponse).toString()).build();

        } catch (ValidationFailureException e) {
            log.debug("[" + requestId + "] " + "Validation failed for consent creation. Returning failed response.",
                    e);
            return Response.ok().entity(e.getFormattedErrorAsString()).build();

        }  catch (ExtensionException e) {
            log.error("[" + requestId + "] " + "An error occurred creating consent.", e);
            return Response.status(e.getStatus()).entity(e.getFormattedErrorAsString()).build();
        }
    }

    /**
     * Method for returning the response for pre-processing consent retrieval request.
     *
     * @param requestBody request body from pre-process consent retrieval request
     * @return built pre-processed consent retrieval response
     */
    public static Response preProcessConsentRetrieval(PreProcessConsentRequestBody requestBody) {
        String requestId = requestBody.getRequestId();
        try {
            // Validate X-request-ID header
            // Enable forwarding of the specific header in accelerator configurations
            CommonConsentValidationUtil.validateRequestIdentificationHeader(requestBody.getData().getRequestHeaders());

            // Get validation response for consent retrieval based on consent type
            ConsentManagementValidationHandler consentManagementValidationHandler = CommonConsentValidationUtil
                    .getConsentManagementResponseHandler(requestBody.getData().getConsentResourcePath());
            SuccessResponseForResponseAlternation validationResponse = consentManagementValidationHandler
                    .handleRetrieval(requestBody);

            return Response.ok().entity(new JSONObject(validationResponse).toString()).build();

        } catch (ValidationFailureException e) {
            log.debug("[" + requestId + "] " + "Validation failed for consent retrieval. Returning failed response.",
                    e);
            return Response.ok().entity(e.getFormattedErrorAsString()).build();

        }  catch (ExtensionException e) {
            log.error("[" + requestId + "] " + "An error occurred retrieving consent.", e);
            return Response.status(e.getStatus()).entity(e.getFormattedErrorAsString()).build();
        }
    }

    /**
     * Method for returning response for pre-processing consent revocation request.
     *
     * @param requestBody request body from pre-process consent revocation request
     * @return built pre-processed consent revocation response
     */
    public static Response preProcessConsentRevoke(PreProcessConsentRequestBody requestBody) {
        String requestId = requestBody.getRequestId();
        try {
            // Validate X-request-ID header
            // Enable forwarding of the specific header in accelerator configurations
            CommonConsentValidationUtil.validateRequestIdentificationHeader(requestBody.getData().getRequestHeaders());

            // Get validation response for consent revocation based on consent type
            ConsentManagementValidationHandler consentManagementValidationHandler = CommonConsentValidationUtil
                    .getConsentManagementResponseHandler(requestBody.getData().getConsentResourcePath());
            SuccessResponseConsentRevocation validationResponse = consentManagementValidationHandler
                    .handleRevocation(requestBody);

            return Response.ok().entity(new JSONObject(validationResponse).toString()).build();

        } catch (ValidationFailureException e) {
            log.debug("[" + requestId + "] " + "Validation failed for consent revocation. Returning failed" +
                            "response.", e);
            return Response.ok().entity(e.getFormattedErrorAsString()).build();

        }  catch (ExtensionException e) {
            log.error("[" + requestId + "] " + "An error occurred revoking consent.", e);
            return Response.status(e.getStatus()).entity(e.getFormattedErrorAsString()).build();
        }
    }
}
