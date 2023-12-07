package com.docparser.springboot.errorHandler;

// Class ErrorResponse is designed to encapsulate details about an error
public class ErrorResponse {
    // errorCode stores the specific error code associated with the error
    private final int errorCode;

    // errorMessage provides a human-readable message describing the error
    private final String errorMessage;

    /*
     * stackTrace contains the stack trace associated with the error for debugging
     * purposes
     */
    private final String stackTrace;

    // Constructor for ErrorResponse that initializes the error details
    public ErrorResponse(int errorCode, String errorMessage, String stackTrace) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.stackTrace = stackTrace;
    }

    // Getter method to retrieve the error code
    public int getErrorCode() {
        return errorCode;
    }

    // Getter method to retrieve the error message
    public String getErrorMessage() {
        return errorMessage;
    }

    // Getter method to retrieve the stack trace
    public String getStackTrace() {
        return stackTrace;
    }
}
