package com.wso2.openbanking.berlin.extensions.utils;

import com.wso2.openbanking.berlin.extensions.configurations.ConfigurableProperties;
import com.wso2.openbanking.berlin.extensions.datamodels.ScaMethod;
import com.wso2.openbanking.berlin.extensions.datamodels.TPPMessage;
import com.wso2.openbanking.berlin.extensions.exceptions.FailedValidationException;
import com.wso2.openbanking.berlin.extensions.exceptions.ServerException;
import com.wso2.openbanking.berlin.extensions.model.StoredDetailedConsentResourceData;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/**
 * Utility class for payment consent management
 */
public class PaymentConsentUtil {

    private static final Log log = LogFactory.getLog(PaymentConsentUtil.class);

    /**
     * Method to validate whether the dates are consistent.
     *
     * @param startDate
     * @param endDate
     */
    public static void areDatesValid(LocalDate startDate, LocalDate endDate) throws FailedValidationException {

        if (endDate.compareTo(startDate) <= 0) {
            log.error(ErrorConstants.DATES_INCONSISTENT);
            throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                    ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                            TPPMessage.CodeEnum.FORMAT_ERROR, ErrorConstants.DATES_INCONSISTENT));
        }
    }

    /**
     * Validates payload of bulk payment consents
     *
     * @param requestPayload
     * @throws FailedValidationException
     */
    private static void validateBulkPaymentInitiation(JSONObject requestPayload) throws FailedValidationException {
        String maxPaymentExecutionDays = ConfigurableProperties.MAX_FUTURE_PAYMENT_DAYS;
        PaymentConsentUtil.validateDebtorAccount(requestPayload);

        if (requestPayload.opt(ConsentExtensionConstants.REQUESTED_EXECUTION_DATE) != null
                && requestPayload.opt(ConsentExtensionConstants.REQUESTED_EXECUTION_TIME) != null) {
            log.error(ErrorConstants.EXECUTION_DATE_TIME_ERROR);
            throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                    ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                            TPPMessage.CodeEnum.FORMAT_ERROR, ErrorConstants.EXECUTION_DATE_TIME_ERROR));
        }

        PaymentConsentUtil.validateRequestedExecutionDate(requestPayload, maxPaymentExecutionDays);

        JSONArray payments;

        log.debug("Validate presence of payment objects");
        if (requestPayload.opt(ConsentExtensionConstants.PAYMENTS) == null) {
            log.error(ErrorConstants.NO_PAYMENTS_IN_BODY);
            throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                    ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                            TPPMessage.CodeEnum.FORMAT_ERROR, ErrorConstants.NO_PAYMENTS_IN_BODY));
        } else {
            payments = requestPayload.getJSONArray(ConsentExtensionConstants.PAYMENTS);
            if (payments.isEmpty()) {
                log.error(ErrorConstants.EMPTY_PAYMENTS_ELEMENT);
                throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                        ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                                TPPMessage.CodeEnum.FORMAT_ERROR, ErrorConstants.EMPTY_PAYMENTS_ELEMENT));
            }
        }

        // Validate each payment object present in bulk payments payload
        validatePaymentElements(payments);
    }

    /**
     * Method to validate common payload elements.
     *
     * @param payload request payload
     */
    public static void validateCommonPaymentElements(JSONObject payload) throws FailedValidationException {
        log.debug("Validating payload for instructed amount");
        if (payload.opt(ConsentExtensionConstants.INSTRUCTED_AMOUNT) == null
                || !payload.has(ConsentExtensionConstants.INSTRUCTED_AMOUNT) ||
                !(payload.get(ConsentExtensionConstants.INSTRUCTED_AMOUNT) instanceof JSONObject)) {
            log.error(ErrorConstants.INSTRUCTED_AMOUNT_MISSING);
            throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                    ErrorUtil.constructBerlinError(null,
                            TPPMessage.CategoryEnum.ERROR, TPPMessage.CodeEnum.FORMAT_ERROR,
                            ErrorConstants.INSTRUCTED_AMOUNT_MISSING));
        } else {
            log.debug("Validate the amount and currency of instructed amount");
            JSONObject instructedAmountJson = payload.getJSONObject(ConsentExtensionConstants.INSTRUCTED_AMOUNT);
            if (instructedAmountJson.opt(ConsentExtensionConstants.CURRENCY) == null
                    || StringUtils.isBlank(instructedAmountJson.getString(ConsentExtensionConstants.CURRENCY))) {
                log.error(ErrorConstants.CURRENCY_CODE_MISSING);
                throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                        ErrorUtil.constructBerlinError(null,
                                TPPMessage.CategoryEnum.ERROR, TPPMessage.CodeEnum.FORMAT_ERROR,
                                ErrorConstants.CURRENCY_CODE_MISSING));
            }

            if (instructedAmountJson.opt(ConsentExtensionConstants.AMOUNT) == null
                    || StringUtils.isBlank(instructedAmountJson.getString(ConsentExtensionConstants.AMOUNT))) {
                log.error(ErrorConstants.AMOUNT_IS_MISSING);
                throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                        ErrorUtil.constructBerlinError(null,
                                TPPMessage.CategoryEnum.ERROR, TPPMessage.CodeEnum.FORMAT_ERROR,
                                ErrorConstants.AMOUNT_IS_MISSING));
            }
        }

        log.debug("Validating payload for creditor account");
        if (payload.opt(ConsentExtensionConstants.CREDITOR_ACCOUNT) == null) {
            log.error(ErrorConstants.CREDITOR_ACCOUNT_MISSING);
            throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                    ErrorUtil.constructBerlinError(null,
                            TPPMessage.CategoryEnum.ERROR, TPPMessage.CodeEnum.FORMAT_ERROR,
                            ErrorConstants.CREDITOR_ACCOUNT_MISSING));
        } else {
            JSONObject creditorAccountObject = payload.getJSONObject(ConsentExtensionConstants.CREDITOR_ACCOUNT);

            if (!creditorAccountObject.has(ConsentExtensionConstants.IBAN)
                    && !creditorAccountObject.has(ConsentExtensionConstants.BBAN)
                    && !creditorAccountObject.has(ConsentExtensionConstants.PAN)
                    && !creditorAccountObject.has(ConsentExtensionConstants.MASKED_PAN)
                    && !creditorAccountObject.has(ConsentExtensionConstants.MSISDN)) {

                log.error(ErrorConstants.CREDITOR_ACCOUNT_REFERENCE_MISSING);
                throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                        ErrorUtil.constructBerlinError(null,
                                TPPMessage.CategoryEnum.ERROR, TPPMessage.CodeEnum.FORMAT_ERROR,
                                ErrorConstants.CREDITOR_ACCOUNT_REFERENCE_MISSING));
            }
        }

        log.debug("Validating payload for creditor name");
        if (payload.opt(ConsentExtensionConstants.CREDITOR_NAME) == null
                || StringUtils.isBlank(payload.getString(ConsentExtensionConstants.CREDITOR_NAME))) {
            log.error(ErrorConstants.CREDITOR_NAME_MISSING);
            throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                    ErrorUtil.constructBerlinError(null,
                            TPPMessage.CategoryEnum.ERROR, TPPMessage.CodeEnum.FORMAT_ERROR,
                            ErrorConstants.CREDITOR_NAME_MISSING));
        }
    }

    /**
     * Method to validate dayOfExecution in payload elements.
     *
     * @param payload request payload
     */
    public static void validateDayOfExecution(JSONObject payload) throws FailedValidationException {

        if (payload.opt(ConsentExtensionConstants.DAY_OF_EXECUTION) != null) {
            log.debug("Validating payload for dayOfExecution");
            try {
                int dayOfExecution = Integer.parseInt(
                        payload.get(ConsentExtensionConstants.DAY_OF_EXECUTION).toString());
                if (dayOfExecution > 31 || dayOfExecution < 1) {
                    log.error("Invalid dayOfExecution value received : " + dayOfExecution);
                    throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                            ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                                    TPPMessage.CodeEnum.FORMAT_ERROR, ErrorConstants.INVALID_DAY_OF_EXECUTION));
                }
            } catch (NumberFormatException e) {
                log.error("Error occurred while validating payload for dayOfExecution", e);
                throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                        ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                                TPPMessage.CodeEnum.FORMAT_ERROR, ErrorConstants.INVALID_DAY_OF_EXECUTION));
            }
        }
    }

    /**
     * Method to validate debtor account element of the payload.
     *
     * @param payload the request payload
     */
    public static void validateDebtorAccount(JSONObject payload) throws FailedValidationException {

        JSONObject debtorAccountObject = payload.getJSONObject(ConsentExtensionConstants.DEBTOR_ACCOUNT);

        log.debug("Validating payload for debtor account");
        CommonConsentValidationUtil.validateAccountRefObject(debtorAccountObject);
    }

    /**
     * Method to validate a provided date is a future date. Throws an exception if the date is a past date.
     *
     * @param date
     */
    public static void validateFutureDate(LocalDate date, String errorMessage) throws FailedValidationException {

        if (!date.isAfter(LocalDate.now(ZoneOffset.UTC))) {
            log.error(errorMessage);
            throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                    ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                            TPPMessage.CodeEnum.FORMAT_ERROR, errorMessage));
        }
    }

    /**
     * Validates payment consent initiation payload based on payment type
     *
     * @param requestPayload
     * @param resourcePath
     */
    public static void validatePaymentInitiationPayload(JSONObject requestPayload, String resourcePath)
            throws FailedValidationException {
        switch (CommonConsentValidationUtil.getServiceDifferentiatingRequestPath(resourcePath)) {
            case ConsentExtensionConstants.PAYMENTS_SERVICE_PATH:
                validateSinglePaymentInitiation(requestPayload);
                break;

            case ConsentExtensionConstants.BULK_PAYMENTS_SERVICE_PATH:
                validateBulkPaymentInitiation(requestPayload);
                break;

            case ConsentExtensionConstants.PERIODIC_PAYMENTS_SERVICE_PATH:
                validatePeriodicPaymentInitiation(requestPayload);
                break;

            default:
                // Execution shouldn't reach here given that path is validated prior to reaching this point
                throw new FailedValidationException(FailedValidationException.ErrorCode.INTERNAL_SERVER_ERROR,
                        ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                                TPPMessage.CodeEnum.RESOURCE_UNKNOWN_404, ErrorConstants.PATH_INVALID));
        }
    }

    /**
     * Validates payload of periodic payment consents
     *
     * @param requestPayload
     * @throws FailedValidationException
     */
    private static void validatePeriodicPaymentInitiation(JSONObject requestPayload) throws FailedValidationException {
        LocalDate startDate;
        PaymentConsentUtil.validateDebtorAccount(requestPayload);
        PaymentConsentUtil.validateCommonPaymentElements(requestPayload);
        PaymentConsentUtil.validateDayOfExecution(requestPayload);

        log.debug("Validating periodic payments payload for start date");
        if (requestPayload.opt(ConsentExtensionConstants.START_DATE) == null
                || StringUtils.isBlank(requestPayload.getString(ConsentExtensionConstants.START_DATE))) {
            log.error(ErrorConstants.START_DATE_MISSING);
            throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                    ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                            TPPMessage.CodeEnum.FORMAT_ERROR, ErrorConstants.START_DATE_MISSING));
        } else {
            log.debug("Validating start date for correct date format");
            startDate = CommonConsentValidationUtil
                    .parseDateToISO(requestPayload.getString(ConsentExtensionConstants.START_DATE),
                            TPPMessage.CodeEnum.FORMAT_ERROR, ErrorConstants.START_DATE_INVALID);

            log.debug("Validating whether the start date is a future date");
            validateFutureDate(startDate, ErrorConstants.START_DATE_NOT_FUTURE);
        }

        log.debug("Validating periodic payments payload for frequency");
        if (requestPayload.opt(ConsentExtensionConstants.FREQUENCY) == null
                || StringUtils.isBlank(requestPayload.getString(ConsentExtensionConstants.FREQUENCY))) {
            log.error(ErrorConstants.FREQUENCY_MISSING);
            throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                    ErrorUtil.constructBerlinError(null,
                            TPPMessage.CategoryEnum.ERROR, TPPMessage.CodeEnum.FORMAT_ERROR,
                            ErrorConstants.FREQUENCY_MISSING));
        }

        if (!ConsentExtensionConstants.SUPPORTED_PERIODIC_PAYMENT_FREQUENCY_CODES.contains(
                requestPayload.getString(ConsentExtensionConstants.FREQUENCY))) {
            log.error(ErrorConstants.FREQUENCY_UNSUPPORTED);
            throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                    ErrorUtil.constructBerlinError(null,
                            TPPMessage.CategoryEnum.ERROR, TPPMessage.CodeEnum.FORMAT_ERROR,
                            ErrorConstants.FREQUENCY_UNSUPPORTED));
        }

        if (requestPayload.opt(ConsentExtensionConstants.END_DATE) != null &&
                StringUtils.isNotBlank(requestPayload.getString(
                        ConsentExtensionConstants.END_DATE))) {
            log.debug("Validating whether periodic payments end date if a future date");
            LocalDate endDate =
                    CommonConsentValidationUtil.parseDateToISO(
                            requestPayload.getString(ConsentExtensionConstants.END_DATE),
                            TPPMessage.CodeEnum.FORMAT_ERROR, ErrorConstants.END_DATE_NOT_VALID);
            validateFutureDate(endDate, ErrorConstants.END_DATE_NOT_FUTURE);
            areDatesValid(startDate, endDate);
        }

        if (requestPayload.opt(ConsentExtensionConstants.EXECUTION_RULE) != null &&
                StringUtils.isNotBlank(requestPayload.getString(
                        ConsentExtensionConstants.EXECUTION_RULE))) {
            log.debug("Validating execution rule");
            String executionRule = requestPayload.getString(ConsentExtensionConstants.EXECUTION_RULE);
            if (!(StringUtils.equals(ConsentExtensionConstants.FOLLOWING_EXECUTION_RULE, executionRule)
                    || StringUtils.equals(ConsentExtensionConstants.PRECEDING_EXECUTION_RULE, executionRule))) {
                log.error(ErrorConstants.INVALID_EXECUTION_RULE);
                throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                        ErrorUtil.constructBerlinError(null,
                                TPPMessage.CategoryEnum.ERROR, TPPMessage.CodeEnum.FORMAT_ERROR,
                                ErrorConstants.INVALID_EXECUTION_RULE));
            }
        }
    }

    private static void validateSinglePaymentInitiation(JSONObject requestPayload) throws FailedValidationException {
        validateDebtorAccount(requestPayload);
        validateRequestedExecutionDate(requestPayload, ConfigurableProperties.MAX_FUTURE_PAYMENT_DAYS);
        validateCommonPaymentElements(requestPayload);
    }

    /**
     * Validates debtor account, requested execution date, requested executed time presence in bulk payment elements.
     *
     * @param payments payment elements array
     */
    public static void validatePaymentElements(JSONArray payments) throws FailedValidationException {

        log.debug("Iterating and validating payment objects");
        JSONObject paymentJSON;
        for (int i = 0; i < payments.length(); i++) {
            paymentJSON = payments.getJSONObject(i);
            PaymentConsentUtil.validateCommonPaymentElements(paymentJSON);

            if (paymentJSON.has(ConsentExtensionConstants.DEBTOR_ACCOUNT)) {
                log.error("Debtor account cannot be present in bulk payment objects");
                throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                        ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                                TPPMessage.CodeEnum.FORMAT_ERROR,
                                String.format(ErrorConstants.INVALID_DATA_IN_PAYMENTS,
                                        ConsentExtensionConstants.DEBTOR_ACCOUNT)));
            }
            if (paymentJSON.has(ConsentExtensionConstants.REQUESTED_EXECUTION_DATE)) {
                log.error("Requested execution date cannot be present in bulk payment objects");
                throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                        ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                                TPPMessage.CodeEnum.FORMAT_ERROR,
                                String.format(ErrorConstants.INVALID_DATA_IN_PAYMENTS,
                                        ConsentExtensionConstants.REQUESTED_EXECUTION_DATE)));
            }
            if (paymentJSON.has(ConsentExtensionConstants.REQUESTED_EXECUTION_TIME)) {
                log.error("Requested execution time cannot be present in bulk payment objects");
                throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                        ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                                TPPMessage.CodeEnum.FORMAT_ERROR,
                                String.format(ErrorConstants.INVALID_DATA_IN_PAYMENTS,
                                        ConsentExtensionConstants.REQUESTED_EXECUTION_TIME)));
            }
        }
    }

    /**
     * Method to validate requested execution date.
     *
     * @param payload                 request payload
     * @param maxPaymentExecutionDays maximum payment execution days allowed
     */
    public static void validateRequestedExecutionDate(JSONObject payload, String maxPaymentExecutionDays)
            throws FailedValidationException {

        log.debug("Validating requested execution date");
        if (payload.opt(ConsentExtensionConstants.REQUESTED_EXECUTION_DATE) != null
                && StringUtils.isNotBlank(payload.getString(ConsentExtensionConstants.REQUESTED_EXECUTION_DATE))) {

            LocalDate requestedExecutionDate =
                    CommonConsentValidationUtil.parseDateToISO(payload.getString(ConsentExtensionConstants
                                    .REQUESTED_EXECUTION_DATE), TPPMessage.CodeEnum.EXECUTION_DATE_INVALID,
                            ErrorConstants.REQUESTED_EXECUTION_DATE_INVALID);

            LocalDate today = LocalDate.now(ZoneOffset.UTC);

            if (!requestedExecutionDate.isAfter(today)) { //Checks whether the requested execution date is a future date
                log.error(ErrorConstants.EXECUTION_DATE_NOT_FUTURE);
                throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                        ErrorUtil.constructBerlinError(null,
                                TPPMessage.CategoryEnum.ERROR, TPPMessage.CodeEnum.EXECUTION_DATE_INVALID,
                                ErrorConstants.EXECUTION_DATE_NOT_FUTURE));
            } else if (StringUtils.isNotBlank(maxPaymentExecutionDays)) {
                /* this block checks whether the requested execution date is within the maximum number of payment
                 execution days allowed by the ASPSP */
                long maxNumberPaymentExecutionDays = Long.parseLong(maxPaymentExecutionDays);
                if (ChronoUnit.DAYS.between(today.plusDays(maxNumberPaymentExecutionDays),
                        requestedExecutionDate) > 0) {
                    log.error(ErrorConstants.PAYMENT_EXECUTION_DATE_EXCEEDED);
                    throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                            ErrorUtil.constructBerlinError(null,
                                    TPPMessage.CategoryEnum.ERROR, TPPMessage.CodeEnum.EXECUTION_DATE_INVALID,
                                    ErrorConstants.PAYMENT_EXECUTION_DATE_EXCEEDED));
                }
            }
        }
    }

    /**
     * Method to get the payment initiation response without links.
     *
     * @param createdConsent
     * @param scaMethods
     * @return
     */
    public static void appendPaymentInitiationResponseToPayload(StoredDetailedConsentResourceData createdConsent,
                                                                ArrayList<ScaMethod> scaMethods, JSONObject payload) {

        payload.put(ConsentExtensionConstants.TRANSACTION_STATUS, createdConsent.getStatus());
        payload.put(CommonConstants.PAYMENT_ID, createdConsent.getId());

        JSONArray chosenSCAMethods = new JSONArray();
        for (ScaMethod scaMethod : scaMethods) {
            chosenSCAMethods.put(CommonConsentValidationUtil.convertObjectToJson(scaMethod));
        }

        if (scaMethods.size() > 1) {
            payload.put(ConsentExtensionConstants.SCA_METHODS, chosenSCAMethods);
        } else {
            payload.put(ConsentExtensionConstants.CHOSEN_SCA_METHOD, chosenSCAMethods.get(0));
        }
    }

    /**
     * Method to return a JSON Object with payment product
     *
     * @param consentResourcePath
     * @return
     */
    public static Object getPaymentProductAttribute(String consentResourcePath) {
        JSONObject attributesJSON = new JSONObject();
        attributesJSON.put(ConsentExtensionConstants.PAYMENT_PRODUCT_CC, getPaymentProduct(consentResourcePath));
        return attributesJSON;
    }

    /**
     * Helper method to extract payment product from resource path
     *
     * @param consentResourcePath
     * @return
     */
    public static String getPaymentProduct(String consentResourcePath) {
        return consentResourcePath.split("/")[1];
    }

    /**
     * Method to validate payment product for payment consents
     *
     * @param attributes
     * @param consentResourcePath
     */
    public static void validatePaymentProductFromAttributes(Object attributes, String consentResourcePath)
            throws FailedValidationException {
        String paymentProductFromPath = getPaymentProduct(consentResourcePath);

        // Extract payment product from attributes
        String paymentProductFromAttributes;
        try {
            JSONObject attributesJSON = CommonConsentValidationUtil.convertObjectToJson(attributes);
            paymentProductFromAttributes = attributesJSON.getString(ConsentExtensionConstants.PAYMENT_PRODUCT_CC);
        } catch (JSONException e) {
            // Should be unreachable as payment product gets added as an attribute at initiation
            throw new ServerException(ServerException.ErrorCode.BAD_REQUEST, ErrorUtil.constructBerlinError(
                    null, TPPMessage.CategoryEnum.ERROR, TPPMessage.CodeEnum.INTERNAL_SERVER_ERROR,
                    "Payment product not stored at consent initiation. Product validation failed."));
        }

        if (!paymentProductFromAttributes.equals(paymentProductFromPath)) {
            throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST, ErrorUtil.constructBerlinError(
                    null, TPPMessage.CategoryEnum.ERROR, TPPMessage.CodeEnum.PRODUCT_INVALID,
                    "The provided consent ID valid but belongs to a different payment product"));
        }
    }
}
