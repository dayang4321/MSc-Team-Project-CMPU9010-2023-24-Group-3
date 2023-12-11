package com.docparser.springboot.model;

import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.Instant;
import java.util.List;

// Annotating the class to be a DynamoDB bean for ORM-like functionality
@DynamoDbBean
@ToString
public class SessionInfo {

    // Declaring private fields for session information
    private String sessionID;
    private String tokenID;

    private Instant createdDate;
    private Instant expirationTime;

    // List of feedback forms associated with the session
    private List<FeedBackForm> feedBackForms;


    // Constructor for session info including expiration time
    public SessionInfo(String sessionID, String tokenID, Instant createdDate, Instant expirationTime) {
        this.tokenID = tokenID;
        this.createdDate = createdDate;
        this.sessionID = sessionID;
        this.expirationTime = expirationTime;
    }

    // Default constructor for cases where session info is set via setters
    public SessionInfo() {
        // Initialization code if needed
    }

    // Getter for expiration time with DynamoDB attribute mapping
    @DynamoDbAttribute("expirationTime")
    public Instant getExpirationTime() {
        return expirationTime;
    }

    // Setter for expiration time
    public void setExpirationTime(Instant expirationTime) {
        this.expirationTime = expirationTime;
    }

    // Getter for token ID with DynamoDB attribute mapping
    @DynamoDbAttribute("token")
    public String getTokenID() {
        return tokenID;
    }

    // Setter for token ID
    public void setTokenID(String tokenID) {
        this.tokenID = tokenID;
    }

    // Getter for created date with DynamoDB attribute mapping
    @DynamoDbAttribute("createdDate")
    public Instant getCreatedDate() {
        return createdDate;
    }

    // Setter for created date
    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    // Getter for session ID with DynamoDB attribute mapping
    // This field is also marked as a partition key for DynamoDB
    @DynamoDbAttribute("sessionID")
    @DynamoDbPartitionKey
    public String getSessionID() {
        return sessionID;
    }

    // Setter for session ID
    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    // Getter for feedback forms list with DynamoDB attribute mapping
    @DynamoDbAttribute("feedbackForms")
    public List<FeedBackForm> getFeedBackForms() {
        FeedBackForm feedBackForm = new FeedBackForm();
        return feedBackForms;
    }

    // Setter for feedback forms list
    public void setFeedBackForms(List<FeedBackForm> feedBackForms) {
        this.feedBackForms = feedBackForms;
    }
}
