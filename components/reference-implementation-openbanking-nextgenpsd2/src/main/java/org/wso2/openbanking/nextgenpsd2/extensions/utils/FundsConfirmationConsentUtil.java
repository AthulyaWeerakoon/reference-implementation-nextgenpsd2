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
import org.json.JSONObject;
import org.wso2.openbanking.nextgenpsd2.extensions.constants.CommonConstants;
import org.wso2.openbanking.nextgenpsd2.extensions.exceptions.ExtensionException;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.StoredDetailedConsentResourceData;
import org.wso2.openbanking.nextgenpsd2.extensions.model.ScaMethod;

import java.util.ArrayList;

/**
 * Utility class for payment consent management.
 */
public class FundsConfirmationConsentUtil {

    /**
     * Method to get the funds confirmation initiation response without links.
     *
     * @param createdConsent created consent retrieved
     * @param scaMethods
     * @param payload
     */
    public static void appendCoFInitiationResponseToPayload(StoredDetailedConsentResourceData createdConsent,
                                                            ArrayList<ScaMethod> scaMethods, JSONObject payload)
            throws ExtensionException {
        payload.put(CommonConstants.CONSENT_STATUS, createdConsent.getStatus());
        payload.put(CommonConstants.CONSENT_ID, createdConsent.getId());

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
}
