package com.docparser.springboot.model;

public class S3StorageInfo {
    private String etag;
    private String url;

    // Constructors, getters, and setters
    public S3StorageInfo(String etag, String url) {
        this.etag = etag;
        this.url = url;
    }

    // Getters and setters
    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
