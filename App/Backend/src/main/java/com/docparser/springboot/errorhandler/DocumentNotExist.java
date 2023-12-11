package com.docparser.springboot.errorhandler;

public class DocumentNotExist extends RuntimeException {
    public DocumentNotExist(String errorMessage) {
        super(errorMessage);
    }
}
