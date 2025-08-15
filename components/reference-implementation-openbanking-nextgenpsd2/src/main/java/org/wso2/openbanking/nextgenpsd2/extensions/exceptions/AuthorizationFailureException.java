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

package org.wso2.openbanking.nextgenpsd2.extensions.exceptions;

import org.json.JSONObject;
import org.wso2.openbanking.nextgenpsd2.extensions.utils.ErrorUtil;

/**
 * Exception class for success responses carrying FailedResponseInConsentAuthorize objects.
 */
public class AuthorizationFailureException extends Exception {
    String message;
    String newStatus;
    String responseId;

    public AuthorizationFailureException(String message) {
        super(message);
        this.message = message;
    }

    public AuthorizationFailureException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    public AuthorizationFailureException(String message, String newStatus) {
        super(message);
        this.message = message;
        this.newStatus = newStatus;
    }

    public AuthorizationFailureException(String message, String newStatus, Throwable e) {
        super(message, e);
        this.message = message;
        this.newStatus = newStatus;
    }

    /**
     * Sets response id for the failed response in consent authorize.
     *
     * @param responseId
     */
    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    /**
     * Returns exception formatted as a FailedResponseInConsentAuthorize.
     * @return
     */
    public JSONObject getFormattedError() {
        return ErrorUtil.getFormattedAuthorizationFailureException(this.responseId, this.message, this.newStatus);
    }

    /**
     * Returns formatted error as String.
     * @return
     */
    public String getFormattedErrorAsString() {
        return getFormattedError().toString();
    }
}
