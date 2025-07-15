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

package org.wso2.openbanking.nextgenpsd2.extensions.handlers.impl;

import org.wso2.openbanking.nextgenpsd2.extensions.constants.ErrorConstants;
import org.wso2.openbanking.nextgenpsd2.extensions.exceptions.BadRequestException;
import org.wso2.openbanking.nextgenpsd2.extensions.exceptions.ValidationFailureException;
import org.wso2.openbanking.nextgenpsd2.extensions.handlers.ConsentManagementValidationHandler;
import org.wso2.openbanking.nextgenpsd2.extensions.model.TPPMessage;
import org.wso2.openbanking.nextgenpsd2.extensions.model.generated.EnrichConsentCreationRequestBody;
import org.wso2.openbanking.nextgenpsd2.extensions.model.generated.PreProcessConsentCreationRequestBody;
import org.wso2.openbanking.nextgenpsd2.extensions.model.generated.PreProcessConsentRequestBody;
import org.wso2.openbanking.nextgenpsd2.extensions.model.generated.SuccessResponseConsentRevocation;
import org.wso2.openbanking.nextgenpsd2.extensions.model.generated.SuccessResponseForResponseAlternation;
import org.wso2.openbanking.nextgenpsd2.extensions.model.generated.SuccessResponsePreProcessConsentCreation;
import org.wso2.openbanking.nextgenpsd2.extensions.utils.ErrorUtil;

/**
 * Consent authorisation handler for explicit authorisation.
 */
public class ConsentAuthorisationManageHandler implements ConsentManagementValidationHandler {
    // ToDo: Implement authorisation creation for consents

    /**
     * Handle creation of authorisations for consents.
     *
     * @param requestBody
     * @return
     * @throws ValidationFailureException
     */
    @Override
    public SuccessResponsePreProcessConsentCreation handleCreation(PreProcessConsentCreationRequestBody requestBody)
            throws ValidationFailureException, BadRequestException {
        // Throws an error since creating auth object for an existing consent is not supported
        throw new ValidationFailureException(ValidationFailureException.ErrorCode.BAD_REQUEST,
                ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                        TPPMessage.CodeEnum.SERVICE_INVALID_405, ErrorConstants.AUTH_CREATION_NOT_SUPPORTED));
    }

    /**
     * Handles retrieval of consent authorisations.
     *
     * @param requestBody
     * @return
     * @throws ValidationFailureException
     */
    @Override
    public SuccessResponseForResponseAlternation handleRetrieval(PreProcessConsentRequestBody requestBody)
            throws ValidationFailureException, BadRequestException {
        // Throws an error since auth resources for a consent cannot be retrieved through consent creation
        throw new ValidationFailureException(ValidationFailureException.ErrorCode.BAD_REQUEST,
                ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                        TPPMessage.CodeEnum.SERVICE_INVALID_405, ErrorConstants.DELETE_NOT_SUPPORTED));
    }

    /**
     * Handles revocation of consent authorizations.
     *
     * @param requestBody
     * @return
     */
    @Override
    public SuccessResponseConsentRevocation handleRevocation(PreProcessConsentRequestBody requestBody)
            throws ValidationFailureException, BadRequestException {
        // Throws an error since auth resources for a consent cannot be retrieved through consent creation
        throw new ValidationFailureException(ValidationFailureException.ErrorCode.BAD_REQUEST,
                ErrorUtil.constructBerlinError(null, TPPMessage.CategoryEnum.ERROR,
                        TPPMessage.CodeEnum.SERVICE_INVALID_405, ErrorConstants.AUTH_CREATION_NOT_SUPPORTED));
    }

    /**
     * Handles consent authorization creation response customization.
     *
     * @param requestBody
     * @return
     * @throws BadRequestException
     */
    @Override
    public SuccessResponseForResponseAlternation enrichCreationResponse(EnrichConsentCreationRequestBody requestBody)
            throws BadRequestException {
        // Throws an error since this should be unreachable
        throw new BadRequestException(ErrorConstants.AUTH_CREATION_NOT_SUPPORTED);
    }
}
