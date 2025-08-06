package org.wso2.openbanking.nextgenpsd2.extensions.validators.impl;

import org.wso2.openbanking.nextgenpsd2.extensions.constants.CommonConstants;
import org.wso2.openbanking.nextgenpsd2.extensions.constants.ErrorConstants;
import org.wso2.openbanking.nextgenpsd2.extensions.model.AccountAccess;
import org.wso2.openbanking.nextgenpsd2.extensions.utils.CommonConsentValidationUtil;
import org.wso2.openbanking.nextgenpsd2.extensions.validators.annotations.ValidAccountAccess;

import java.util.Arrays;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator implementation for validating account access object.
 */
public class AccountAccessValidator implements ConstraintValidator<ValidAccountAccess, AccountAccess> {

    @Override
    public boolean isValid(AccountAccess access, ConstraintValidatorContext context) {
        boolean hasArrays = notEmpty(access.getAccounts()) ||
                notEmpty(access.getBalances()) ||
                notEmpty(access.getTransactions());

        boolean hasPermissions = access.getAvailableAccounts() != null ||
                access.getAvailableAccountsWithBalances() != null ||
                access.getAllPsd2() != null;

        // At least one must be present
        if (!hasArrays && !hasPermissions) {
            CommonConsentValidationUtil.setConstrainViolation(context,
                    CommonConsentValidationUtil
                            .buildViolationMessage(ErrorConstants.ACCESS_OBJECT_MANDATORY_ELEMENTS_MISSING));
            return false;
        }

        // Permissions and arrays must not coexist
        if (hasArrays && hasPermissions) {
            CommonConsentValidationUtil.setConstrainViolation(context,
                    CommonConsentValidationUtil.buildViolationMessage(ErrorConstants.INVALID_PERMISSION));
            return false;
        }

        // Validate permission configurations
        if (hasPermissions) {
            if (CommonConstants.ALL_ACCOUNTS.equals(access.getAvailableAccounts())
                    || CommonConstants.ALL_ACCOUNTS_WITH_OWNER_NAME.equals(access.getAvailableAccounts())) {
                if (access.getAvailableAccountsWithBalances() != null || access.getAllPsd2() != null) {
                    CommonConsentValidationUtil.setConstrainViolation(context,
                            CommonConsentValidationUtil.buildViolationMessage(ErrorConstants.INVALID_PERMISSION));
                }
            }
            if (CommonConstants.ALL_ACCOUNTS.equals(access.getAvailableAccountsWithBalances())
                    || CommonConstants.ALL_ACCOUNTS_WITH_OWNER_NAME
                    .equals(access.getAvailableAccountsWithBalances())) {
                if (access.getAvailableAccounts() != null || access.getAllPsd2() != null) {
                    CommonConsentValidationUtil.setConstrainViolation(context,
                            CommonConsentValidationUtil.buildViolationMessage(ErrorConstants.INVALID_PERMISSION));
                }
            }
            if (CommonConstants.ALL_ACCOUNTS.equals(access.getAllPsd2())
                    || CommonConstants.ALL_ACCOUNTS_WITH_OWNER_NAME.equals(access.getAllPsd2())) {
                if (access.getAvailableAccounts() != null || access.getAvailableAccountsWithBalances() != null) {
                    CommonConsentValidationUtil.setConstrainViolation(context,
                            CommonConsentValidationUtil.buildViolationMessage(ErrorConstants.INVALID_PERMISSION));
                }
            }
        }

        // Additional info only with at least one array
        if (access.getAdditionalInformation() != null && !hasArrays) {
            CommonConsentValidationUtil.setConstrainViolation(context,
                    CommonConsentValidationUtil
                            .buildViolationMessage(ErrorConstants.INVALID_USE_OF_ADDITIONAL_INFO_ATTRIBUTE));
            return false;
        }

        // Arrays must all be empty or all non-empty
        List<List<?>> arrays = Arrays.asList(access.getAccounts(), access.getBalances(), access.getTransactions());
        long emptyCount = arrays.stream().filter(arr -> arr != null && arr.isEmpty()).count();
        long nonEmptyCount = arrays.stream().filter(arr -> arr != null && !arr.isEmpty()).count();

        if (emptyCount > 0 && nonEmptyCount > 0) {
            CommonConsentValidationUtil.setConstrainViolation(context,
                    CommonConsentValidationUtil.buildViolationMessage(ErrorConstants.INVALID_PERMISSION));
            return false;
        }

        return true;
    }

    /**
     * Helper method for null and isEmpty check.
     *
     * @param list
     * @return
     */
    private boolean notEmpty(List<?> list) {
        return list != null && !list.isEmpty();
    }
}
