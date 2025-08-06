package org.wso2.openbanking.nextgenpsd2.extensions.exceptions;

import org.json.JSONObject;
import org.wso2.openbanking.nextgenpsd2.extensions.utils.ErrorUtil;

import javax.ws.rs.core.Response;

/**
 * Exception class for internal server and bad request errors caused within the service extension,
 * formatted to return a custom ErrorResponse object.
 */
public class ExtensionException extends Exception {

    private final Response.Status errorStatus;
    private final JSONObject data;

    public ExtensionException(Response.Status status, String message, String description) {
        super(description);
        this.errorStatus = status;
        this.data = ErrorUtil.getErrorDataObject(message, description);
    }

    public ExtensionException(Response.Status status, String message, String description, Throwable cause) {
        super(description, cause);
        this.errorStatus = status;
        this.data = ErrorUtil.getErrorDataObject(message, description);
    }

    /**
     * Getter for error status to set to response.
     * @return
     */
    public Response.Status getStatus() {
        return this.errorStatus;
    }

    /**
     * Format the error to a simplified ErrorResponse object.
     * @return JSONObject representing the error
     */
    public JSONObject getFormattedError() {
        return ErrorUtil.getFormattedErrorResponse(data);
    }

    /**
     * Return the formatted error as a string.
     * @return String representation of the error
     */
    public String getFormattedErrorAsString() {
        return getFormattedError().toString();
    }
}
