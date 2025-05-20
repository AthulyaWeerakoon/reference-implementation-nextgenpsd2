package com.wso2.openbanking.berlin.extensions.exceptions;

import com.wso2.openbanking.berlin.extensions.model.FailedResponse;
import org.json.JSONObject;

/**
 * Exception class for success responses carrying FailedResponse objects
 */
public class FailedValidationException extends Exception {

    public enum ErrorCode {
        BAD_REQUEST(400),
        UNAUTHORIZED(401),
        NOT_FOUND(404),
        FORBIDDEN(403),
        INTERNAL_SERVER_ERROR(500);

        private final int code;

        ErrorCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    private final FailedResponse.StatusEnum status = FailedResponse.StatusEnum.ERROR;
    private final ErrorCode errorCode;
    private final JSONObject data;

    public FailedValidationException(ErrorCode errorCode, JSONObject data) {
        super(data.toString());
        this.errorCode = errorCode;
        this.data = data;
    }

    public FailedValidationException(ErrorCode errorCode, JSONObject data, Throwable e) {
        super(data.toString(), e);
        this.errorCode = errorCode;
        this.data = data;
    }

    /**
     * Format error to the expected FailedResponse object format
     * @return
     */
    public JSONObject getFormattedError() {
        FailedResponse failedResponse = new FailedResponse();

        failedResponse.setStatus(this.status);
        failedResponse.setErrorCode(this.errorCode.getCode());
        failedResponse.setData(this.data);

        return new JSONObject(failedResponse);
    }

    /**
     * Return formatted error as string
     * @return
     */
    public String getFormattedErrorAsString() {
        return getFormattedError().toString();
    }
}
