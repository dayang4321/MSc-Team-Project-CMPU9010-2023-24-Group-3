package com.docparser.springboot.errorhandler;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
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



}
