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

package org.wso2.openbanking.nextgenpsd2.extensions.utils;

import org.json.JSONObject;
import org.wso2.openbanking.nextgenpsd2.extensions.configurations.ConfigurationConstants;
import org.wso2.openbanking.nextgenpsd2.extensions.constants.CommonConstants;
import org.wso2.openbanking.nextgenpsd2.extensions.constants.ErrorConstants;
import org.wso2.openbanking.nextgenpsd2.extensions.constants.ExtensionEnums;
import org.wso2.openbanking.nextgenpsd2.extensions.exceptions.ExtensionException;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.EnrichConsentCreationRequestBody;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.StoredAuthorization;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.StoredDetailedConsentResourceData;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.SuccessResponseForResponseAlternation;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.SuccessResponseForResponseAlternationData;
import org.wso2.openbanking.nextgenpsd2.extensions.model.ScaApproach;
import org.wso2.openbanking.nextgenpsd2.extensions.model.ScaMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

/**
 * Util class containing common operations required in consent initiation.
 */
public class ConsentInitiationUtil {
    /**
     * Method to construct consent initiation response.
     *
     * @param requestBody consent initiation request body
     * @param payload consent initiation payload
     * @param headers consent initiation request headers
     * @param isRedirectPreferred 'is-redirect-preferred' header value
     * @param apiVersion api version based on consent
     * @param isSCARequired whether SCA is required as per configuration
     */
    public static void buildEnrichedConsentInitiationResponse(String consentType,
                                                              EnrichConsentCreationRequestBody requestBody,
                                                              JSONObject payload,
                                                              JSONObject headers,
                                                              boolean isRedirectPreferred,
                                                              String apiVersion, boolean isSCARequired)
            throws ExtensionException {

        String resourcePath = requestBody.getData().getConsentResourcePath();
        String locationString = String.format(CommonConstants.SELF_LINK_TEMPLATE,
                apiVersion, resourcePath, requestBody.getData().getConsentId());
        headers.put(CommonConstants.LOCATION_HEADER, locationString);

        Map<String, Object> scaElements = CommonConsentValidationUtil.getScaApproachAndMethods(isRedirectPreferred,
                isSCARequired);
        ScaApproach scaApproach = (ScaApproach) scaElements.get(CommonConstants.SCA_APPROACH_KEY);
        ArrayList<ScaMethod> scaMethods =
                (ArrayList<ScaMethod>) scaElements.get(CommonConstants.SCA_METHODS_KEY);
        headers.put(CommonConstants.ASPSP_SCA_APPROACH, scaApproach.getApproach().toString());

        StoredDetailedConsentResourceData createdConsent = requestBody.getData().getConsentResource();

        if (ExtensionEnums.ConsentTypeEnum.ACCOUNTS.toString().equals(consentType)) {
            AccountConsentUtil.appendAccountInitiationResponseToPayload(createdConsent, scaMethods, payload);
        } else if (ExtensionEnums.ConsentTypeEnum.PAYMENTS.toString().equals(consentType)) {
            PaymentConsentUtil.appendPaymentInitiationResponseToPayload(createdConsent, scaMethods, payload);
        } else if (ExtensionEnums.ConsentTypeEnum.FUNDS_CONFIRMATION.toString().equals(consentType)) {
            FundsConfirmationConsentUtil.appendCoFInitiationResponseToPayload(createdConsent, scaMethods, payload);
        }

        String authId = null;

        // Always only one auth resource is created for implicit initiation
        List<StoredAuthorization> authResources = createdConsent.getAuthorizations();
        StoredAuthorization implicitAuthResource = authResources.get(0);
        authId = implicitAuthResource.getId();

        JSONObject links = getInitiationLinks(false, scaApproach,
                scaMethods, resourcePath, createdConsent.getId(), authId, consentType);

        payload.put(CommonConstants.LINKS, links);
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
    public static JSONObject getInitiationLinks(boolean isTppExplicitAuthorisationPreferred,
                                                ScaApproach currentScaApproach, List<ScaMethod> currentScaMethods,
                                                String requestPath, String consentId, String authorisationId,
                                                String consentType) throws ExtensionException {
        JSONObject links = new JSONObject();

        String apiVersion = CommonConsentValidationUtil.getApiVersion(consentType);

        String selfLink = String.format(CommonConstants.SELF_LINK_TEMPLATE,
                apiVersion, requestPath, consentId);
        JSONObject self = new JSONObject();
        self.put(CommonConstants.HREF, selfLink);
        links.put(CommonConstants.SELF, self);

        String statusLink = String.format(CommonConstants.STATUS_LINK_TEMPLATE,
                apiVersion, requestPath, consentId);
        JSONObject status = new JSONObject();
        status.put(CommonConstants.HREF, statusLink);
        links.put(CommonConstants.STATUS, status);

        if (!isTppExplicitAuthorisationPreferred) {
            // Implicit authorisation
            String authResourceLink = String.format(CommonConstants.AUTH_RESOURCE_LINK_TEMPLATE,
                    apiVersion, requestPath, consentId, authorisationId);
            if (ExtensionEnums.ScaApproachEnum.REDIRECT.equals(currentScaApproach.getApproach())) {
                // Implicit REDIRECT approach
                String wellKnown = ConfigurationConstants.OAUTH_METADATA_ENDPOINT;
                JSONObject scaOAuth = new JSONObject();
                scaOAuth.put(CommonConstants.HREF, wellKnown);
                links.put(CommonConstants.SCA_OAUTH, scaOAuth);

                JSONObject scaStatus = new JSONObject();
                scaStatus.put(CommonConstants.HREF, authResourceLink);
                links.put(CommonConstants.SCA_STATUS, scaStatus);
            } else {
                // Implicit but SCA approach not decided
                if (currentScaMethods.size() > 1) {
                    // If SCA is required and has more than 1 current SCA method
                    JSONObject selectAuthMethod = new JSONObject();
                    selectAuthMethod.put(CommonConstants.HREF, authResourceLink);
                    links.put(CommonConstants.SELECT_AUTH_METHOD, selectAuthMethod);
                }
            }
        } else {
            // Explicit authorisation not supported
            // Should be unreachable since this is validated upon consent creation
            // ToDo: revisit once auth resources can be added explicitly
            throw new ExtensionException(Response.Status.BAD_REQUEST, "invalid_request",
                    ErrorConstants.EXPLICIT_AUTH_NOT_SUPPORTED);
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
     * Build response alteration response for success consent initiations.
     *
     * @param requestBody consent initiation enrichment request body
     * @param consentType consent type of initiated consent
     * @throws ExtensionException if the request body is malformed
     */
    public static SuccessResponseForResponseAlternation buildResponseAlterationResponseForConsentCreation(
            EnrichConsentCreationRequestBody requestBody, String consentType) throws ExtensionException {

        SuccessResponseForResponseAlternation enrichedResponse = new SuccessResponseForResponseAlternation();

        JSONObject payloadToSend = new JSONObject();
        JSONObject headersToSend = new JSONObject();

        String apiVersion = CommonConsentValidationUtil.getApiVersion(consentType);
        boolean isScaRequired = Boolean.parseBoolean(ConfigurationConstants.IS_SCA_REQUIRED);

        buildEnrichedConsentInitiationResponse(consentType, requestBody, payloadToSend, headersToSend,
                true, apiVersion, isScaRequired);

        SuccessResponseForResponseAlternationData data = new SuccessResponseForResponseAlternationData();
        data.setResponseHeaders(headersToSend);
        data.setModifiedResponse(payloadToSend);

        enrichedResponse.setResponseId(requestBody.getRequestId());
        enrichedResponse.setStatus(SuccessResponseForResponseAlternation.StatusEnum.SUCCESS);
        enrichedResponse.setData(data);

        return enrichedResponse;
    }
}
