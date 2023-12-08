package com.docparser.springboot.errorHandler;

public class DuplicateUpload extends RuntimeException {
    public DuplicateUpload(String errorMessage) {
        super(errorMessage);
    }
}
