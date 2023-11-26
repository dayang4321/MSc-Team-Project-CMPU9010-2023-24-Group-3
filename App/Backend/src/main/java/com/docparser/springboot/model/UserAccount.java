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
    String userId;
    String email;
    String username;
    String provider;
    private List<FeedBackForm> feedBackForms;


    @DynamoDbAttribute("userId")
    @DynamoDbPartitionKey
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

}
