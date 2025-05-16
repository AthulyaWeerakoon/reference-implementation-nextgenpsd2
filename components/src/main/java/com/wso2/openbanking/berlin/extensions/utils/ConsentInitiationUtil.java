package com.wso2.openbanking.berlin.extensions.utils;

import com.wso2.openbanking.berlin.extensions.configurations.ConfigurableProperties;
import com.wso2.openbanking.berlin.extensions.datamodels.ScaApproach;
import com.wso2.openbanking.berlin.extensions.datamodels.ScaMethod;
import com.wso2.openbanking.berlin.extensions.datamodels.TPPMessage;
import com.wso2.openbanking.berlin.extensions.enums.ConsentTypeEnum;
import com.wso2.openbanking.berlin.extensions.enums.ScaApproachEnum;
import com.wso2.openbanking.berlin.extensions.exceptions.ServerException;
import com.wso2.openbanking.berlin.extensions.model.*;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Util class containing common operations required in consent initiation
 */
public class ConsentInitiationUtil {
    /**
     * Method to construct account initiation response.
     *
     * @param requestBody
     * @param payload
     * @param headers
     * @param isRedirectPreferred
     * @param apiVersion
     * @param isSCARequired
     * @return
     * @throws ServerException
     */
    public static void buildEnrichedConsentInitiationResponse(String consentType,
                                                              EnrichConsentCreationRequestBody requestBody,
                                                              JSONObject payload,
                                                              JSONObject headers,
                                                              boolean isRedirectPreferred,
                                                              String apiVersion, boolean isSCARequired)
            throws ServerException {

        String resourcePath = requestBody.getData().getConsentResourcePath();
        String locationString = String.format(ConsentExtensionConstants.SELF_LINK_TEMPLATE,
                apiVersion, resourcePath, requestBody.getData().getConsentId());
        headers.put(ConsentExtensionConstants.LOCATION_HEADER, locationString);

        Map<String, Object> scaElements = CommonConsentValidationUtil.getScaApproachAndMethods(isRedirectPreferred,
                isSCARequired);
        ScaApproach scaApproach = (ScaApproach) scaElements.get(CommonConstants.SCA_APPROACH_KEY);
        ArrayList<ScaMethod> scaMethods =
                (ArrayList<ScaMethod>) scaElements.get(CommonConstants.SCA_METHODS_KEY);
        headers.put(ConsentExtensionConstants.ASPSP_SCA_APPROACH, scaApproach.getApproach().toString());

        StoredDetailedConsentResourceData createdConsent = requestBody.getData().getConsentResource();

        if (ConsentTypeEnum.ACCOUNTS.toString().equals(consentType)) {
            AccountConsentUtil.appendAccountInitiationResponseToPayload(createdConsent, scaMethods, payload);
        } else if (ConsentTypeEnum.PAYMENTS.toString().equals(consentType)) {
            PaymentConsentUtil.appendPaymentInitiationResponseToPayload(createdConsent, scaMethods, payload);
        } else if (ConsentTypeEnum.FUNDS_CONFIRMATION.toString().equals(consentType)) {
            FundsConfirmationConsentUtil.appendPaymentInitiationResponseToPayload(createdConsent, scaMethods, payload);
        }

        String authId = null;

        // Always only one auth resource is created for implicit initiation
        List<StoredAuthorization> authResources = createdConsent.getAuthorizations();
        StoredAuthorization implicitAuthResource = authResources.get(0);
        authId = implicitAuthResource.getId();

        JSONObject links = getInitiationLinks(false, scaApproach,
                scaMethods, resourcePath, createdConsent.getId(), authId, consentType);

        payload.put(ConsentExtensionConstants.LINKS, links);
    }

    /**
     * Constructs the links object for initiation responses.
     *
     * @param isTppExplicitAuthorisationPreferred is explicit authorisation
     * @param currentScaApproach                  current SCA approach
     * @param currentScaMethods                   current SCA methods
     * @param requestPath                         request path of initiation
     * @param consentId                           consent/payment consentId
     * @param authorisationId                     authorisation resource consentId
     * @param consentType                         type of consent
     * @return constructed links object for initiation response
     */
    public static JSONObject getInitiationLinks(boolean isTppExplicitAuthorisationPreferred, ScaApproach currentScaApproach, List<ScaMethod> currentScaMethods,
                                                String requestPath, String consentId, String authorisationId,
                                                String consentType) throws ServerException {
        JSONObject links = new JSONObject();

        String apiVersion = CommonConsentValidationUtil.getApiVersion(consentType);

        String selfLink = String.format(ConsentExtensionConstants.SELF_LINK_TEMPLATE,
                apiVersion, requestPath, consentId);
        JSONObject self = new JSONObject();
        self.put(ConsentExtensionConstants.HREF, selfLink);
        links.put(ConsentExtensionConstants.SELF, self);

        String statusLink = String.format(ConsentExtensionConstants.STATUS_LINK_TEMPLATE,
                apiVersion, requestPath, consentId);
        JSONObject status = new JSONObject();
        status.put(ConsentExtensionConstants.HREF, statusLink);
        links.put(ConsentExtensionConstants.STATUS, status);

        if (!isTppExplicitAuthorisationPreferred) {
            // Implicit authorisation
            String authResourceLink = String.format(ConsentExtensionConstants.AUTH_RESOURCE_LINK_TEMPLATE,
                    apiVersion, requestPath, consentId, authorisationId);
            if (ScaApproachEnum.REDIRECT.equals(currentScaApproach.getApproach())) {
                // Implicit REDIRECT approach
                String wellKnown = ConfigurableProperties.OAUTH_METADATA_ENDPOINT;
                JSONObject scaOAuth = new JSONObject();
                scaOAuth.put(ConsentExtensionConstants.HREF, wellKnown);
                links.put(ConsentExtensionConstants.SCA_OAUTH, scaOAuth);

                JSONObject scaStatus = new JSONObject();
                scaStatus.put(ConsentExtensionConstants.HREF, authResourceLink);
                links.put(ConsentExtensionConstants.SCA_STATUS, scaStatus);
            } else {
                // Implicit but SCA approach not decided
                if (currentScaMethods.size() > 1) {
                    // If SCA is required and has more than 1 current SCA method
                    JSONObject selectAuthMethod = new JSONObject();
                    selectAuthMethod.put(ConsentExtensionConstants.HREF, authResourceLink);
                    links.put(ConsentExtensionConstants.SELECT_AUTH_METHOD, selectAuthMethod);
                }
            }
        } else {
            // Explicit authorisation not supported
            // Should be unreachable since this is validated upon consent creation
            // ToDo: revisit once auth resources can be added explicitly
            throw new ServerException(ServerException.ErrorCode.BAD_REQUEST, ErrorUtil.constructBerlinError(
                    null, TPPMessage.CategoryEnum.ERROR, TPPMessage.CodeEnum.SERVICE_INVALID_405,
                    ErrorConstants.EXPLICIT_AUTH_NOT_SUPPORTED));
            /*
            String startAuthorisationsLink = String.format(ConsentExtensionConstants.START_AUTH_LINK_TEMPLATE,
                    apiVersion, requestPath, consentId);
            if (ScaApproachEnum.REDIRECT.equals(currentScaApproach.getApproach())) {
                // Explicit REDIRECT approach
                JSONObject startAuthWithPSUIdentification = new JSONObject();
                startAuthWithPSUIdentification.put(ConsentExtensionConstants.HREF, startAuthorisationsLink);
                links.put(ConsentExtensionConstants.START_AUTH_WITH_PSU_IDENTIFICATION, startAuthWithPSUIdentification);
            } else {
                // Explicit but SCA approach not decided
                if (currentScaMethods.size() > 1) {
                    // If SCA is required and has more than 1 current SCA method

                    JSONObject startAuthWithAuthMethod = new JSONObject();
                    startAuthWithAuthMethod.put(ConsentExtensionConstants.HREF, startAuthorisationsLink);
                    links.put(ConsentExtensionConstants.START_AUTH_WITH_AUTH_METHOD_SELECTION, startAuthWithAuthMethod);
                }
            }
             */
        }

        return links;
    }

    /**
     * Build response alteration response for success consent initiations
     *
     * @param requestBody
     * @param validationResponse
     * @param consentType
     */
    static void buildResponseAlterationResponseForConsentCreation(EnrichConsentCreationRequestBody requestBody,
                                                                  SuccessResponseForResponseAlternation validationResponse,
                                                                  String consentType) {
        JSONObject payloadToSend = new JSONObject();
        JSONObject headersToSend = new JSONObject();

        String apiVersion = CommonConsentValidationUtil.getApiVersion(consentType);
        boolean isScaRequired = Boolean.parseBoolean(ConfigurableProperties.IS_SCA_REQUIRED);

        buildEnrichedConsentInitiationResponse(consentType, requestBody, payloadToSend, headersToSend,
                false, apiVersion, isScaRequired);

        SuccessResponseForResponseAlternationData data = new SuccessResponseForResponseAlternationData();
        data.setResponseHeaders(headersToSend);
        data.setModifiedResponse(payloadToSend);

        validationResponse.setResponseId(requestBody.getRequestId());
        validationResponse.setStatus(SuccessResponseForResponseAlternation.StatusEnum.SUCCESS);
        validationResponse.setData(data);
    }
}
