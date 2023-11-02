package com.docparser.springboot.Repository;

import com.docparser.springboot.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class UserRepository {
    @Autowired
    private DynamoDbEnhancedClient dynamoDbenhancedClient;

    private DynamoDbTable<Users> getTable() {
        // Create a tablescheme to scan our bean class order
        return dynamoDbenhancedClient.table("Users",
                TableSchema.fromBean(Users.class));
    }

    public void save(Users user) {
        DynamoDbTable<Users> userTable = getTable();
        //Thread.currentThread().setContextClassLoader(Users.class.getClassLoader());

        userTable.putItem(user);
    }

    public Users getUser(String ipAddress) {
        DynamoDbTable<Users> userTable = getTable();
        // Construct the key with partition and sort key
        Key key = Key.builder().partitionValue(ipAddress).build();
        return userTable.getItem(key);
    }
}
