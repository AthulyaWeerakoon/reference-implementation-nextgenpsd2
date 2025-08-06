package org.wso2.openbanking.nextgenpsd2.extensions.constants;

import org.apache.commons.lang3.StringUtils;


/**
 * Class encapsulating all nextGenPSD2 specification specified enums.
 */
public class ExtensionEnums {

    /**
     * Auth Type enum.
     */
    public enum AuthTypeEnum {

        AUTHORISATION("authorisation"),
        CANCELLATION("cancellation");

        private String value;

        AuthTypeEnum(String value) {
            this.value = value;
        }

        public String toString() {
            return String.valueOf(value);
        }

        public static AuthTypeEnum fromValue(String text) {

            if (StringUtils.isBlank(text)) {
                throw new IllegalArgumentException("Value cannot be null or empty");
            }

            for (AuthTypeEnum b : AuthTypeEnum.values()) {
                if (text.equals(String.valueOf(b.value))) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unknown value: " + text);
        }

    }

    /**
     * Access method enum.
     */
    public enum AccessMethodEnum {

        ACCOUNTS("accounts"),
        BALANCES("balances"),
        TRANSACTIONS("transactions");

        private String value;

        AccessMethodEnum(String value) {
            this.value = value;
        }

        public String toString() {
            return String.valueOf(value);
        }

        public static AccessMethodEnum fromValue(String text) {

            if (StringUtils.isBlank(text)) {
                throw new IllegalArgumentException("Value cannot be null or empty");
            }

            for (AccessMethodEnum b : AccessMethodEnum.values()) {
                if (text.equals(String.valueOf(b.value))) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unknown value: " + text);
        }

    }

    /**
     * Consent Status enum.
     */
    public enum ConsentStatusEnum {

        RECEIVED("received"),
        REJECTED("rejected"),
        PARTIALLY_AUTHORISED("partiallyAuthorised"),
        VALID("valid"),
        REVOKED_BY_PSU("revokedByPsu"),
        EXPIRED("expired"),
        TERMINATED_BY_TPP("terminatedByTpp");

        private String value;

        ConsentStatusEnum(String value) {
            this.value = value;
        }

        public String toString() {
            return String.valueOf(value);
        }

        public static ConsentStatusEnum fromValue(String text) {

            if (StringUtils.isBlank(text)) {
                throw new IllegalArgumentException("Value cannot be null or empty");
            }

            for (ConsentStatusEnum b : ConsentStatusEnum.values()) {
                if (text.equals(String.valueOf(b.value))) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unknown value: " + text);
        }

    }

    /**
     * Consent Type enum.
     */
    public enum ConsentTypeEnum {

        ACCOUNTS("accounts"),
        PAYMENTS("payments"),
        BULK_PAYMENTS("bulk-payments"),
        PERIODIC_PAYMENTS("periodic-payments"),
        FUNDS_CONFIRMATION("funds-confirmations");

        private String value;

        ConsentTypeEnum(String value) {
            this.value = value;
        }

        public String toString() {
            return String.valueOf(value);
        }

        public static ConsentTypeEnum fromValue(String text) {

            if (StringUtils.isBlank(text)) {
                throw new IllegalArgumentException("Value cannot be null or empty");
            }

            for (ConsentTypeEnum b : ConsentTypeEnum.values()) {
                if (text.equals(String.valueOf(b.value))) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unknown value: " + text);
        }

    }

    /**
     * Permission enum.
     */
    public enum PermissionEnum {

        AVAILABLE_ACCOUNTS("availableAccounts"),
        AVAILABLE_ACCOUNTS_WITH_BALANCES("availableAccountsWithBalance"),
        ALL_PSD2("allPsd2"),
        BANK_OFFERED("bankOffered"),
        DEDICATED_ACCOUNTS("dedicatedAccounts");

        private String value;

        PermissionEnum(String value) {
            this.value = value;
        }

        public String toString() {
            return String.valueOf(value);
        }

        public static PermissionEnum fromValue(String text) {

            if (StringUtils.isBlank(text)) {
                throw new IllegalArgumentException("Value cannot be null or empty");
            }

            for (PermissionEnum b : PermissionEnum.values()) {
                if (text.equals(String.valueOf(b.value))) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unknown value: " + text);
        }

    }

    /**
     * SCA Approaches enum.
     */
    public enum ScaApproachEnum {

        REDIRECT("REDIRECT"),
        DECOUPLED("DECOUPLED"),
        EMBEDDED("EMBEDDED");

        private String value;

        ScaApproachEnum(String value) {
            this.value = value;
        }

        public String toString() {
            return String.valueOf(value);
        }

        public static ScaApproachEnum fromValue(String text) {

            if (StringUtils.isBlank(text)) {
                throw new IllegalArgumentException("Value cannot be null or empty");
            }

            for (ScaApproachEnum b : ScaApproachEnum.values()) {
                if (text.equals(String.valueOf(b.value))) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unknown value: " + text);
        }

    }

    /**
     * Sca Status enum.
     */
    public enum ScaStatusEnum {

        RECEIVED("received"),
        PSU_IDENTIFIED("psuIdentified"),
        PSU_AUTHENTICATED("psuAuthenticated"),
        SCA_METHOD_SELECTED("scaMethodSelected"),
        STARTED("started"),
        UNCONFIRMED("unconfirmed"),
        FINALISED("finalised"),
        FAILED("failed"),
        EXEMPTED("exempted");

        private String value;

        ScaStatusEnum(String value) {
            this.value = value;
        }

        public String toString() {
            return String.valueOf(value);
        }

        public static ScaStatusEnum fromValue(String text) {

            if (StringUtils.isBlank(text)) {
                throw new IllegalArgumentException("Value cannot be null or empty");
            }

            for (ScaStatusEnum b : ScaStatusEnum.values()) {
                if (text.equals(String.valueOf(b.value))) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unknown value: " + text);
        }

    }

    /**
     * Specifies the status of a particular transaction.
     */
    public enum TransactionStatusEnum {

        // Preceding check of technical validation was successful. Customer profile check was also successful.
        ACCP("AcceptedCustomerProfile"),
        // Settlement on the debtor’s account has been completed.
        ACSC("AcceptedSettlementCompleted"),
        // All preceding checks such as technical validation and customer profile were successful and therefore the
        // payment initiation has been accepted for execution.
        ACSP("AcceptedSettlementInProcess"),
        // Authentication and syntactical and semantic validation are successful
        ACTC("AcceptedTechnicalValidation"),
        // Instruction is accepted but a change will be made, such as date or remittance not sent.
        ACWC("AcceptedWithChange"),
        // Payment instruction included in the credit transfer is accepted without being posted to the creditor
        // customer’s account.
        ACWP("AcceptedWithoutPosting"),
        // Payment initiation has been received by the receiving agent.
        RCVD("Received"),
        // Payment initiation or individual transaction included in the payment initiation is pending. Further
        // checks and status update will be performed.
        PDNG("Pending"),
        // Payment initiation or individual transaction included in the payment initiation has been rejected.
        RJCT("Rejected"),
        // PSU has revoked a previously given consent for a particular payment.
        REVOKED("Revoked"),
        // Cancellation of a payment
        CANC("Cancelled"),
        // Partially Accepted Technical Correct in cases of multi level SCA.
        PATC("PartiallyAcceptedTechnicalCorrect");

        private String value;

        TransactionStatusEnum(String value) {

            this.value = value;
        }

        public static TransactionStatusEnum fromValue(String text) {

            if (StringUtils.isBlank(text)) {
                throw new IllegalArgumentException("Value cannot be null or empty");
            }

            for (TransactionStatusEnum statusEnum : TransactionStatusEnum.values()) {
                if (String.valueOf(statusEnum.value).equals(text)) {
                    return statusEnum;
                }
            }
            throw new IllegalArgumentException("Unknown value: " + text);
        }

        public String toString() {

            return String.valueOf(value);
        }
    }
}
