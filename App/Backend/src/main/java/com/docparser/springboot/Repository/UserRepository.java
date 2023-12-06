package com.docparser.springboot.Repository;

import com.docparser.springboot.errorHandler.UserNotFoundException;
import com.docparser.springboot.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.docparser.springboot.Repository.DocumentRepository.DOCUMENT_CONFIG_PARAMS;
import static com.docparser.springboot.Repository.SessionRepository.TABLE_SCHEMA_FEEDBACKFORM;

@Repository
public class UserRepository {
    @Autowired
    private DynamoDbEnhancedClient dynamoDbenhancedClient;
    @Autowired
    private DynamoDbClient dynamoDbClient;

    public static final TableSchema<UserDocument> TABLE_SCHEMA_DOCUMENTS = TableSchema.builder(UserDocument.class)
            .newItemSupplier(UserDocument::new)
            .addAttribute(String.class, a -> a.name("documentID")
                    .getter(UserDocument::getDocumentID)
                    .setter(UserDocument::setDocumentID))
            .addAttribute(String.class, a -> a.name("documentKey")
                    .getter(UserDocument::getDocumentKey)
                    .setter(UserDocument::setDocumentKey))
            .addAttribute(Instant.class, a -> a.name("createdDate")
                    .getter(UserDocument::getCreatedDate)
                    .setter(UserDocument::setCreatedDate))
            .addAttribute(Instant.class, a -> a.name("expirationTime")
                    .getter(UserDocument::getExpirationTime)
                    .setter(UserDocument::setExpirationTime))
            .build();
    public static final TableSchema<UserAccount> TABLE_SCHEMA_USER = TableSchema.builder(UserAccount.class)
            .newItemSupplier(UserAccount::new)
            .addAttribute(String.class, a -> a.name("userId")
                    .getter(UserAccount::getUserId)
                    .setter(UserAccount::setUserId)
                    .addTag(StaticAttributeTags.primaryPartitionKey()))
            .addAttribute(String.class, a -> a.name("email")
                    .getter(UserAccount::getEmail)
                    .setter(UserAccount::setEmail))
            .addAttribute(String.class, a -> a.name("username")
                    .getter(UserAccount::getUsername)
                    .setter(UserAccount::setUsername))
            .addAttribute(String.class, a -> a.name("provider")
                    .getter(UserAccount::getProvider)
                    .setter(UserAccount::setProvider))
            .addAttribute(EnhancedType.listOf(
                    EnhancedType.documentOf(UserDocument.class, TABLE_SCHEMA_DOCUMENTS)), a -> a.name("userDocuments")  // DocumentConfig.class
                    .getter(UserAccount::getUserDocuments)
                    .setter(UserAccount::setUserDocuments))
            .addAttribute(EnhancedType.documentOf(DocumentConfig.class, DOCUMENT_CONFIG_PARAMS), a -> a.name("userPresets")  // DocumentConfig.class
                    .getter(UserAccount::getUserPresets)
                    .setter(UserAccount::setUserPresets))
            .addAttribute(EnhancedType.listOf(
                    EnhancedType.documentOf(FeedBackForm.class, TABLE_SCHEMA_FEEDBACKFORM)), a -> a.name("feedBackForms")
                    .getter(UserAccount::getFeedBackForms)
                    .setter(UserAccount::setFeedBackForms))
            .build();


    private DynamoDbTable<UserAccount> getTable() {
        // Create a tablescheme to scan our bean class order
        return dynamoDbenhancedClient.table("Users", TABLE_SCHEMA_USER);
    }

    public void saveUser(UserAccount userAccount) {
        DynamoDbTable<UserAccount> sessionTable = getTable();
        sessionTable.putItem(userAccount);
    }

    public Optional<UserAccount> getUserInfo(String id) {
        try {
            DynamoDbTable<UserAccount> userAccountDynamoDbTable = getTable();
            // Construct the key with partition and sort key
            Key key = Key.builder().partitionValue(id).build();
            return Optional.ofNullable(userAccountDynamoDbTable.getItem(key));
        } catch (Exception e) {
            throw new UserNotFoundException("User not found" + e.getMessage());
        }
    }

    public Optional<UserAccount> getUserInfobyEmail(String email) {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
        DynamoDbTable<UserAccount> userAccountDynamoDbTable = getTable();
        expressionAttributeValues.put(":val", AttributeValue.builder().s(email).build());
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName("Users")
                .filterExpression("email = :val")
                .expressionAttributeValues(expressionAttributeValues)
                .build();
        ScanResponse response = dynamoDbClient.scan(scanRequest);
        Map<String, AttributeValue> item = response.items().stream().findFirst().orElse(null);
        if (item == null) {
            return Optional.empty();
        }
        Optional<UserAccount> userAccount = Optional.ofNullable(userAccountDynamoDbTable.getItem(Key.builder().partitionValue(item.get("userId").s()).build()));

        return userAccount;
    }

}
