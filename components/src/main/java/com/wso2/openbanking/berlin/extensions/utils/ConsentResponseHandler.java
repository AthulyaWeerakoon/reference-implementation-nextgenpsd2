package com.wso2.openbanking.berlin.extensions.utils;

import com.wso2.openbanking.berlin.extensions.exceptions.ServerException;
import com.wso2.openbanking.berlin.extensions.model.EnrichConsentCreationRequestBody;
import com.wso2.openbanking.berlin.extensions.model.SuccessResponseForResponseAlternation;

public interface ConsentResponseHandler {
    void enrichCreationResponse(EnrichConsentCreationRequestBody requestBody,
                                SuccessResponseForResponseAlternation validationResponse) throws ServerException;
}
