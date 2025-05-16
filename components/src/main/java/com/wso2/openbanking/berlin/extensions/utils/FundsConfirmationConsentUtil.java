package com.wso2.openbanking.berlin.extensions.utils;

import com.wso2.openbanking.berlin.extensions.datamodels.ScaMethod;
import com.wso2.openbanking.berlin.extensions.datamodels.TPPMessage;
import com.wso2.openbanking.berlin.extensions.exceptions.FailedValidationException;
import com.wso2.openbanking.berlin.extensions.model.StoredDetailedConsentResourceData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;

/**
 * Utility class for payment consent management
 */
public class FundsConfirmationConsentUtil {
    private static final Log log = LogFactory.getLog(FundsConfirmationConsentUtil.class);

    /**
     * Validate the requested card expiry date.
     *
     * @param cardExpiryDate requested card expiry date
     */
    public static void validateCardExpiryDate(String cardExpiryDate) throws FailedValidationException {

        LocalDate parsedCardExpiryDate = CommonConsentValidationUtil.parseDateToISO(cardExpiryDate,
                TPPMessage.CodeEnum.FORMAT_ERROR, ErrorConstants.CARD_EXPIRY_DATE_INVALID);

        if (parsedCardExpiryDate.isBefore(LocalDate.now(ZoneOffset.UTC))) {
            String errorMessage = String.format("The provided card expiry date %s is a past date",
                    parsedCardExpiryDate);
            log.error(errorMessage);
            throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                    ErrorUtil.constructBerlinError(
                            null, TPPMessage.CategoryEnum.ERROR, TPPMessage.CodeEnum.TIMESTAMP_INVALID,
                            errorMessage));
        }
    }

    /**
     * Method to validate funds confirmation initiation payload.
     *
     * @param payload
     */
    public static void validateFundsConfirmationInitiationPayload(JSONObject payload) throws FailedValidationException {

        log.debug("Validating mandatory request body elements");
        if (!payload.has(ConsentExtensionConstants.ACCOUNT)
                || payload.opt(ConsentExtensionConstants.ACCOUNT) == null) {
            log.error(ErrorConstants.MANDATORY_ELEMENTS_MISSING);
            throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST,
                    ErrorUtil.constructBerlinError(null,
                            TPPMessage.CategoryEnum.ERROR, TPPMessage.CodeEnum.FORMAT_ERROR,
                            ErrorConstants.MANDATORY_ELEMENTS_MISSING));
        }

        if (payload.has(ConsentExtensionConstants.CARD_EXPIRY_DATE)) {
            log.debug("Validating card expiry date");
            validateCardExpiryDate(payload.getString(ConsentExtensionConstants.CARD_EXPIRY_DATE));
        }

        JSONObject accountObject = payload.optJSONObject(ConsentExtensionConstants.ACCOUNT);

        log.debug("Validating account reference object");
        CommonConsentValidationUtil.validateAccountRefObject(accountObject);
    }

    /**
     * Method to get the funds confirmation initiation response without links.
     *
     * @param createdConsent
     * @param scaMethods
     * @param payload
     */
    public static void appendPaymentInitiationResponseToPayload(StoredDetailedConsentResourceData createdConsent,
                                                                ArrayList<ScaMethod> scaMethods, JSONObject payload) {
        payload.put(ConsentExtensionConstants.CONSENT_STATUS, createdConsent.getStatus());
        payload.put(ConsentExtensionConstants.CONSENT_ID, createdConsent.getId());

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
}
