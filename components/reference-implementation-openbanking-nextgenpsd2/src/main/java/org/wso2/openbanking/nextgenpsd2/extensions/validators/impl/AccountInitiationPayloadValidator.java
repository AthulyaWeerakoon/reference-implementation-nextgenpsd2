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

package org.wso2.openbanking.nextgenpsd2.extensions.validators.impl;

import org.wso2.openbanking.nextgenpsd2.extensions.configurations.ConfigurationConstants;
import org.wso2.openbanking.nextgenpsd2.extensions.constants.CommonConstants;
import org.wso2.openbanking.nextgenpsd2.extensions.constants.ErrorConstants;
import org.wso2.openbanking.nextgenpsd2.extensions.model.AccountInitiationPayload;
import org.wso2.openbanking.nextgenpsd2.extensions.model.TPPMessage;
import org.wso2.openbanking.nextgenpsd2.extensions.utils.CommonConsentValidationUtil;
import org.wso2.openbanking.nextgenpsd2.extensions.validators.annotations.ValidAccountInitiationPayload;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator implementation for validating account initiation payload.
 */
public class AccountInitiationPayloadValidator implements
        ConstraintValidator<ValidAccountInitiationPayload, AccountInitiationPayload> {

    private final int configuredMinimumFreqPerDay = Integer.parseInt(ConfigurationConstants.FREQ_PER_DAY);
    private final boolean isValidUntilDateCapEnabled =
            Boolean.parseBoolean(ConfigurationConstants.VALID_UNTIL_DATE_CAP_ENABLED);
    private final int validUntilDaysCap = Integer.parseInt(ConfigurationConstants.VALID_UNTIL_DAYS);

    @Override
    public boolean isValid(AccountInitiationPayload payload, ConstraintValidatorContext context) {

        if (payload.getAccess() == null ||
                payload.getRecurringIndicator() == null ||
                payload.getValidUntil() == null ||
                payload.getFrequencyPerDay() == null ||
                payload.getCombinedServiceIndicator() == null
        ) {
            CommonConsentValidationUtil.setConstrainViolation(context,
                    CommonConsentValidationUtil.buildViolationMessage(ErrorConstants.MANDATORY_ELEMENTS_MISSING));
        }

        if (!payload.getRecurringIndicator() && payload.getFrequencyPerDay() > 1) {
            CommonConsentValidationUtil.setConstrainViolation(context,
                    CommonConsentValidationUtil.buildViolationMessage(ErrorConstants.INVALID_FREQ_PER_DAY_COUNT));
            return false;
        }

        if (payload.getRecurringIndicator() && payload.getFrequencyPerDay() < configuredMinimumFreqPerDay) {
            String errorMessageTemplate = "Frequency per day for recurring consent is lesser than the supported " +
                    "minimum value %s";
            CommonConsentValidationUtil.setConstrainViolation(context, String.format(errorMessageTemplate,
                    configuredMinimumFreqPerDay));
            return false;
        }

        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        LocalDate validUntil = payload.getValidUntil();

        if (validUntil.isBefore(today)) {
            String errorMessage = "validUntil has to be today, %s or a future date";
            CommonConsentValidationUtil.setConstrainViolation(context,
                    CommonConsentValidationUtil.buildViolationMessage(TPPMessage.CodeEnum.TIMESTAMP_INVALID,
                            String.format(errorMessage, today)
                    ));
            return false;
        }

        // isValidUntilDateCapEnabled validation
        LocalDate maximumValidUntil = LocalDate.parse(CommonConstants.MAXIMUM_VALID_DATE);
        if (isValidUntilDateCapEnabled &&
                (validUntil.isAfter(today.plusDays(validUntilDaysCap)))) {
            /*
            If the valid until date cap is enabled;
            and if now plus the valid until days cap(now + valid until days cap) is still a valid date;
            the new valid until date will be now plus the valid until days cap(now + valid until days cap)
             */
            validUntil = LocalDate.from(LocalDateTime.now().plusDays(validUntilDaysCap));
        } else if (validUntil.isAfter(maximumValidUntil)) {
            validUntil = LocalDate.parse(CommonConstants.MAXIMUM_VALID_DATE);
        }
        payload.setValidUntil(validUntil);

        return true;
    }
}
