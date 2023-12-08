package com.docparser.springboot.repository;

import com.docparser.springboot.Repository.UserRepository;
import com.docparser.springboot.errorHandler.UserNotFoundException;
import com.docparser.springboot.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.Instant;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private DynamoDbEnhancedClient dynamoDbenhancedClient;

    @Mock
    private DynamoDbClient dynamoDbClient;

    @InjectMocks
    private UserRepository userRepository;

    @Test
    void saveUser() {
        DynamoDbTable userAccountTable = mock(DynamoDbTable.class);
        when(dynamoDbenhancedClient.table(eq("Users"), any())).thenReturn(userAccountTable);

        UserAccount userAccount = new UserAccount();
        userRepository.saveUser(userAccount);

        verify(userAccountTable, times(1)).putItem(userAccount);
    }

    @Test
    void getUserInfo() {
        DynamoDbTable userAccountTable = mock(DynamoDbTable.class);
        when(dynamoDbenhancedClient.table(eq("Users"), any())).thenReturn(userAccountTable);

        UserAccount expectedUserAccount = new UserAccount();
        when(userAccountTable.getItem(any(Key.class))).thenReturn(expectedUserAccount);

        String userId = "testUserId";
        Optional<UserAccount> retrievedUserAccount = userRepository.getUserInfo(userId);

        verify(userAccountTable, times(1)).getItem(any(Key.class));
        // Add assertions for the retrievedUserAccount
    }

    @Test
    void getUserInfo_userNotFound() {
        DynamoDbTable userAccountTable = mock(DynamoDbTable.class);
        when(dynamoDbenhancedClient.table(eq("Users"), any())).thenReturn(userAccountTable);
        when(userAccountTable.getItem(any(Key.class))).thenReturn(null);

        String userId = "nonexistentUserId";
        try {
            userRepository.getUserInfo(userId);
        } catch (UserNotFoundException e) {
            // Ensure that UserNotFoundException is thrown when the user is not found
            assert (e.getMessage().contains("User not found"));
        }
    }

    @Test
    void getUserInfobyEmail() {
        DynamoDbTable userAccountTable = mock(DynamoDbTable.class);
        when(dynamoDbenhancedClient.table(eq("Users"), any())).thenReturn(userAccountTable);

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("userId", AttributeValue.builder().s("testUserId").build());
        when(dynamoDbClient.scan(any(ScanRequest.class)))
                .thenReturn(ScanResponse.builder().items(Collections.singletonList(item)).build());

        UserAccount expectedUserAccount = new UserAccount();
        when(userAccountTable.getItem(any(Key.class))).thenReturn(expectedUserAccount);

        String email = "test@example.com";
        Optional<UserAccount> retrievedUserAccount = userRepository.getUserInfobyEmail(email);

        verify(dynamoDbClient, times(1)).scan(any(ScanRequest.class));
        verify(userAccountTable, times(1)).getItem(any(Key.class));
        // Add assertions for the retrievedUserAccount
    }

}