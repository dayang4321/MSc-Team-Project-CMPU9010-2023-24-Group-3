package com.docparser.springboot.errorHandler;

public class DocumentNotExist extends RuntimeException {
    public DocumentNotExist(String errorMessage) {
        super(errorMessage);
    }
}
