package com.docparser.springboot.errorhandler;

public class DuplicateUpload extends RuntimeException {
    public DuplicateUpload(String errorMessage) {
        super(errorMessage);
    }
}
