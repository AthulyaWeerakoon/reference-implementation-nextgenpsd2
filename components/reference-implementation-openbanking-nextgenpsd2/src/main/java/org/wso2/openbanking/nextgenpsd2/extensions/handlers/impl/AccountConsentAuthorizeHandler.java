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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.wso2.openbanking.nextgenpsd2.extensions.constants.CommonConstants;
import org.wso2.openbanking.nextgenpsd2.extensions.constants.ExtensionEnums;
import org.wso2.openbanking.nextgenpsd2.extensions.exceptions.AuthorizationFailureException;
import org.wso2.openbanking.nextgenpsd2.extensions.exceptions.ExtensionException;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.AuthorizedResourcesAuthorizedDataInner;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.DetailedConsentResourceDataWithAmendments;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.PersistAuthorizedConsent;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.PersistAuthorizedConsentRequestBody;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.PopulateConsentAuthorizeScreenData;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.Resource;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.StoredAuthorization;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.SuccessResponsePopulateConsentAuthorizeScreenData;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.SuccessResponsePopulateConsentAuthorizeScreenDataConsentData;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.UserGrantedData;
import org.wso2.openbanking.nextgenpsd2.extensions.handlers.ConsentAuthorizationHandler;
import org.wso2.openbanking.nextgenpsd2.extensions.model.AccountAccess;
import org.wso2.openbanking.nextgenpsd2.extensions.model.AccountInitiationPayload;
import org.wso2.openbanking.nextgenpsd2.extensions.utils.AccountConsentUtil;
import org.wso2.openbanking.nextgenpsd2.extensions.utils.CommonConsentValidationUtil;
import org.wso2.openbanking.nextgenpsd2.extensions.utils.ConsentAuthorizationUtil;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

/**
 * Consent handler interface for processing account consent authorization related requests.
 */
public class AccountConsentAuthorizeHandler implements ConsentAuthorizationHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    /**
     * Return consent data for account consents.
     *
     * @param responseData data of the response object to populate consent authorize screen for account consents
     * @param requestData data of the request made to populate-consent-authorize-screen for account consents
     */
    @Override
    public void populateBasicConsentData(SuccessResponsePopulateConsentAuthorizeScreenData responseData,
                                         PopulateConsentAuthorizeScreenData requestData) throws ExtensionException {
        // Add consent data if null
        if (responseData.getConsentData() == null) {
            responseData.setConsentData(new SuccessResponsePopulateConsentAuthorizeScreenDataConsentData());
        }

        // Populate consent data
        responseData.getConsentData().setType(requestData.getConsentResource().getType());
        AccountConsentUtil.populateAccountsBasicConsentData(responseData, requestData);
    }

    /**
     * Return consumer data for account consents.
     *
     * @param responseData data of the response object to populate consent authorize screen for account consents
     * @param requestData data of the request made to populate-consent-authorize-screen for account consents
     */
    @Override
    public void populateAccountsData(SuccessResponsePopulateConsentAuthorizeScreenData responseData,
                                     PopulateConsentAuthorizeScreenData requestData)
            throws ExtensionException, AuthorizationFailureException {

        // Map receipt to initiation payload object
        AccountInitiationPayload receipt = objectMapper.convertValue(requestData.getConsentResource().getReceipt(),
                AccountInitiationPayload.class);

        // Identify requested permission from receipt
        String permission = AccountConsentUtil.identifyPermissionFromReceipt(receipt);

        // Create response consent data body
        SuccessResponsePopulateConsentAuthorizeScreenDataConsentData consentData
                = responseData.getConsentData();
        if (consentData == null) {
            consentData = new SuccessResponsePopulateConsentAuthorizeScreenDataConsentData();
            responseData.setConsentData(consentData);
        }

        // Set accounts based on permission
        String userId = requestData.getUserId();
        switch (ExtensionEnums.PermissionEnum.fromValue(permission)) {
            case DEDICATED_ACCOUNTS:
                consentData.setPermissions(AccountConsentUtil.buildPermissionsForDedicatedAccounts(receipt, userId));
                break;
            case ALL_PSD2:
                consentData.setPermissions(AccountConsentUtil.buildPermissionsForAllPsd2Accounts(userId));
                break;
            case AVAILABLE_ACCOUNTS:
            case AVAILABLE_ACCOUNTS_WITH_BALANCES:
                consentData.setPermissions(AccountConsentUtil.buildPermissionsForAvailableAccounts(userId, permission));
                break;
            case BANK_OFFERED:
                // Build both consumer data with consumer accounts and consent data with permissions
                /*
                ToDo: When an bank offered account consent requires multi-level authorization, how account selection
                 happens is not specified in the nextGenPSD2 specification. Depending on how this issue is resolved,
                 the following account selection might need be modified.
                 */
                AccountConsentUtil.populatePermissionsAndConsumerAccounts(responseData, receipt, userId);
                break;
            default:
                throw new ExtensionException(Response.Status.BAD_REQUEST, "invalid_request",
                        "Unidentified permission found.");
        }
    }

    /**
     * Returns authorizations and account mappings for account consent persistence.
     *
     * @param requestBody body of the request received by persist-authorized-consent for account consents
     * @param authorizingResource authorization resource being authorized for account consents
     * @return amended account consent resource
     */
    @Override
    public DetailedConsentResourceDataWithAmendments getAmendedConsentResource(
            PersistAuthorizedConsentRequestBody requestBody, StoredAuthorization authorizingResource)
            throws AuthorizationFailureException, ExtensionException {

        // Build response consent resource
        PersistAuthorizedConsent requestData = requestBody.getData();
        boolean isApproved = requestData.getIsApproved();

        // Get auth status from approval
        String authStatus;
        if (isApproved) {
            authStatus = ExtensionEnums.ScaStatusEnum.FINALISED.toString();
        } else {
            authStatus = ExtensionEnums.ScaStatusEnum.FAILED.toString();
        }

        // Extract accounts and permissions, and build new access object
        UserGrantedData userGrantedData = requestData.getUserGrantedData();
        List<Resource> accountMappingResources = new ArrayList<>();
        AccountAccess newAccess = new AccountAccess();
        for (AuthorizedResourcesAuthorizedDataInner authorizedPermission: userGrantedData.getAuthorizedResources()
                .getAuthorizedData()) {
            if (authorizedPermission.getPermissions().contains(CommonConstants.BALANCES_PERMISSION)) {
                // accounts authorized for balances access
                accountMappingResources.addAll(AccountConsentUtil.createAccountPermissionMappings(authorizedPermission,
                        ExtensionEnums.AccessMethodEnum.BALANCES.toString()));
                newAccess.setBalances(CommonConsentValidationUtil
                        .extractAccountRef(authorizedPermission.getAccounts()));
            } else if (authorizedPermission.getPermissions()
                    .contains(CommonConstants.TRANSACTIONS_PERMISSION)) {
                // accounts authorized for transactions history access
                accountMappingResources.addAll(AccountConsentUtil.createAccountPermissionMappings(authorizedPermission,
                        ExtensionEnums.AccessMethodEnum.TRANSACTIONS.toString()));
                newAccess.setTransactions(CommonConsentValidationUtil
                        .extractAccountRef(authorizedPermission.getAccounts()));
            } else {
                // accounts authorized for account information access
                accountMappingResources.addAll(AccountConsentUtil.createAccountPermissionMappings(authorizedPermission,
                        ExtensionEnums.AccessMethodEnum.ACCOUNTS.toString()));
                newAccess.setAccounts(CommonConsentValidationUtil
                        .extractAccountRef(authorizedPermission.getAccounts()));
            }
        }

        // Add bank offered accounts to consent receipt
        AccountConsentUtil.updateReceiptAccess(requestData.getConsentResource(), newAccess);

        // Set new consent status
        return ConsentAuthorizationUtil.buildAmendedConsentResource(requestBody.getRequestId(), authorizingResource,
                isApproved, requestData, accountMappingResources, authStatus);
    }
}
