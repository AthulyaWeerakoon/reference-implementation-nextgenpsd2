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
import org.wso2.openbanking.nextgenpsd2.extensions.constants.ErrorConstants;
import org.wso2.openbanking.nextgenpsd2.extensions.constants.ExtensionEnums;
import org.wso2.openbanking.nextgenpsd2.extensions.exceptions.ExtensionException;
import org.wso2.openbanking.nextgenpsd2.extensions.exceptions.ValidationFailureException;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.StoredBasicConsentResourceData;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.StoredDetailedConsentResourceData;
import org.wso2.openbanking.nextgenpsd2.extensions.model.ScaMethod;
import org.wso2.openbanking.nextgenpsd2.extensions.model.TPPMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;

/**
 * Utility class for Account consent management.
 */
public class AccountConsentUtil {

    /**
     * Method to get the account initiation response without links.
     *
     * @param createdConsent the created consent
     * @param scaMethods     decided SCA methods
     */
    public static void appendAccountInitiationResponseToPayload(StoredDetailedConsentResourceData createdConsent,
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
        } else if (scaMethods.size() == 1) {
            payload.put(CommonConstants.CHOSEN_SCA_METHOD, chosenSCAMethods.get(0));
        }
    }

    /**
     * Converts a given date to a UTC timestamp.
     *
     * @param date date in string format
     * @return date/time after converting to UTC timestamp
     */
    public static long convertToUtcTimestamp(String date) throws ValidationFailureException, ExtensionException {

        LocalDate localDate = CommonConsentValidationUtil.parseDateToISO(date,
                TPPMessage.CodeEnum.FORMAT_ERROR, ErrorConstants.VALID_UNTIL_DATE_INVALID);
        LocalDateTime localDateTime = localDate.atStartOfDay();
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of(CommonConstants.UTC));

        // Retrieve the UTC timestamp in long.
        return Instant.from(zonedDateTime).getEpochSecond();
    }

    /**
     * Checks if consent is expired based on validUntilDate.
     *
     * @param validUntilDate valid until time in epoch seconds
     * @return whether consent is expired or not
     */
    public static boolean isConsentExpired(long validUntilDate) {
        LocalDateTime expDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(validUntilDate), ZoneOffset.UTC);
        LocalDate expDate = expDateTime.toLocalDate();

        LocalDate currDate = LocalDate.now(ZoneOffset.UTC);

        return currDate.isAfter(expDate);
    }

    /**
     * Method to construct accounts consent get response.
     *
     * @param retrievedConsent consent object
     */
    public static void extendAccountConsentGetResponse(StoredBasicConsentResourceData retrievedConsent,
                                                       JSONObject payloadToSend) {

        AccountConsentUtil.addAdditionalAccountConsentAttributes(retrievedConsent, payloadToSend);
        payloadToSend.put(CommonConstants.LINKS, getAccountConsentGetLinks());
    }

    /**
     * Constructs the links object for account consent get responses.
     *
     * @return constructed links for account consent get responses
     */
    public static JSONObject getAccountConsentGetLinks() {

        JSONObject links = new JSONObject();

        String apiVersion = CommonConsentValidationUtil.getApiVersion(
                ExtensionEnums.ConsentTypeEnum.ACCOUNTS.toString());

        JSONObject account = new JSONObject();
        account.put(CommonConstants.HREF,
                String.format(CommonConstants.ACCOUNTS_LINK_TEMPLATE, apiVersion));
        links.put(CommonConstants.ACCOUNT, account);

        return links;
    }

    /**
     * Method to get the account consent get response without links.
     *
     * @param retrievedConsent consent object
     */
    public static void addAdditionalAccountConsentAttributes(StoredBasicConsentResourceData retrievedConsent,
                                                             JSONObject payloadToSend) {

        payloadToSend.put(CommonConstants.CONSENT_STATUS, retrievedConsent.getStatus());

        Date currentDate = new Date(retrievedConsent.getUpdatedTime() * 1000L);
        DateFormat dateFormat = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        String lastActionDate = dateFormat.format(currentDate);

        payloadToSend.put(CommonConstants.LAST_ACTION_DATE, lastActionDate);
    }
}
