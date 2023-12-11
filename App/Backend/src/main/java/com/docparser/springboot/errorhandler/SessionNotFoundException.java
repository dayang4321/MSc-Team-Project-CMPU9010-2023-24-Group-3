package com.docparser.springboot.errorhandler;

public class SessionNotFoundException extends RuntimeException {
    public SessionNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
