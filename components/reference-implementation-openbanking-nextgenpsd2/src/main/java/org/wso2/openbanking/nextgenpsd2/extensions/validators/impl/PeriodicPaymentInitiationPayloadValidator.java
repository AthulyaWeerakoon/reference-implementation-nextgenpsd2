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

import org.wso2.openbanking.nextgenpsd2.extensions.constants.CommonConstants;
import org.wso2.openbanking.nextgenpsd2.extensions.constants.ErrorConstants;
import org.wso2.openbanking.nextgenpsd2.extensions.model.PeriodicPaymentInitiationPayload;
import org.wso2.openbanking.nextgenpsd2.extensions.utils.CommonConsentValidationUtil;
import org.wso2.openbanking.nextgenpsd2.extensions.validators.annotations.ValidPeriodicPaymentInitiationPayload;

import java.time.LocalDate;
import java.time.ZoneOffset;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator implementation for validating periodic payment consent initiation payload.
 */
public class PeriodicPaymentInitiationPayloadValidator implements
        ConstraintValidator<ValidPeriodicPaymentInitiationPayload, PeriodicPaymentInitiationPayload> {

    @Override
    public boolean isValid(PeriodicPaymentInitiationPayload payload, ConstraintValidatorContext context) {
        if (payload == null) {
            return false;
        }

        context.disableDefaultConstraintViolation();

        // Validate startDate is in future
        if (payload.getStartDate() != null && !payload.getStartDate().isAfter(LocalDate.now(ZoneOffset.UTC))) {
            CommonConsentValidationUtil.setConstrainViolation(context,
                    CommonConsentValidationUtil.buildViolationMessage(ErrorConstants.START_DATE_NOT_FUTURE));
            return false;
        }

        // Validate endDate is in future
        if (payload.getEndDate() != null && !payload.getEndDate().isAfter(LocalDate.now(ZoneOffset.UTC))) {
            CommonConsentValidationUtil.setConstrainViolation(context,
                    CommonConsentValidationUtil.buildViolationMessage(ErrorConstants.END_DATE_NOT_FUTURE));
            return false;
        }

        // Validate endDate if after startDate
        if (payload.getStartDate() != null && payload.getEndDate() != null
                && !payload.getEndDate().isAfter(payload.getStartDate())) {
            CommonConsentValidationUtil.setConstrainViolation(context,
                    CommonConsentValidationUtil.buildViolationMessage(ErrorConstants.DATES_INCONSISTENT));
            return false;
        }

        // Validate frequency is supported
        if (payload.getFrequency() != null && !CommonConstants.SUPPORTED_PERIODIC_PAYMENT_FREQUENCY_CODES
                .contains(payload.getFrequency())) {
            CommonConsentValidationUtil.setConstrainViolation(context,
                    CommonConsentValidationUtil.buildViolationMessage(ErrorConstants.FREQUENCY_UNSUPPORTED));
            return false;
        }

        // Validate executionRule if present
        if (payload.getExecutionRule() != null &&
                !(CommonConstants.FOLLOWING_EXECUTION_RULE.equals(payload.getExecutionRule())
                        || CommonConstants.PRECEDING_EXECUTION_RULE.equals(payload.getExecutionRule()))) {
            CommonConsentValidationUtil.setConstrainViolation(context,
                    CommonConsentValidationUtil.buildViolationMessage(ErrorConstants.INVALID_EXECUTION_RULE));
            return false;
        }

        return true;
    }
}
