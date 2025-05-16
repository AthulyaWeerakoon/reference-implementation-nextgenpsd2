package com.wso2.openbanking.berlin.extensions.utils;

import com.wso2.openbanking.berlin.extensions.configurations.ConfigurableProperties;
import com.wso2.openbanking.berlin.extensions.datamodels.ScaMethod;
import com.wso2.openbanking.berlin.extensions.datamodels.TPPMessage;
import com.wso2.openbanking.berlin.extensions.enums.AccessMethodEnum;
import com.wso2.openbanking.berlin.extensions.enums.PermissionEnum;
import com.wso2.openbanking.berlin.extensions.exceptions.FailedValidationException;
import com.wso2.openbanking.berlin.extensions.model.StoredDetailedConsentResourceData;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Utility class for Account consent management
 */
public class AccountConsentUtil {
    public static final Log log = LogFactory.getLog(AccountConsentUtil.class);
    
    /**
     * Helper method to validate account initiation payload.
     *
     * @param payload
     * @throws FailedValidationException
     * @return permission
     */
    public static String validateAccountInitiationPayloadAndGetPermission(JSONObject payload) throws
            FailedValidationException {
        int configuredMinimumFreqPerDay = Integer.parseInt(ConfigurableProperties.FREQ_PER_DAY);
        boolean isValidUntilDateCapEnabled = Boolean.parseBoolean(ConfigurableProperties.VALID_UNTIL_DATE_CAP_ENABLED);
        int validUntilDaysCap = Integer.parseInt(ConfigurableProperties.VALID_UNTIL_DAYS);

        JSONObject accessObject = payload.optJSONObject(ConsentExtensionConstants.ACCESS);

        log.debug("Validating mandatory request body elements");
        if (accessObject == null
                || !payload.has(ConsentExtensionConstants.RECURRING_INDICATOR)
                || !payload.has(ConsentExtensionConstants.VALID_UNTIL)
                || !payload.has(ConsentExtensionConstants.FREQUENCY_PER_DAY)
                || !payload.has(ConsentExtensionConstants.COMBINED_SERVICE_INDICATOR)) {
            log.error(ErrorConstants.MANDATORY_ELEMENTS_MISSING);
            throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                    ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                            TPPMessage.CodeEnum.FORMAT_ERROR,ErrorConstants.MANDATORY_ELEMENTS_MISSING));
        }

        log.debug("Validating mandatory access object attributes");
        // At least one of these attributes must be there for the access object to be valid
        if (!accessObject.has(AccessMethodEnum.ACCOUNTS.toString())
                && !accessObject.has(AccessMethodEnum.BALANCES.toString())
                && !accessObject.has(AccessMethodEnum.TRANSACTIONS.toString())
                && !accessObject.has(PermissionEnum.AVAILABLE_ACCOUNTS.toString())
                && !accessObject.has(PermissionEnum.AVAILABLE_ACCOUNTS_WITH_BALANCES.toString())
                && !accessObject.has(PermissionEnum.ALL_PSD2.toString())) {
            log.error(ErrorConstants.ACCESS_OBJECT_MANDATORY_ELEMENTS_MISSING);
            throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                    ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                            TPPMessage.CodeEnum.FORMAT_ERROR, ErrorConstants.ACCESS_OBJECT_MANDATORY_ELEMENTS_MISSING));
        }

        log.debug("Validating additionalInformation attribute");
        //  It can only be present with at least one of the major access attributes (accounts, balances, transactions)
        if (accessObject.has(ConsentExtensionConstants.ADDITIONAL_INFORMATION)
                && !(accessObject.has(AccessMethodEnum.ACCOUNTS.toString())
                || accessObject.has(AccessMethodEnum.BALANCES.toString())
                || accessObject.has(AccessMethodEnum.TRANSACTIONS.toString()))) {
            log.error(ErrorConstants.INVALID_USE_OF_ADDITIONAL_INFO_ATTRIBUTE);
            throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                    ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                            TPPMessage.CodeEnum.FORMAT_ERROR, ErrorConstants.INVALID_USE_OF_ADDITIONAL_INFO_ATTRIBUTE));
        }

        log.debug("Validating account permissions");
        String permission = getPermissionByValidatingAccountAccessAttribute(accessObject);

        if (StringUtils.equals(permission, PermissionEnum.DEDICATED_ACCOUNTS.toString())) {
            log.debug("Validating account reference objects");
            JSONArray accounts = accessObject.optJSONArray(AccessMethodEnum.ACCOUNTS.toString());
            JSONArray balances = accessObject.optJSONArray(AccessMethodEnum.BALANCES.toString());
            JSONArray transactions = accessObject.optJSONArray(AccessMethodEnum.TRANSACTIONS.toString());

            validateAccountRefObjects(accounts);
            validateAccountRefObjects(balances);
            validateAccountRefObjects(transactions);
        }


        log.debug("Validating frequency per day and recurring indicator");
        if (payload.getInt(ConsentExtensionConstants.FREQUENCY_PER_DAY) < 1) {
            log.error(ErrorConstants.INVALID_FREQ_PER_DAY);
            throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                    ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                            TPPMessage.CodeEnum.FORMAT_ERROR, ErrorConstants.INVALID_FREQ_PER_DAY));
        }

        if (!(payload.getBoolean(ConsentExtensionConstants.RECURRING_INDICATOR))
                && payload.getInt(ConsentExtensionConstants.FREQUENCY_PER_DAY) > 1) {
            log.error(ErrorConstants.INVALID_FREQ_PER_DAY_COUNT);
            throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                    ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                            TPPMessage.CodeEnum.FORMAT_ERROR, ErrorConstants.INVALID_FREQ_PER_DAY_COUNT));
        }

        if (payload.getBoolean(ConsentExtensionConstants.RECURRING_INDICATOR) && configuredMinimumFreqPerDay >
                payload.getInt(ConsentExtensionConstants.FREQUENCY_PER_DAY)) {
            String errorMessageTemplate = "Frequency per day for recurring consent is lesser than the supported " +
                    "minimum value %s";
            if (log.isDebugEnabled()) {
                log.debug(String.format(errorMessageTemplate, configuredMinimumFreqPerDay));
            }
            throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                    ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                            TPPMessage.CodeEnum.FORMAT_ERROR, String.format(errorMessageTemplate,
                                    configuredMinimumFreqPerDay)));
        }

        log.debug("Validating valid until");
        if (payload.has(ConsentExtensionConstants.VALID_UNTIL)) {
            payload.put(ConsentExtensionConstants.VALID_UNTIL, getValidatedValidUntil(payload
                    .getString(ConsentExtensionConstants.VALID_UNTIL), isValidUntilDateCapEnabled, validUntilDaysCap));
        }

        log.debug("Validating combined service indicator");
        // (Not supported)
        if (payload.getBoolean(ConsentExtensionConstants.COMBINED_SERVICE_INDICATOR)) {
            log.error(ErrorConstants.COMBINED_SERVICE_INDICATOR_NOT_SUPPORTED);
            throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                    ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                            TPPMessage.CodeEnum.SESSIONS_NOT_SUPPORTED,
                            ErrorConstants.COMBINED_SERVICE_INDICATOR_NOT_SUPPORTED));
        }

        return permission;
    }

    /**
     * Method to get the account initiation response without links.
     *
     * @param createdConsent the created consent
     * @param scaMethods     decided SCA methods
     * @return the constructed initiation response without links
     */
    public static void appendAccountInitiationResponseToPayload(StoredDetailedConsentResourceData createdConsent,
                                                                ArrayList<ScaMethod> scaMethods, JSONObject payload) {

        payload.put(ConsentExtensionConstants.CONSENT_STATUS, createdConsent.getStatus());
        payload.put(ConsentExtensionConstants.CONSENT_ID, createdConsent.getId());

        JSONArray chosenSCAMethods = new JSONArray();
        for (ScaMethod scaMethod : scaMethods) {
            chosenSCAMethods.put(CommonConsentValidationUtil.convertObjectToJson(scaMethod));
        }

        if (scaMethods.size() > 1) {
            payload.put(ConsentExtensionConstants.SCA_METHODS, chosenSCAMethods);
        } else if (scaMethods.size() == 1) {
            payload.put(ConsentExtensionConstants.CHOSEN_SCA_METHOD, chosenSCAMethods.get(0));
        }
    }

    /**
     * Validates the access attribute by checking the combinations of sub-attributes that can be present
     * and returns the permission only relevant for 'Account List of Available Accounts' and 'Global' type consents.
     *
     * @param accessObject access attribute of the request body
     * @return permission
     */
    public static String getPermissionByValidatingAccountAccessAttribute(JSONObject accessObject) throws
            FailedValidationException {
        String availableAccounts = accessObject.optString(PermissionEnum.AVAILABLE_ACCOUNTS.toString(), null);
        String availableAccountsWithBalances = accessObject
                .optString(PermissionEnum.AVAILABLE_ACCOUNTS_WITH_BALANCES.toString(), null);
        String allPsd2 = accessObject.optString(PermissionEnum.ALL_PSD2.toString(), null);

        if (!accessObject.has(AccessMethodEnum.ACCOUNTS.toString())
                && !accessObject.has(AccessMethodEnum.BALANCES.toString())
                && !accessObject.has(AccessMethodEnum.TRANSACTIONS.toString())) {
            if (ConsentExtensionConstants.ALL_ACCOUNTS.equals(availableAccounts)
                    || ConsentExtensionConstants.ALL_ACCOUNTS_WITH_OWNER_NAME.equals(availableAccounts)) {
                if (availableAccountsWithBalances != null || allPsd2 != null) {
                    log.error("availableAccounts permission cannot be set with availableAccountsWithBalances " +
                            "or allPsd2 permissions");
                    throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                            ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                                    TPPMessage.CodeEnum.FORMAT_ERROR, ErrorConstants.INVALID_PERMISSION));
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug(String.format("Account permission is set to %s ",
                                PermissionEnum.AVAILABLE_ACCOUNTS));
                    }
                    return PermissionEnum.AVAILABLE_ACCOUNTS.toString();
                }
            }
            if (ConsentExtensionConstants.ALL_ACCOUNTS.equals(availableAccountsWithBalances)
                    || ConsentExtensionConstants.ALL_ACCOUNTS_WITH_OWNER_NAME.equals(availableAccountsWithBalances)) {
                if (availableAccounts != null || allPsd2 != null) {
                    log.error("availableAccountsWithBalances permission cannot be set with availableAccounts " +
                            "or allPsd2 permissions");
                    throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                            ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                                    TPPMessage.CodeEnum.FORMAT_ERROR, ErrorConstants.INVALID_PERMISSION));
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug(String.format("Account permission is set to %s ",
                                PermissionEnum.AVAILABLE_ACCOUNTS_WITH_BALANCES));
                    }
                    return PermissionEnum.AVAILABLE_ACCOUNTS_WITH_BALANCES.toString();
                }
            }
            if (ConsentExtensionConstants.ALL_ACCOUNTS.equals(allPsd2)
                    || ConsentExtensionConstants.ALL_ACCOUNTS_WITH_OWNER_NAME.equals(allPsd2)) {
                if (availableAccounts != null || availableAccountsWithBalances != null) {
                    log.error("allPsd2 permission cannot be set with availableAccounts or " +
                            "availableAccountsWithBalances permissions");
                    throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                            ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                                    TPPMessage.CodeEnum.FORMAT_ERROR, ErrorConstants.INVALID_PERMISSION));
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug(String.format("Account permission is set to %s ",
                                PermissionEnum.ALL_PSD2));
                    }
                    return PermissionEnum.ALL_PSD2.toString();
                }
            }
        } else {

            /* According to Berlin/Israel specifications, either all access arrays should be empty, or non-empty.
             * The following logic checks for this requirement.
             */
            int numberOfProvidedAccessTypes = 0;
            int numberOfEmptyAccessMethodArrays = 0;

            if (accessObject.opt(AccessMethodEnum.ACCOUNTS.toString()) != null) {
                numberOfProvidedAccessTypes++;
                JSONArray accounts = accessObject.optJSONArray(AccessMethodEnum.ACCOUNTS.toString());
                if (accounts != null && accounts.isEmpty()) {
                    numberOfEmptyAccessMethodArrays++;
                }
            }
            if (accessObject.opt(AccessMethodEnum.BALANCES.toString()) != null) {
                numberOfProvidedAccessTypes++;
                JSONArray balances = accessObject.optJSONArray(AccessMethodEnum.BALANCES.toString());
                if (balances != null && balances.isEmpty()) {
                    numberOfEmptyAccessMethodArrays++;
                }
            }
            if (accessObject.opt(AccessMethodEnum.TRANSACTIONS.toString()) != null) {
                numberOfProvidedAccessTypes++;
                JSONArray transactions = accessObject.optJSONArray(AccessMethodEnum.TRANSACTIONS.toString());
                if (transactions != null && transactions.isEmpty()) {
                    numberOfEmptyAccessMethodArrays++;
                }
            }

            if ((numberOfProvidedAccessTypes > numberOfEmptyAccessMethodArrays)
                    && numberOfEmptyAccessMethodArrays != 0) {
                // If either all arrays are not empty or not non-empty, an error is thrown.
                log.error("Either all arrays should be empty or non-empty");
                throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                        ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                                TPPMessage.CodeEnum.FORMAT_ERROR, ErrorConstants.INVALID_PERMISSION));
            }
            if (availableAccounts != null || availableAccountsWithBalances != null || allPsd2 != null) {
                log.error("Special permissions availableAccounts, availableAccountsWithBalances or allPsd2 " +
                        "cannot be applied when account, balances or transaction access is specified");
                throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                        ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                                TPPMessage.CodeEnum.FORMAT_ERROR, ErrorConstants.INVALID_PERMISSION));
            }

            if (numberOfProvidedAccessTypes == numberOfEmptyAccessMethodArrays) {
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Account permission is set to %s ", PermissionEnum.BANK_OFFERED));
                }
                return PermissionEnum.BANK_OFFERED.toString();
            } else {
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Account permission is set to %s ", PermissionEnum.DEDICATED_ACCOUNTS));
                }
                return PermissionEnum.DEDICATED_ACCOUNTS.toString();
            }
        }
        if (log.isDebugEnabled()) {
            log.debug(String.format("Account permission is set to %s ", PermissionEnum.DEDICATED_ACCOUNTS));
        }
        return PermissionEnum.DEDICATED_ACCOUNTS.toString();
    }

    /**
     * Validates the individual account references in case of dedicated accounts initiation.
     *
     * @param accountRefs account refs object array
     */
    public static void validateAccountRefObjects(JSONArray accountRefs) throws FailedValidationException {

        if (accountRefs != null) {
            for (Object accountRef : accountRefs) {
                JSONObject accountRefObject = (JSONObject) accountRef;
                CommonConsentValidationUtil.validateAccountRefObject(accountRefObject);
            }
        }
    }

    /**
     * Validate the requested date.
     * If a maximal available date is requested, a date in far future is to be used: “9999-12-31”.
     *
     * @param validUntil requested valid until date
     * @return allowed valid until date
     */
    public static String getValidatedValidUntil(String validUntil, boolean isValidUntilDateCapEnabled,
                                                int validUntilDaysCap) throws FailedValidationException {

        LocalDate validUntilDate = CommonConsentValidationUtil.parseDateToISO(validUntil,
                TPPMessage.CodeEnum.FORMAT_ERROR, ErrorConstants.VALID_UNTIL_DATE_INVALID);
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        if (validUntilDate.isBefore(today)) {
            String errorMessage = "validUntil has to be today, %s or a future date";
            log.error(String.format(errorMessage, today));
            throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                    ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                            TPPMessage.CodeEnum.TIMESTAMP_INVALID, String.format(errorMessage, today)));
        }
        LocalDate maximumValidUntil = LocalDate.parse(ConsentExtensionConstants.MAXIMUM_VALID_DATE);
        if (isValidUntilDateCapEnabled &&
                (validUntil.compareTo(today.
                        plusDays(validUntilDaysCap).
                        format(DateTimeFormatter.ISO_LOCAL_DATE)) > 0)) {
            /*
            If the valid until date cap is enabled;
            and if now plus the valid until days cap(now + valid until days cap) is still a valid date;
            the new valid until date will be now plus the valid until days cap(now + valid until days cap)
             */
            validUntil = LocalDateTime.now().plusDays(validUntilDaysCap).
                    format(DateTimeFormatter.ISO_LOCAL_DATE);
        } else if (validUntilDate.isAfter(maximumValidUntil)) {
            validUntil = ConsentExtensionConstants.MAXIMUM_VALID_DATE;
        }
        return validUntil;
    }

    /**
     * Converts a given date to a UTC timestamp.
     *
     * @param date date in string format
     * @return date/time after converting to UTC timestamp
     */
    public static long convertToUtcTimestamp(String date) throws FailedValidationException {

        LocalDate localDate = CommonConsentValidationUtil.parseDateToISO(date, TPPMessage.CodeEnum.FORMAT_ERROR,
                ErrorConstants.VALID_UNTIL_DATE_INVALID);
        LocalDateTime localDateTime = localDate.atStartOfDay();
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of(ConsentExtensionConstants.UTC));

        // Retrieve the UTC timestamp in long.
        return Instant.from(zonedDateTime).getEpochSecond();
    }
}
