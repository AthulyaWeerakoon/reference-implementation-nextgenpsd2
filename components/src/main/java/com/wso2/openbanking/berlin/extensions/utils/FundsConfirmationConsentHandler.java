package com.wso2.openbanking.berlin.extensions.utils;

import com.wso2.openbanking.berlin.extensions.configurations.ConfigurableProperties;
import com.wso2.openbanking.berlin.extensions.datamodels.TPPMessage;
import com.wso2.openbanking.berlin.extensions.enums.AuthTypeEnum;
import com.wso2.openbanking.berlin.extensions.enums.ConsentStatusEnum;
import com.wso2.openbanking.berlin.extensions.enums.ConsentTypeEnum;
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
 * Consent handler for account consents
 */
public class FundsConfirmationConsentHandler implements ConsentHandler, ConsentResponseHandler {
    private static final Log log = LogFactory.getLog(FundsConfirmationConsentUtil.class);

    /**
     * Handles creation of confirmation of funds consents
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

        // Validate payload
        JSONObject requestPayload;
        try {
            requestPayload =
                    CommonConsentValidationUtil.convertObjectToJson(requestBody.getData().getConsentInitiationData());
        } catch (JSONException e) {
            throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                    ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                            TPPMessage.CodeEnum.FORMAT_ERROR, ErrorConstants.PAYLOAD_FORMAT_ERROR));
        }
        FundsConfirmationConsentUtil.validateFundsConfirmationInitiationPayload(requestPayload);

        Optional<Boolean> isRedirectPreferred = CommonConsentValidationUtil.isTppRedirectPreferred(headersJSON);

        if (!isRedirectPreferred.isPresent() || BooleanUtils.isTrue(isRedirectPreferred.get())) {
            log.debug("SCA approach is Redirect SCA (OAuth2)");

            // Response body
            validationResponse.setResponseId(requestBody.getRequestId());
            validationResponse.setStatus(SuccessResponsePreProcessConsentCreation.StatusEnum.SUCCESS);

            // Response data, contains consentResource
            SuccessResponseWithDetailedConsentData data = new SuccessResponseWithDetailedConsentData();

            // Consent resource
            DetailedConsentResourceData consentResource = new DetailedConsentResourceData();
            consentResource.setReceipt(requestPayload);
            consentResource.setType(ConsentTypeEnum.FUNDS_CONFIRMATION.toString());
            consentResource.setStatus(ConsentStatusEnum.RECEIVED.toString());

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
     * Handles CoF consent creation response customization
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
                ConsentTypeEnum.FUNDS_CONFIRMATION.toString());
    }
}
