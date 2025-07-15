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

package org.wso2.openbanking.nextgenpsd2.extensions.handlers.impl;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.openbanking.nextgenpsd2.extensions.configurations.ConfigurationConstants;
import org.wso2.openbanking.nextgenpsd2.extensions.constants.CommonConstants;
import org.wso2.openbanking.nextgenpsd2.extensions.constants.ConsentExtensionConstants;
import org.wso2.openbanking.nextgenpsd2.extensions.constants.ErrorConstants;
import org.wso2.openbanking.nextgenpsd2.extensions.enums.AuthTypeEnum;
import org.wso2.openbanking.nextgenpsd2.extensions.enums.ConsentStatusEnum;
import org.wso2.openbanking.nextgenpsd2.extensions.enums.ConsentTypeEnum;
import org.wso2.openbanking.nextgenpsd2.extensions.enums.ScaApproachEnum;
import org.wso2.openbanking.nextgenpsd2.extensions.exceptions.BadRequestException;
import org.wso2.openbanking.nextgenpsd2.extensions.exceptions.ValidationFailureException;
import org.wso2.openbanking.nextgenpsd2.extensions.handlers.ConsentManagementValidationHandler;
import org.wso2.openbanking.nextgenpsd2.extensions.model.AccountInitiationPayload;
import org.wso2.openbanking.nextgenpsd2.extensions.model.TPPMessage;
import org.wso2.openbanking.nextgenpsd2.extensions.model.generated.Authorization;
import org.wso2.openbanking.nextgenpsd2.extensions.model.generated.DetailedConsentResourceData;
import org.wso2.openbanking.nextgenpsd2.extensions.model.generated.EnrichConsentCreationRequestBody;
import org.wso2.openbanking.nextgenpsd2.extensions.model.generated.PreProcessConsentCreationRequestBody;
import org.wso2.openbanking.nextgenpsd2.extensions.model.generated.PreProcessConsentRequestBody;
import org.wso2.openbanking.nextgenpsd2.extensions.model.generated.PreProcessConsentRetrievalData;
import org.wso2.openbanking.nextgenpsd2.extensions.model.generated.StoredBasicConsentResourceData;
import org.wso2.openbanking.nextgenpsd2.extensions.model.generated.SuccessResponseConsentRevocation;
import org.wso2.openbanking.nextgenpsd2.extensions.model.generated.SuccessResponseForResponseAlternation;
import org.wso2.openbanking.nextgenpsd2.extensions.model.generated.SuccessResponseForResponseAlternationData;
import org.wso2.openbanking.nextgenpsd2.extensions.model.generated.SuccessResponsePreProcessConsentCreation;
import org.wso2.openbanking.nextgenpsd2.extensions.model.generated.SuccessResponseWithDetailedConsentData;
import org.wso2.openbanking.nextgenpsd2.extensions.utils.AccountConsentUtil;
import org.wso2.openbanking.nextgenpsd2.extensions.utils.CommonConsentValidationUtil;
import org.wso2.openbanking.nextgenpsd2.extensions.utils.ConsentInitiationUtil;
import org.wso2.openbanking.nextgenpsd2.extensions.utils.ErrorUtil;

import java.util.Optional;

/**
 * Consent handler for account consents.
 */
public class AccountConsentManageHandler implements ConsentManagementValidationHandler {
    private static final Log log = LogFactory.getLog(AccountConsentManageHandler.class);

    /**
     * Handles creation of account consents.
     *
     * @param requestBody
     * @return
     * @throws ValidationFailureException
     */
    @Override
    public SuccessResponsePreProcessConsentCreation handleCreation(PreProcessConsentCreationRequestBody requestBody)
            throws ValidationFailureException, BadRequestException {
        String requestId = requestBody.getRequestId();

        // Skipping idempotency check as it's handled by the accelerator
        // ToDo: Add explicit authorisation support

        boolean isSCARequired = Boolean.parseBoolean(ConfigurationConstants.IS_SCA_REQUIRED);

        JSONObject headersJSON = CommonConsentValidationUtil.convertObjectToJson(requestBody.getData()
                .getRequestHeaders());

        // Validate headers
        CommonConsentValidationUtil.validateTppRedirectPreferredHeader(requestId, headersJSON);
        CommonConsentValidationUtil.validatePsuIpAddress(requestId, headersJSON);

        // Validate payload
        JSONObject requestPayload;
        try {
            requestPayload = CommonConsentValidationUtil
                    .convertObjectToJson(requestBody.getData().getConsentInitiationData());
        } catch (JSONException e) {
            throw new ValidationFailureException(ValidationFailureException.ErrorCode.BAD_REQUEST,
                    ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                            TPPMessage.CodeEnum.FORMAT_ERROR, ErrorConstants.PAYLOAD_FORMAT_ERROR));
        }

        // Parse account initiation payload and validate its structure
        AccountInitiationPayload payload = CommonConsentValidationUtil
                .validateJSONFromModel(requestPayload.toString(), AccountInitiationPayload.class);

        Optional<Boolean> isRedirectPreferred = CommonConsentValidationUtil.isTppRedirectPreferred(requestId,
                headersJSON);

        if (!isRedirectPreferred.isPresent() || BooleanUtils.isTrue(isRedirectPreferred.get())) {
            log.debug("[" + requestId + "] " + "SCA approach is Redirect SCA (OAuth2)");
            String authStatus = CommonConsentValidationUtil.getAuthorizationStatus(isSCARequired, headersJSON);

            // Response body
            SuccessResponsePreProcessConsentCreation validationResponse =
                    new SuccessResponsePreProcessConsentCreation();
            validationResponse.setResponseId(requestBody.getRequestId());
            validationResponse.setStatus(SuccessResponsePreProcessConsentCreation.StatusEnum.SUCCESS);

            // Response data, contains consentResource
            SuccessResponseWithDetailedConsentData data = new SuccessResponseWithDetailedConsentData();

            // Consent resource
            DetailedConsentResourceData consentResource = new DetailedConsentResourceData();
            consentResource.setReceipt(requestPayload);
            consentResource.setType(ConsentTypeEnum.ACCOUNTS.toString());
            consentResource.setStatus(ConsentStatusEnum.RECEIVED.toString());

            // Setting additional properties to consent resource
            boolean recurringIndicator = payload.getRecurringIndicator();
            consentResource.setRecurringIndicator(recurringIndicator);
            consentResource.setFrequency(payload.getFrequencyPerDay());

            String validUntilString = String.valueOf(payload.getValidUntil());
            if (recurringIndicator) {
                consentResource.setValidityTime(AccountConsentUtil.convertToUtcTimestamp(validUntilString));
            } else {
                // setting null for one off consent's validity period
                consentResource.setValidityTime(0L);
            }

            // Build auth resource for implicit authorisation
            // ToDo: Revisit once explicit authorisation is supported
            Authorization authObj = new Authorization();
            if (headersJSON.has(ConsentExtensionConstants.PSU_ID_HEADER)) {
                authObj.setUserId(headersJSON.getString(ConsentExtensionConstants.PSU_ID_HEADER));
            }
            authObj.setType(AuthTypeEnum.AUTHORISATION.toString());
            authObj.setStatus(authStatus);

            // Append auth resource to consent
            consentResource.addAuthorizationsItem(authObj);

            // Envelop consent in response data
            data.setConsentResource(consentResource);

            // Append response data to response
            validationResponse.setData(data);

            return validationResponse;
        } else {
            //ToDo: revisit once decoupled approach is implemented.
            throw new ValidationFailureException(ValidationFailureException.ErrorCode.BAD_REQUEST,
                    ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                            TPPMessage.CodeEnum.FORMAT_ERROR, String.format("%s SCA Approach is not supported",
                                    ScaApproachEnum.DECOUPLED)));
        }
    }

    /**
     * Handles retrieval of account requests.
     *
     * @param requestBody
     * @return
     * @throws ValidationFailureException
     * @throws BadRequestException
     */
    @Override
    public SuccessResponseForResponseAlternation handleRetrieval(PreProcessConsentRequestBody requestBody)
            throws ValidationFailureException, BadRequestException {
        String requestId = requestBody.getRequestId();

        PreProcessConsentRetrievalData data = requestBody.getData();
        String requestPath = data.getConsentResourcePath();
        String consentType = CommonConsentValidationUtil.getConsentTypeFromRequestPath(requestPath);
        String consentId = data.getConsentId();
        StoredBasicConsentResourceData consentResource = data.getConsentResource();

        if (log.isDebugEnabled()) {
            log.debug("[" + requestId + "] " + String.format("Validating consent of Id %s for valid client",
                    consentId));
        }

        // Get request client id from the headers
        String requestClientId;
        JSONObject headers;
        try {
            headers = CommonConsentValidationUtil.convertObjectToJson(data.getRequestHeaders());
            requestClientId = headers.getString(CommonConstants.X_WSO2_CLIENT_ID_KEY);
        } catch (JSONException e) {
            // Should be unreachable (since insequence always adds client id header)
            throw new BadRequestException("x-wso2-client-id header not found");
        }

        // Validate client
        CommonConsentValidationUtil.validateClient(requestClientId, data.getConsentResource().getClientId());

        if (log.isDebugEnabled()) {
            log.debug("[" + requestId + "] " + String.format("Validating consent of Id %s for correct type",
                    consentId));
        }
        CommonConsentValidationUtil.validateConsentType(consentType, consentResource.getType());

        if ((consentResource.getRecurringIndicator() && AccountConsentUtil.isConsentExpired(
                consentResource.getValidityTime()))
                && !(StringUtils.equals(consentResource.getStatus(),
                ConsentStatusEnum.TERMINATED_BY_TPP.toString())
                || StringUtils.equals(consentResource.getStatus(),
                ConsentStatusEnum.REVOKED_BY_PSU.toString()))) {
            log.debug("[" + requestId + "] " + "The Consent is expired");
            consentResource.setStatus(ConsentStatusEnum.EXPIRED.toString());
        }

        JSONObject payloadToSend = new JSONObject();

        if (StringUtils.contains(requestPath, ConsentExtensionConstants.STATUS)) {
            CommonConsentValidationUtil.appendConsentStatusResponse(consentResource, consentType, payloadToSend);
        } else {
            payloadToSend = CommonConsentValidationUtil.convertObjectToJson(consentResource.getReceipt());
            AccountConsentUtil.extendAccountConsentGetResponse(consentResource, payloadToSend);
        }

        SuccessResponseForResponseAlternation validationResponse = new SuccessResponseForResponseAlternation();
        validationResponse.setResponseId(requestBody.getRequestId());
        validationResponse.setStatus(SuccessResponseForResponseAlternation.StatusEnum.SUCCESS);
        validationResponse.setData(new SuccessResponseForResponseAlternationData()
                .modifiedResponse(payloadToSend)
                .responseHeaders(CommonConsentValidationUtil.getIdempotencyHeaderJSON(
                        headers.getString(ConsentExtensionConstants.X_REQUEST_ID_HEADER))));

        return validationResponse;
    }

    /**
     * Handles revocation of account consents.
     *
     * @param requestBody
     * @return
     */
    @Override
    public SuccessResponseConsentRevocation handleRevocation(PreProcessConsentRequestBody requestBody)
            throws ValidationFailureException, BadRequestException {
        return CommonConsentValidationUtil.validateRevokeRequestAndReturnResponse(requestBody);
    }

    /**
     * Handles account consent creation response customization.
     *
     * @param requestBody
     * @return
     * @throws BadRequestException
     */
    @Override
    public SuccessResponseForResponseAlternation enrichCreationResponse(EnrichConsentCreationRequestBody requestBody)
            throws BadRequestException {
        SuccessResponseForResponseAlternation validationResponse = new SuccessResponseForResponseAlternation();
        ConsentInitiationUtil.buildResponseAlterationResponseForConsentCreation(requestBody, validationResponse,
                ConsentTypeEnum.ACCOUNTS.toString());
        return validationResponse;
    }
}
