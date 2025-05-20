package com.wso2.openbanking.berlin.extensions.utils;

import com.wso2.openbanking.berlin.extensions.configurations.ConfigurableProperties;
import com.wso2.openbanking.berlin.extensions.datamodels.TPPMessage;
import com.wso2.openbanking.berlin.extensions.enums.AuthTypeEnum;
import com.wso2.openbanking.berlin.extensions.enums.ConsentTypeEnum;
import com.wso2.openbanking.berlin.extensions.enums.TransactionStatusEnum;
import com.wso2.openbanking.berlin.extensions.exceptions.FailedValidationException;
import com.wso2.openbanking.berlin.extensions.exceptions.ServerException;
import com.wso2.openbanking.berlin.extensions.model.*;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

/**
 * Consent handler for payment consents
 */
public class PaymentConsentHandler implements ConsentHandler, ConsentResponseHandler {
    private static final Log log = LogFactory.getLog(PaymentConsentHandler.class);

    /**
     * Handles creation of payment consent creation
     *
     * @param requestBody
     * @param validationResponse
     */
    @Override
    public void handleCreation(PreProcessConsentCreationRequestBody requestBody,
                               SuccessResponsePreProcessConsentCreation validationResponse)
            throws FailedValidationException {
        // Skipping idempotency check as it's handled by the accelerator
        // ToDo: Add explicit authorisation support

        boolean isSCARequired = Boolean.parseBoolean(ConfigurableProperties.IS_SCA_REQUIRED);

        JSONObject headersJSON =
                CommonConsentValidationUtil.convertObjectToJson(requestBody.getData().getRequestHeaders());

        // Validate headers
        CommonConsentValidationUtil.validateTppRedirectPreferredHeader(headersJSON);
        CommonConsentValidationUtil.validatePsuIpAddress(headersJSON);

        // Validate payload
        JSONObject requestPayload;
        try {
            requestPayload = CommonConsentValidationUtil
                    .convertObjectToJson(requestBody.getData().getConsentInitiationData());
        } catch (JSONException e) {
            throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                    ErrorUtil.constructBerlinError(
                            null, TPPMessage.CategoryEnum.ERROR, TPPMessage.CodeEnum.FORMAT_ERROR,
                            ErrorConstants.PAYLOAD_FORMAT_ERROR));
        }
        PaymentConsentUtil.validatePaymentInitiationPayload(requestPayload,
                requestBody.getData().getConsentResourcePath());

        Optional<Boolean> isRedirectPreferred = CommonConsentValidationUtil.isTppRedirectPreferred(headersJSON);

        if (!isRedirectPreferred.isPresent() || BooleanUtils.isTrue(isRedirectPreferred.get())) {
            log.debug("SCA approach is Redirect SCA (OAuth2)");

            String paymentConsentType = CommonConsentValidationUtil
                    .getConsentTypeFromRequestPath(requestBody.getData().getConsentResourcePath());

            // Response body
            validationResponse.setResponseId(requestBody.getRequestId());
            validationResponse.setStatus(SuccessResponsePreProcessConsentCreation.StatusEnum.SUCCESS);

            // Response data, contains consentResource
            SuccessResponseWithDetailedConsentData data = new SuccessResponseWithDetailedConsentData();

            // Consent resource
            DetailedConsentResourceData consentResource = new DetailedConsentResourceData();
            consentResource.setReceipt(requestPayload);
            consentResource.setType(paymentConsentType);
            consentResource.setStatus(TransactionStatusEnum.RCVD.name());

            // Setting inapplicable consent parameters
            consentResource.setFrequency(0);
            consentResource.setValidityTime(0L);
            consentResource.setRecurringIndicator(false);

            // Build auth resource for implicit authorisation
            // ToDo: Revisit once explicit authorisation is supported
            Authorization authObj = new Authorization();
            if (headersJSON.has(ConsentExtensionConstants.PSU_ID_HEADER)) {
                authObj.setUserId(headersJSON.getString(ConsentExtensionConstants.PSU_ID_HEADER));
            }
            authObj.setType(AuthTypeEnum.AUTHORISATION.toString());
            String authStatus = CommonConsentValidationUtil.getAuthorizationStatus(isSCARequired, headersJSON);
            authObj.setStatus(authStatus);

            // Append auth resource to consent
            consentResource.addAuthorizationsItem(authObj);

            // Envelop consent in response data
            data.setConsentResource(consentResource);

            // Append response data to response
            validationResponse.setData(data);
        }
    }

    /**
     * Handles retrieval of payment consents
     *
     * @param requestBody
     * @param validationResponse
     * @throws FailedValidationException
     */
    @Override
    public void handleRetrieval(PreProcessConsentRequestBody requestBody,
                                SuccessResponseForResponseAlternation validationResponse)
            throws FailedValidationException, ServerException {

        PreProcessConsentRetrievalData data = requestBody.getData();
        StoredBasicConsentResourceData consentResource = data.getConsentResource();
        String consentId = consentResource.getId();
        String consentTypeFromPath = CommonConsentValidationUtil
                .getConsentTypeFromRequestPath(requestBody.getData().getConsentResourcePath());

        if (log.isDebugEnabled()) {
            log.debug(String.format("Validating consent of Id %s for valid client", consentId));
        }

        // Get request client id from the headers
        String requestClientId;
        JSONObject headers;
        try {
            headers = CommonConsentValidationUtil.convertObjectToJson(data.getRequestHeaders());
            requestClientId = headers.getString(CommonConstants.X_WSO2_CLIENT_ID_KEY);
        } catch (JSONException e) {
            // Should be unreachable (since insequence always adds client id header)
            throw new ServerException(ServerException.ErrorCode.BAD_REQUEST, ErrorUtil.constructBerlinError(
                    null, TPPMessage.CategoryEnum.ERROR, TPPMessage.CodeEnum.INTERNAL_SERVER_ERROR,
                    "x-wso2-client-id header not found"));
        }

        // Validate client
        CommonConsentValidationUtil.validateClient(requestClientId, data.getConsentResource().getClientId());

        // Validate consent type
        if (log.isDebugEnabled()) {
            log.debug(String.format("Validating consent of Id %s for correct type", consentId));
        }
        CommonConsentValidationUtil.validateConsentType(consentTypeFromPath, consentResource.getType());

        validationResponse.setStatus(SuccessResponseForResponseAlternation.StatusEnum.SUCCESS);
        validationResponse.setResponseId(requestBody.getRequestId());

        SuccessResponseForResponseAlternationData responseData = new SuccessResponseForResponseAlternationData();

        // For status calls
        JSONObject statusPayload = new JSONObject();
        if(!requestBody.getData().getConsentResourcePath().contains(ConsentExtensionConstants.STATUS)) {
            statusPayload = CommonConsentValidationUtil.convertObjectToJson(consentResource.getReceipt());
        }

        CommonConsentValidationUtil.appendConsentStatusResponse(consentResource, consentTypeFromPath, statusPayload);
        responseData.setModifiedResponse(statusPayload);
        responseData.setResponseHeaders(CommonConsentValidationUtil.getIdempotencyHeaderJSON(
                headers.getString(ConsentExtensionConstants.X_REQUEST_ID_HEADER)
        ));

        validationResponse.setData(responseData);
    }

    /**
     * Handles payment consent creation response customization
     *
     * @param requestBody
     * @param validationResponse
     * @throws FailedValidationException
     */
    @Override
    public void enrichCreationResponse(EnrichConsentCreationRequestBody requestBody,
                                       SuccessResponseForResponseAlternation validationResponse)
            throws ServerException {
        ConsentInitiationUtil.buildResponseAlterationResponseForConsentCreation(requestBody, validationResponse,
                ConsentTypeEnum.PAYMENTS.toString());
    }
}
