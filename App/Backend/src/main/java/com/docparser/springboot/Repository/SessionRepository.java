package com.docparser.springboot.Repository;

import com.docparser.springboot.model.FeedBackForm;
import com.docparser.springboot.model.SessionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;

import java.time.Instant;

@Repository
public class SessionRepository {
    @Autowired
    private DynamoDbEnhancedClient dynamoDbenhancedClient;

    public static final TableSchema<FeedBackForm> TABLE_SCHEMA_FEEDBACKFORM = TableSchema.builder(FeedBackForm.class)
            .newItemSupplier(FeedBackForm::new)
            .addAttribute(String.class, a -> a.name("email")
                    .getter(FeedBackForm::getEmail)
                    .setter(FeedBackForm::setEmail))
            .addAttribute(String.class, a -> a.name("whatUserLiked")
                    .getter(FeedBackForm::getWhatUserLiked)
                    .setter(FeedBackForm::setWhatUserLiked))
            .addAttribute(String.class, a -> a.name("whatUserDisliked")
                    .getter(FeedBackForm::getWhatUserDisliked)
                    .setter(FeedBackForm::setWhatUserDisliked))
            .addAttribute(String.class, a -> a.name("newFeatures")
                    .getter(FeedBackForm::getNewFeatures)
                    .setter(FeedBackForm::setNewFeatures))
            .build();

    public static final TableSchema<SessionInfo> SESSION_INFO_TABLE_SCHEMA =
            TableSchema.builder(SessionInfo.class)
                    .newItemSupplier(SessionInfo::new)
                    .addAttribute(String.class, a -> a.name("sessionID")
                            .getter(SessionInfo::getSessionID)
                            .setter(SessionInfo::setSessionID)
                            .addTag(StaticAttributeTags.primaryPartitionKey()))
                    .addAttribute(String.class, a -> a.name("tokenID")
                            .getter(SessionInfo::getTokenID)
                            .setter(SessionInfo::setTokenID))
                    .addAttribute(Instant.class, a -> a.name("createdDate")
                            .getter(SessionInfo::getCreatedDate)
                            .setter(SessionInfo::setCreatedDate))
                    .addAttribute(Instant.class, a -> a.name("expirationTime")
                            .getter(SessionInfo::getExpirationTime)
                            .setter(SessionInfo::setExpirationTime))
                    .addAttribute(EnhancedType.listOf(
                            EnhancedType.documentOf(FeedBackForm.class, TABLE_SCHEMA_FEEDBACKFORM)), a -> a.name("feedBackForms")
                            .getter(SessionInfo::getFeedBackForms)
                            .setter(SessionInfo::setFeedBackForms))
                    .build();
    private DynamoDbTable<SessionInfo> getTable() {
        // Create a tablescheme to scan our bean class order
        return dynamoDbenhancedClient.table("SessionInfo", SESSION_INFO_TABLE_SCHEMA);
    }

    public void save(SessionInfo sessionInfo) {
        DynamoDbTable<SessionInfo> sessionTable = getTable();
        sessionTable.putItem(sessionInfo);
    }

    public SessionInfo getSessionInfo(String sessionID) {
        DynamoDbTable<SessionInfo> sessionTable = getTable();
        // Construct the key with partition and sort key
        Key key = Key.builder().partitionValue(sessionID).build();
        return sessionTable.getItem(key);
    }
}
