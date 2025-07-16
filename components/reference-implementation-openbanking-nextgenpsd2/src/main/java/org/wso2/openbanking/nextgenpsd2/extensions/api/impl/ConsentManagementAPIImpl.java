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
import org.wso2.openbanking.nextgenpsd2.extensions.exceptions.BadRequestException;
import org.wso2.openbanking.nextgenpsd2.extensions.exceptions.ServerErrorException;
import org.wso2.openbanking.nextgenpsd2.extensions.exceptions.ValidationFailureException;
import org.wso2.openbanking.nextgenpsd2.extensions.handlers.ConsentManagementValidationHandler;
import org.wso2.openbanking.nextgenpsd2.extensions.model.TPPMessage;
import org.wso2.openbanking.nextgenpsd2.extensions.model.generated.EnrichConsentCreationRequestBody;
import org.wso2.openbanking.nextgenpsd2.extensions.model.generated.PreProcessConsentCreationRequestBody;
import org.wso2.openbanking.nextgenpsd2.extensions.model.generated.PreProcessConsentRequestBody;
import org.wso2.openbanking.nextgenpsd2.extensions.model.generated.SuccessResponseConsentRevocation;
import org.wso2.openbanking.nextgenpsd2.extensions.model.generated.SuccessResponseForResponseAlternation;
import org.wso2.openbanking.nextgenpsd2.extensions.model.generated.SuccessResponsePreProcessConsentCreation;
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
     * @param requestBody
     * @return
     */
    public static Response enrichConsentCreationResponse(EnrichConsentCreationRequestBody requestBody) {
        String requestId = requestBody.getRequestId();
        try {
            ConsentManagementValidationHandler consentHandler = CommonConsentValidationUtil
                    .getConsentManagementResponseHandler(requestBody.getData().getConsentResourcePath());

            SuccessResponseForResponseAlternation validationResponse;

            if (consentHandler != null) {
                validationResponse = consentHandler.enrichCreationResponse(requestBody);
            } else {
                // Server error since if path is invalid consent creation should have failed
                // thus making this unreachable
                JSONObject errorResponse = ErrorUtil.getFormattedErrorResponse(ErrorUtil.constructBerlinError(null,
                        TPPMessage.CategoryEnum.ERROR, null, ErrorConstants.PATH_INVALID));
                return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
            }

            return Response.ok().entity(new JSONObject(validationResponse).toString()).build();

        } catch (BadRequestException | ServerErrorException e) {
            log.error("[" + requestId + "] " + "An error occurred creating consent.", e);
            return Response.status(e.getStatus()).entity(e.getFormattedErrorAsString()).build();
        }
    }

    /**
     * Method for returning the response for pre-processing consent creation request.
     *
     * @param requestBody
     * @return
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
                    JSONObject errorResponse = ErrorUtil.getFormattedFailedResponse(400,
                            ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                                    TPPMessage.CodeEnum.FORMAT_ERROR, ErrorConstants.PAYLOAD_NOT_PRESENT_ERROR));
                    return Response.ok().entity(errorResponse.toString()).build();
                }

            } catch (JSONException e) {
                // If payload is not JSON
                JSONObject errorResponse = ErrorUtil.getFormattedFailedResponse(400,
                        ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                                TPPMessage.CodeEnum.FORMAT_ERROR, ErrorConstants.PAYLOAD_FORMAT_ERROR));
                return Response.ok().entity(errorResponse.toString()).build();
            }

            ConsentManagementValidationHandler consentManagementValidationHandler = CommonConsentValidationUtil
                    .getConsentManagementResponseHandler(requestBody.getData().getConsentResourcePath());

            SuccessResponsePreProcessConsentCreation validationResponse;

            if (consentManagementValidationHandler != null) {
                validationResponse = consentManagementValidationHandler.handleCreation(requestBody);
            } else {
                JSONObject errorObject = ErrorUtil.getFormattedFailedResponse(404,
                        ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR, null,
                                ErrorConstants.PATH_INVALID));
                return Response.ok().entity(errorObject).build();
            }

            return Response.ok().entity(new JSONObject(validationResponse).toString()).build();

        } catch (ValidationFailureException e) {
            log.debug("[" + requestId + "] " + "Validation failed for consent creation. Returning failed response.", e);
            return Response.ok().entity(e.getFormattedErrorAsString()).build();

        }  catch (BadRequestException | ServerErrorException e) {
            log.error("[" + requestId + "] " + "An error occurred creating consent.", e);
            return Response.status(e.getStatus()).entity(e.getFormattedErrorAsString()).build();
        }
    }

    /**
     * Method for returning the response for pre-processing consent retrieval request.
     *
     * @param requestBody
     * @return
     */
    public static Response preProcessConsentRetrieval(PreProcessConsentRequestBody requestBody) {
        String requestId = requestBody.getRequestId();
        try {
            // Validate X-request-ID header
            // Enable forwarding of the specific header in accelerator configurations
            CommonConsentValidationUtil.validateRequestIdentificationHeader(requestBody.getData().getRequestHeaders());

            ConsentManagementValidationHandler consentManagementValidationHandler = CommonConsentValidationUtil
                    .getConsentManagementResponseHandler(requestBody.getData().getConsentResourcePath());

            SuccessResponseForResponseAlternation validationResponse;

            if (consentManagementValidationHandler != null) {
                validationResponse = consentManagementValidationHandler.handleRetrieval(requestBody);
            } else {
                JSONObject errorObject = ErrorUtil.getFormattedFailedResponse(404,
                        ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR, null,
                                ErrorConstants.PATH_INVALID));
                return Response.ok().entity(errorObject).build();
            }

            return Response.ok().entity(new JSONObject(validationResponse).toString()).build();

        } catch (ValidationFailureException e) {
            log.debug("[" + requestId + "] " + "Validation failed for consent retrieval. Returning failed response.",
                    e);
            return Response.ok().entity(e.getFormattedErrorAsString()).build();

        }  catch (BadRequestException | ServerErrorException e) {
            log.error("[" + requestId + "] " + "An error occurred retrieving consent.", e);
            return Response.status(e.getStatus()).entity(e.getFormattedErrorAsString()).build();
        }
    }

    /**
     * Method for returning response for pre-processing consent revocation request.
     *
     * @param requestBody
     * @return
     */
    public static Response preProcessConsentRevoke(PreProcessConsentRequestBody requestBody) {
        String requestId = requestBody.getRequestId();
        try {
            // Validate X-request-ID header
            // Enable forwarding of the specific header in accelerator configurations
            CommonConsentValidationUtil.validateRequestIdentificationHeader(requestBody.getData().getRequestHeaders());

            ConsentManagementValidationHandler consentManagementValidationHandler = CommonConsentValidationUtil
                    .getConsentManagementResponseHandler(requestBody.getData().getConsentResourcePath());

            SuccessResponseConsentRevocation validationResponse;

            if (consentManagementValidationHandler != null) {
                validationResponse = consentManagementValidationHandler.handleRevocation(requestBody);
            } else {
                JSONObject errorObject = ErrorUtil.getFormattedFailedResponse(404,
                        ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR, null,
                                ErrorConstants.PATH_INVALID));
                return Response.ok().entity(errorObject).build();
            }

            return Response.ok().entity(new JSONObject(validationResponse).toString()).build();

        } catch (ValidationFailureException e) {
            log.debug("[" + requestId + "] " + "Validation failed for consent revocation. Returning failed" +
                            "response.", e);
            return Response.ok().entity(e.getFormattedErrorAsString()).build();

        }  catch (BadRequestException | ServerErrorException e) {
            log.error("[" + requestId + "] " + "An error occurred revoking consent.", e);
            return Response.status(e.getStatus()).entity(e.getFormattedErrorAsString()).build();
        }
    }
}
