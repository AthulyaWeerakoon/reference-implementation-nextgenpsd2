package com.wso2.openbanking.berlin.extensions.utils;

import com.wso2.openbanking.berlin.extensions.datamodels.TPPMessage;
import com.wso2.openbanking.berlin.extensions.exceptions.FailedValidationException;
import com.wso2.openbanking.berlin.extensions.exceptions.ServerException;
import com.wso2.openbanking.berlin.extensions.model.EnrichConsentCreationRequestBody;
import com.wso2.openbanking.berlin.extensions.model.PreProcessConsentCreationRequestBody;
import com.wso2.openbanking.berlin.extensions.model.SuccessResponseForResponseAlternation;
import com.wso2.openbanking.berlin.extensions.model.SuccessResponsePreProcessConsentCreation;

/**
 * Consent authorisation handler for explicit authorisation
 */
public class ConsentAuthorisationHandler implements ConsentHandler, ConsentResponseHandler {
    // ToDo: Implement authorisation creation for consents

    /**
     * Handle creation of authorisations for consents
     *
     * @param requestBody
     * @param validationResponse
     */
    @Override
    public void handleCreation(PreProcessConsentCreationRequestBody requestBody, SuccessResponsePreProcessConsentCreation validationResponse) throws FailedValidationException {
        // Throws an error since creating auth object for an existing consent is not supported
        throw new FailedValidationException(FailedValidationException.ErrorCode.BAD_REQUEST, ErrorUtil.constructBerlinError(
                null, TPPMessage.CategoryEnum.ERROR, TPPMessage.CodeEnum.SERVICE_INVALID_405,
                ErrorConstants.AUTH_CREATION_NOT_SUPPORTED));
    }

    /**
     * Handles consent authorization creation response customization
     *
     * @param requestBody
     * @param validationResponse
     * @throws FailedValidationException
     */
    @Override
    public void enrichCreationResponse(EnrichConsentCreationRequestBody requestBody,
                                       SuccessResponseForResponseAlternation validationResponse)
            throws ServerException {
        // Throws an error since this should be unreachable
        throw new ServerException(ServerException.ErrorCode.BAD_REQUEST, ErrorUtil.constructBerlinError(
                null, TPPMessage.CategoryEnum.ERROR, TPPMessage.CodeEnum.SERVICE_INVALID_405,
                ErrorConstants.AUTH_CREATION_NOT_SUPPORTED));
    }
}
