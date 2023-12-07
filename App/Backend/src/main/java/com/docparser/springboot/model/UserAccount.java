package com.docparser.springboot.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.List;

@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount {
    String userId; // Unique identifier for the user
    String email; // Email of the user
    String username; // Username of the user
    String provider; // Authentication provider for the user account

    // List of feedback forms associated with the user
    private List<FeedBackForm> feedBackForms;

    // List of user documents associated with the user
    private List<UserDocument> userDocuments;

    // Document configuration settings for the user
    private DocumentConfig userPresets;

    // Getter and setter methods for each field with DynamoDB annotations

    @DynamoDbAttribute("userId")
    @DynamoDbPartitionKey // Marks this attribute as the primary key for DynamoDB
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDbAttribute("email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @DynamoDbAttribute("firstName")
    public String getUsername() {
        return username;
    }

    public void setUsername(String firstName) {
        this.username = firstName;
    }

    @DynamoDbAttribute("feedbackForms")
    public List<FeedBackForm> getFeedBackForms() {
        return feedBackForms;
    }

    public void setFeedBackForms(List<FeedBackForm> feedBackForms) {
        this.feedBackForms = feedBackForms;
    }

    @DynamoDbAttribute("provider")
    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    @DynamoDbAttribute("userPresets")
    public DocumentConfig getUserPresets() {
        return userPresets;
    }

    public void setUserPresets(DocumentConfig userPresets) {
        this.userPresets = userPresets;
    }

    @DynamoDbAttribute("userDocuments")
    public List<UserDocument> getUserDocuments() {
        return userDocuments;
    }

    public void setUserDocuments(List<UserDocument> userDocuments) {
        this.userDocuments = userDocuments;
    }

}
