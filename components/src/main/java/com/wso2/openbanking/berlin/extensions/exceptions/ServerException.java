package com.wso2.openbanking.berlin.extensions.exceptions;

import com.wso2.openbanking.berlin.extensions.datamodels.TPPMessage;
import com.wso2.openbanking.berlin.extensions.model.ErrorResponse;
import com.wso2.openbanking.berlin.extensions.utils.ErrorConstants;
import com.wso2.openbanking.berlin.extensions.utils.ErrorUtil;
import org.json.JSONObject;

import javax.ws.rs.core.Response;

/**
 * Exception class for internal server and bad request errors,
 * formatted to return a custom ErrorResponse object.
 */
public class ServerException extends RuntimeException {

    public enum ErrorCode {
        BAD_REQUEST(Response.Status.BAD_REQUEST),
        INTERNAL_SERVER_ERROR(Response.Status.INTERNAL_SERVER_ERROR);

        private final Response.Status status;

        ErrorCode(Response.Status status) {
            this.status = status;
        }

        public Response.Status getStatus() {
            return status;
        }
    }

    private final ErrorCode errorCode;
    private final JSONObject data;

    public ServerException(ErrorCode errorCode, JSONObject data) {
        super(data.toString());
        this.errorCode = errorCode;
        this.data = data;
    }

    public ServerException(ErrorCode errorCode, JSONObject data, Throwable cause) {
        super(data.toString(), cause);
        this.errorCode = errorCode;
        this.data = data;
    }

    public ServerException(ErrorCode errorCode, TPPMessage.CodeEnum code, String message) {
        super(message);
        this.errorCode = errorCode;
        this.data = ErrorUtil.constructBerlinError(
                null, TPPMessage.CategoryEnum.ERROR, code,
                message);
    }

    public ServerException(ErrorCode errorCode, TPPMessage.CodeEnum code, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.data = ErrorUtil.constructBerlinError(
                null, TPPMessage.CategoryEnum.ERROR, code,
                message);
    }

    /**
     * Getter for error status to set to response
     * @return
     */
    public Response.Status getStatus() {
        return this.errorCode.getStatus();
    }

    /**
     * Format the error to a simplified ErrorResponse object
     * @return JSONObject representing the error
     */
    public JSONObject getFormattedError() {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(ErrorResponse.StatusEnum.ERROR);
        errorResponse.setData(data);
        return new JSONObject(errorResponse);
    }

    /**
     * Return the formatted error as a string
     * @return String representation of the error
     */
    public String getFormattedErrorAsString() {
        return getFormattedError().toString();
    }
}