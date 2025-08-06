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

package org.wso2.openbanking.nextgenpsd2.extensions.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Common constants.
 */
public class CommonConstants {

    // Config tag constants
    public static final String SCA_TYPE = "Type";
    public static final String SCA_VERSION = "Version";
    public static final String SCA_ID = "Id";
    public static final String SCA_NAME = "Name";
    public static final String SCA_MAPPED_APPROACH = "MappedApproach";
    public static final String SCA_DESCRIPTION = "Description";
    public static final String SCA_DEFAULT = "Default";
    public static final String X_WSO2_CLIENT_ID_KEY = "x-wso2-client-id";
    public static final String SCA_APPROACH_KEY = "SCA-Approach";
    public static final String SCA_METHODS_KEY = "SCA-Methods";
    public static final String CONSENT_ID = "consentId";
    public static final String PAYMENT_ID = "paymentId";
    public static final String CURRENCY = "currency";
    public static final String EXPLICIT_AUTHORISATION_PATH_END = "authorisations";
    public static final String PSU_IP_ADDRESS_PROPER_CASE_HEADER = "PSU-IP-Address";
    public static final String PSU_IP_ADDRESS_HEADER = "psu-ip-address";
    public static final String PSU_ID_HEADER = "psu-id";
    // Constants that are common to all request types
    public static final String TPP_REDIRECT_PREFERRED_HEADER = "tpp-redirect-preferred";
    public static final String LAST_ACTION_DATE = "lastActionDate";
    public static final String CONSENT_STATUS = "consentStatus";
    public static final String CHOSEN_SCA_METHOD = "chosenScaMethod";
    public static final String SCA_METHODS = "scaMethods";
    public static final String LINKS = "_links";
    public static final String HREF = "href";
    public static final String SELF_LINK_TEMPLATE = "/%s/%s/%s";
    public static final String STATUS_LINK_TEMPLATE = "/%s/%s/%s/status";
    public static final String AUTH_RESOURCE_LINK_TEMPLATE = "/%s/%s/%s/authorisations/%s";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String UTC = "UTC";
    // Original proper case header constants
    public static final String LOCATION_HEADER = "Location";
    public static final String X_REQUEST_ID_PROPER_CASE_HEADER = "X-Request-ID";
    public static final String X_REQUEST_ID_HEADER = "x-request-id";
    // Constants that are used by accounts service
    public static final String ACCOUNTS_CONSENT_PATH = "consents";
    public static final String MAXIMUM_VALID_DATE = "9999-12-31";
    public static final String ALL_ACCOUNTS = "allAccounts";
    public static final String ALL_ACCOUNTS_WITH_OWNER_NAME = "allAccountsWithOwnerName";
    public static final String ACCOUNT = "account";
    public static final String ACCOUNTS_LINK_TEMPLATE = "/%s/accounts";
    // Constants that are used by payments service
    public static final String PAYMENTS_SERVICE_PATH = "payments";
    public static final String BULK_PAYMENTS_SERVICE_PATH = "bulk-payments";
    public static final String PERIODIC_PAYMENTS_SERVICE_PATH = "periodic-payments";
    public static final String PAYMENT_EXPLICIT_CANCELLATION_AUTHORISATION_PATH_END = "cancellation-authorisations";
    public static final String FOLLOWING_EXECUTION_RULE = "following";
    public static final String PRECEDING_EXECUTION_RULE = "preceding";
    public static final String IBAN = "iban";
    public static final String BBAN = "bban";
    public static final String PAN = "pan";
    public static final String MASKED_PAN = "maskedPan";
    public static final String MSISDN = "msisdn";
    public static final String PAYMENT_PRODUCT_CC = "paymentProduct";
    public static final String TRANSACTION_STATUS = "transactionStatus";
    public static final String ASPSP_SCA_APPROACH = "ASPSP-SCA-Approach";
    public static final List<String> SUPPORTED_PERIODIC_PAYMENT_FREQUENCY_CODES = Collections
            .unmodifiableList(Arrays.asList(
                    "Daily", "Weekly", "EveryTwoWeeks",
                    "Monthly", "EveryTwoMonths", "Quarterly",
                    "SemiAnnual", "Annual", "MonthlyVariable"
            ));
    // Payment services
    public static final String PAYMENTS = "payments";
    // Constants that are used by funds confirmation service
    public static final String FUNDS_CONFIRMATIONS_SERVICE_PATH = "confirmation-of-funds";
    // Response related constants
    public static final String SELF = "self";
    public static final String STATUS = "status";
    public static final String SCA_OAUTH = "scaOAuth";
    public static final String SCA_STATUS = "scaStatus";
    public static final String SELECT_AUTH_METHOD = "selectAuthenticationMethod";
}
