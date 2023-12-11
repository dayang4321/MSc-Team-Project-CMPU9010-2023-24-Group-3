package com.docparser.springboot.repository;

import com.docparser.springboot.model.FeedBackForm;
import com.docparser.springboot.model.SessionInfo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.Instant;
import java.util.Collections;

@Repository
@RequiredArgsConstructor
public class SessionRepository {

    private final DynamoDbEnhancedClient dynamoDbenhancedClient;
    private final  DynamoDbClient dynamoDbClient;
    Logger logger = LoggerFactory.getLogger(SessionRepository.class);
    public static final TableSchema<FeedBackForm> TABLE_SCHEMA_FEEDBACKFORM = TableSchema
            .builder(FeedBackForm.class)
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

    public static final TableSchema<SessionInfo> SESSION_INFO_TABLE_SCHEMA = TableSchema.builder(SessionInfo.class)
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
                    EnhancedType.documentOf(FeedBackForm.class, TABLE_SCHEMA_FEEDBACKFORM)),
                    a -> a.name("feedBackForms")
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

    public void deleteUserSession(String userSessionId) {
        logger.info("deleting user session ID on logout");

        AttributeValue value = AttributeValue.builder().s(userSessionId).build();
        DeleteItemResponse deleteItemResponse = dynamoDbClient.deleteItem(DeleteItemRequest.builder()
                .tableName("SessionInfo")
                .key(Collections.singletonMap("sessionID", value))
                .build());
        logger.info("session deleted:{}" ,deleteItemResponse);
    }
}
