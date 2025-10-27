package com.activiti.extension.bean;

/**
 * Response wrapper class
 */
public class ApiResponse {
    private final int statusCode;
    private final String message;
    private final String body;
    private final boolean success;

    public ApiResponse(int statusCode, String message, String body, boolean success) {
        this.statusCode = statusCode;
        this.message = message;
        this.body = body;
        this.success = success;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public String getBody() {
        return body;
    }

    public boolean isSuccess() {
        return success;
    }
}
