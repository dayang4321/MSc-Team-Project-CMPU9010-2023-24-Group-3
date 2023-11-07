package com.docparser.springboot.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.Instant;
import java.util.List;

@DynamoDbBean
public class SessionInfo {

    private String sessionID;
    private String ipAddress;
    private String tokenID;
    private Instant createdDate;

    private List<FeedBackForm> feedBackForms;


    @DynamoDbAttribute("ipAddress")
    public String getIpAddress() {
        return ipAddress;
    }

    public SessionInfo(String sessionID, String ipAddress, String tokenID, Instant createdDate) {
        this.ipAddress = ipAddress;
        this.tokenID = tokenID;
        this.createdDate = createdDate;
        this.sessionID = sessionID;
    }

    public SessionInfo() {
        // Initialization code if needed
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @DynamoDbAttribute("token")

    public String getTokenID() {
        return tokenID;
    }

    public void setTokenID(String tokenID) {
        this.tokenID = tokenID;
    }

    @DynamoDbAttribute("createdDate")
    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }


    @DynamoDbAttribute("sessionID")
    @DynamoDbPartitionKey
    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }
    @DynamoDbAttribute("feedbackForms")
    public List<FeedBackForm> getFeedBackForms() {
        return feedBackForms;
    }

    public void setFeedBackForms(List<FeedBackForm> feedBackForms) {
        this.feedBackForms = feedBackForms;
    }
}
