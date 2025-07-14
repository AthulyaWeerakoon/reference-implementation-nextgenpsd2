/**
 * Copyright (c) 2025, WSO2 LLC. (https://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.openbanking.berlin.demo.backend.utils;

import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import static org.wso2.openbanking.berlin.demo.backend.utils.Constants.AccountService.CODE;
import static org.wso2.openbanking.berlin.demo.backend.utils.Constants.AccountService.MESSAGE;
import static org.wso2.openbanking.berlin.demo.backend.utils.Constants.AccountService.VALID;

import static javax.ws.rs.core.Response.Status.FORBIDDEN;

/**
 * Utility methods.
 */
public class Utility {

    private Utility() {

    }

    /**
     * Check if request ID and consent ID are set.
     *
     * @param requestID ID of the request.
     * @param consentID ID gotten after consent transaction.
     * @return JSON response with either an error message or "valid".
     */
    public static JSONObject validateRequestHeader(String requestID, String consentID) {

        JSONObject responseJSON = new JSONObject();
        if (StringUtils.isBlank(requestID)) {
            responseJSON.put(CODE, FORBIDDEN.getStatusCode());
            responseJSON.put(MESSAGE, "X-Request_ID not found");
            return responseJSON;
        } else if (StringUtils.isBlank(consentID)) {
            responseJSON.put(CODE, FORBIDDEN.getStatusCode());
            responseJSON.put(MESSAGE, "Consent-ID not found");
            return responseJSON;
        } else {
            responseJSON.put(MESSAGE, VALID);
            return responseJSON;
        }
    }

    /**
     * Check if request ID is set.
     *
     * @param requestID ID of the request.
     * @return JSON response with either an error message or "valid".
     */
    public static JSONObject validatePaymentRequestHeader(String requestID) {

        JSONObject responseJSON = new JSONObject();
        if (StringUtils.isBlank(requestID)) {
            responseJSON.put(CODE, FORBIDDEN.getStatusCode());
            responseJSON.put(MESSAGE, "X-Request_ID not found");
        } else {
            responseJSON.put(MESSAGE, VALID);
        }
        return responseJSON;
    }

}
