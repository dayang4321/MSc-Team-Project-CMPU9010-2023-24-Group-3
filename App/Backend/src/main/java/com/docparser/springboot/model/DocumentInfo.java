package com.docparser.springboot.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.Instant;
import java.util.List;

// Annotation indicating that this class is a DynamoDB bean.
@DynamoDbBean
public class DocumentInfo {

    /*
     * Fields to store the document's ID, key, creation and expiration times,
     * configuration, and version information
     */
    private String documentID;
    private String documentKey;

    private Instant createdDate;
    private Instant expirationTime;

    private DocumentConfig documentConfig;

    private List<VersionInfo> documentVersions;

    /*
     * Getter for document ID. Annotated as both a DynamoDB attribute and a
     * partition key.
     */
    @DynamoDbAttribute("documentID")
    @DynamoDbPartitionKey
    public String getDocumentID() {
        return documentID;
    }

    // Setter for document ID
    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    // Getter for document configuration
    @DynamoDbAttribute("documentConfig")
    public DocumentConfig getDocumentConfig() {
        return documentConfig;
    }

    // Setter for document configuration
    public void setDocumentConfig(DocumentConfig documentConfig) {
        this.documentConfig = documentConfig;
    }

    // Getter for the list of document versions
    @DynamoDbAttribute("documentVersions")
    public List<VersionInfo> getDocumentVersions() {
        return documentVersions;
    }

    // Setter for the list of document versions
    public void setDocumentVersions(List<VersionInfo> documentVersions) {
        this.documentVersions = documentVersions;
    }

    // Getter for document key
    @DynamoDbAttribute("documentKey")
    public String getDocumentKey() {
        return documentKey;
    }

    // Setter for document key
    public void setDocumentKey(String documentKey) {
        this.documentKey = documentKey;
    }

    // Getter for created date
    @DynamoDbAttribute("createdDate")
    public Instant getCreatedDate() {
        return createdDate;
    }

    // Setter for created date
    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    // Getter for expiration time
    @DynamoDbAttribute("expirationTime")
    public Instant getExpirationTime() {
        return expirationTime;
    }

    // Setter for expiration time
    public void setExpirationTime(Instant expirationTime) {
        this.expirationTime = expirationTime;
    }
}
