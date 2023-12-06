package com.docparser.springboot.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.Instant;
import java.util.List;

@DynamoDbBean
public class DocumentInfo {

    private String documentID;

    private String documentKey;

    private Instant createdDate;
    private Instant expirationTime;

    private DocumentConfig documentConfig;

    private List<VersionInfo> documentVersions;
    @DynamoDbAttribute("documentID")
    @DynamoDbPartitionKey
    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }
    @DynamoDbAttribute("documentConfig")
    public DocumentConfig getDocumentConfig() {
        return documentConfig;
    }

    public void setDocumentConfig(DocumentConfig documentConfig) {
        this.documentConfig = documentConfig;
    }
    @DynamoDbAttribute("documentVersions")
    public List<VersionInfo> getDocumentVersions() {
        return documentVersions;
    }

    public void setDocumentVersions(List<VersionInfo> documentVersions) {
        this.documentVersions = documentVersions;
    }

    @DynamoDbAttribute("documentKey")
    public String getDocumentKey() {
        return documentKey;
    }
    public  void setDocumentKey(String documentKey) {
        this.documentKey = documentKey;
    }
    @DynamoDbAttribute("createdDate")
    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }
    @DynamoDbAttribute("expirationTime")
    public Instant getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Instant expirationTime) {
        this.expirationTime = expirationTime;
    }
}
