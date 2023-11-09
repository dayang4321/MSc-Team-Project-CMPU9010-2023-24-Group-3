package com.docparser.springboot.errorHandler;

public class ErrorResponse {
    private final int errorCode;
    private final String errorMessage;
    private final String stackTrace;

    public ErrorResponse(int errorCode, String errorMessage, String stackTrace) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.stackTrace = stackTrace;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getStackTrace() {
        return stackTrace;
    }
}