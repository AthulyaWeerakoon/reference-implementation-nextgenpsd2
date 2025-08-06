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

package org.wso2.openbanking.nextgenpsd2.extensions.utils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.wso2.openbanking.nextgenpsd2.extensions.exceptions.ExtensionException;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.ErrorResponse;
import org.wso2.openbanking.nextgenpsd2.extensions.generated.model.FailedResponse;
import org.wso2.openbanking.nextgenpsd2.extensions.model.TPPMessage;
import org.wso2.openbanking.nextgenpsd2.extensions.model.TPPMessages;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for building and processing error objects.
 */
public class ErrorUtil {
    /**
     * Method to get ErrorResponse object for error.
     * @param errorMessage error message
     * @param errorDescription error description
     * @return built error response object
     */
    public static ErrorResponse getErrorResponse(String errorMessage, String errorDescription) {
        return new ErrorResponse(ErrorResponse.StatusEnum.ERROR, getErrorDataObject(errorMessage, errorDescription));
    }

    /**
     * Method to construct the error data object.
     *
     * @param errorMessage Error Message
     * @param errorDescription Error Description
     * @return accelerator compatible data object for the error response
     */
    public static JSONObject getErrorDataObject(String errorMessage, String errorDescription) {

        JSONObject data = new JSONObject();
        data.put("errorMessage", errorMessage);
        data.put("errorDescription", errorDescription);

        return data;
    }

    /**
     * Used to construct a Berlin specific error.
     *
     * @param path path which the error might have occurred
     * @param category error category, ERROR or WARN
     * @param code error code
     * @param text error text
     * @return an error constructed as a json object
     */
    public static JSONObject constructBerlinError(String path, TPPMessage.CategoryEnum category,
                                                  TPPMessage.CodeEnum code, String text) throws ExtensionException {

        List<TPPMessage> tppMessagesList = new ArrayList<>();
        TPPMessages tppMessages = new TPPMessages();

        TPPMessage tppMessage = new TPPMessage();

        if (StringUtils.isNotBlank(path)) {
            tppMessage.setPath(path);
        }
        tppMessage.setCategory(category);
        tppMessage.setCode(code);
        tppMessage.setText(text);

        tppMessagesList.add(tppMessage);
        tppMessages.setTppMessages(tppMessagesList);

        return CommonConsentValidationUtil.convertObjectToJson(tppMessages);
    }

    /**
     * Used to construct error messages on demand. Essentially when need to maintain a list of errors to be
     * constructed later.
     *
     * @param path path which the error might have occurred
     * @param category error category, ERROR or WARN
     * @param code error code
     * @param text error text
     * @return a TPPMessage object to be stored in an error list
     */
    public static TPPMessage constructTPPMessage(String path, TPPMessage.CategoryEnum category,
                                                 TPPMessage.CodeEnum code, String text) {

        TPPMessage tppMessage = new TPPMessage();
        tppMessage.setPath(path);
        tppMessage.setCategory(category);
        tppMessage.setCode(code);
        tppMessage.setText(text);

        return tppMessage;
    }

    /**
     * Used to construct an error using a set of errors.
     *
     * @param tppErrorMessages a list of TPPMessage error objects
     * @return a set of errors constructed as a json object
     */
    public static JSONObject constructBerlinError(List<TPPMessage> tppErrorMessages) throws ExtensionException {

        TPPMessages tppMessages = new TPPMessages();
        tppMessages.setTppMessages(tppErrorMessages);

        return CommonConsentValidationUtil.convertObjectToJson(tppMessages);
    }

    /**
     * Get formatted error object for validation failures.
     * @return a correctly formatted FailedResponse JSONObject
     */
    public static JSONObject getFormattedFailedResponse(int errorCode, JSONObject data) {
        FailedResponse failedResponse = new FailedResponse();

        failedResponse.setStatus(FailedResponse.StatusEnum.ERROR);
        failedResponse.setErrorCode(errorCode);
        failedResponse.setData(data);

        return new JSONObject(failedResponse);
    }

    /**
     * Get formatted error object for internal errors.
     * @return JSONObject representing the error
     */
    public static JSONObject getFormattedErrorResponse(JSONObject data) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(ErrorResponse.StatusEnum.ERROR);
        errorResponse.setData(data);
        return new JSONObject(errorResponse);
    }

    /**
     * Method to extract error message from nextGenPSD2 error message if exists.
     *
     * @param data extracts error message from error message object
     * @return error message from return object
     */
    public static String getErrorMessage(JSONObject data) {
        String message = data.toString();

        // Extract error message from data if it exists
        JSONArray tppMessages = data.optJSONArray("tppMessages");
        if (tppMessages != null) {
            JSONObject errorObj = tppMessages.optJSONObject(0);
            if (errorObj != null) {
                String text = errorObj.optString("text");
                if (text != null && !text.isEmpty()) {
                    message = text;
                }
            }
        }

        return message;
    }
}
