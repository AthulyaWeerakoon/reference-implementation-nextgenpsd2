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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.openbanking.nextgenpsd2.extensions.constants.CommonConstants;
import org.wso2.openbanking.nextgenpsd2.extensions.constants.ErrorConstants;
import org.wso2.openbanking.nextgenpsd2.extensions.exceptions.ExtensionException;
import org.wso2.openbanking.nextgenpsd2.extensions.exceptions.ValidationFailureException;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.StoredDetailedConsentResourceData;
import org.wso2.openbanking.nextgenpsd2.extensions.model.BulkPaymentInitiationPayload;
import org.wso2.openbanking.nextgenpsd2.extensions.model.PeriodicPaymentInitiationPayload;
import org.wso2.openbanking.nextgenpsd2.extensions.model.ScaMethod;
import org.wso2.openbanking.nextgenpsd2.extensions.model.SinglePaymentInitiationPayload;
import org.wso2.openbanking.nextgenpsd2.extensions.model.TPPMessage;

import java.util.ArrayList;

import javax.ws.rs.core.Response;

/**
 * Utility class for payment consent management.
 */
public class PaymentConsentUtil {

    /**
     * Validates payment consent initiation payload based on payment type.
     *
     * @param requestPayload consent initiation request payload
     * @param resourcePath called resource path
     * @throws ValidationFailureException if the payment initiation payload is invalid
     */
    public static void validatePaymentInitiationPayload(JSONObject requestPayload, String resourcePath)
            throws ValidationFailureException, ExtensionException {
        switch (CommonConsentValidationUtil.getServiceDifferentiatingRequestPath(resourcePath)) {
            case CommonConstants.PAYMENTS_SERVICE_PATH:
                CommonConsentValidationUtil
                        .validateJSONFromModel(requestPayload.toString(), SinglePaymentInitiationPayload.class);
                break;

            case CommonConstants.BULK_PAYMENTS_SERVICE_PATH:
                CommonConsentValidationUtil
                        .validateJSONFromModel(requestPayload.toString(), BulkPaymentInitiationPayload.class);
                break;

            case CommonConstants.PERIODIC_PAYMENTS_SERVICE_PATH:
                CommonConsentValidationUtil
                        .validateJSONFromModel(requestPayload.toString(), PeriodicPaymentInitiationPayload.class);
                break;

            default:
                // Execution shouldn't reach here given that path is validated prior to reaching this point
                throw new ValidationFailureException(ValidationFailureException.ErrorCode.INTERNAL_SERVER_ERROR,
                        ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                                TPPMessage.CodeEnum.RESOURCE_UNKNOWN_404, ErrorConstants.PATH_INVALID));
        }
    }

    /**
     * Method to get the payment initiation response without links.
     *
     * @param createdConsent created consent resource data
     * @param scaMethods supported SCA methods
     */
    public static void appendPaymentInitiationResponseToPayload(StoredDetailedConsentResourceData createdConsent,
                                                                ArrayList<ScaMethod> scaMethods, JSONObject payload)
            throws ExtensionException {

        payload.put(CommonConstants.TRANSACTION_STATUS, createdConsent.getStatus());
        payload.put(CommonConstants.PAYMENT_ID, createdConsent.getId());

        JSONArray chosenSCAMethods = new JSONArray();
        for (ScaMethod scaMethod : scaMethods) {
            chosenSCAMethods.put(CommonConsentValidationUtil.convertObjectToJson(scaMethod));
        }

        if (scaMethods.size() > 1) {
            payload.put(CommonConstants.SCA_METHODS, chosenSCAMethods);
        } else {
            payload.put(CommonConstants.CHOSEN_SCA_METHOD, chosenSCAMethods.get(0));
        }
    }

    /**
     * Method to return a JSON Object with payment product.
     *
     * @param consentResourcePath resource path of created consent
     * @return attribute object
     */
    public static Object getPaymentProductAttribute(String consentResourcePath) {
        JSONObject attributesJSON = new JSONObject();
        attributesJSON.put(CommonConstants.PAYMENT_PRODUCT_CC, getPaymentProduct(consentResourcePath));
        return attributesJSON;
    }

    /**
     * Helper method to extract payment product from resource path.
     *
     * @param consentResourcePath payment consent resource path
     * @return payment product
     */
    public static String getPaymentProduct(String consentResourcePath) {
        return consentResourcePath.split("/")[1];
    }

    /**
     * Method to validate payment product for payment consents.
     *
     * @param attributes consent attribute object
     * @param consentResourcePath consent resource path
     * @throws ValidationFailureException if stored payment product does not match one specified in consent resource
     * path
     * @throws ExtensionException is payment product is missing in consent attributes
     */
    public static void validatePaymentProductFromAttributes(Object attributes, String consentResourcePath)
            throws ValidationFailureException, ExtensionException {
        String paymentProductFromPath = getPaymentProduct(consentResourcePath);

        // Extract payment product from attributes
        String paymentProductFromAttributes;
        try {
            JSONObject attributesJSON = CommonConsentValidationUtil.convertObjectToJson(attributes);
            paymentProductFromAttributes = attributesJSON.getString(CommonConstants.PAYMENT_PRODUCT_CC);
        } catch (JSONException e) {
            // Should be unreachable as payment product gets added as an attribute at initiation
            throw new ExtensionException(Response.Status.BAD_REQUEST, "invalid_request",
                    "Payment product not stored at consent initiation. Product validation failed.");
        }

        if (!paymentProductFromAttributes.equals(paymentProductFromPath)) {
            throw new ValidationFailureException(ValidationFailureException.ErrorCode.BAD_REQUEST,
                    ErrorUtil.constructBerlinError(
                    null, TPPMessage.CategoryEnum.ERROR, TPPMessage.CodeEnum.PRODUCT_INVALID,
                    "The provided consent ID valid but belongs to a different payment product"));
        }
    }
}
