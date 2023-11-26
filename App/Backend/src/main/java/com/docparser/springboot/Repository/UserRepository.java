package com.docparser.springboot.Repository;

import com.docparser.springboot.errorHandler.UserNotFoundException;
import com.docparser.springboot.model.FeedBackForm;
import com.docparser.springboot.model.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {
    @Autowired
    private DynamoDbEnhancedClient dynamoDbenhancedClient;
    @Autowired
    private DynamoDbClient dynamoDbClient;

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
        try{
            DynamoDbTable<UserAccount> userAccountDynamoDbTable = getTable();
            // Construct the key with partition and sort key
            Key key = Key.builder().partitionValue(id).build();
            return Optional.ofNullable(userAccountDynamoDbTable.getItem(key));
        }catch (Exception e){
            throw new UserNotFoundException("User not found"+e.getMessage());
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
        Optional<UserAccount> userAccount = Optional.ofNullable(userAccountDynamoDbTable.getItem(Key.builder().partitionValue(item.get("userId").s()).build()));

        return userAccount;
    }

}
