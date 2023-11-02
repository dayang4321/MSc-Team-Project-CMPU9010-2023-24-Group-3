package com.docparser.springboot.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.Instant;

@DynamoDbBean
public class Users {
    private String ipAddress;
    private String tokenID;
    private Instant createdDate;
    @DynamoDbPartitionKey
    @DynamoDbAttribute("ip_address")
    public String getIpAddress() {
        return ipAddress;
    }

    public Users(String ipAddress, String tokenID, Instant createdDate) {
        this.ipAddress = ipAddress;
        this.tokenID = tokenID;
        this.createdDate = createdDate;
    }
    public Users() {
        // Initialization code if needed
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getTokenID() {
        return tokenID;
    }

    public void setTokenID(String tokenID) {
        this.tokenID = tokenID;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }
}
