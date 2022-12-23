package io.thatsimple.authservice;

import io.thatsimple.authservice.common.dynamodb.AV;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.thatsimple.authservice.models.exceptions.AccountExistsException;
import io.thatsimple.authservice.models.exceptions.AccountNotFoundException;
import io.thatsimple.authservice.models.Account;
import io.thatsimple.authservice.models.AccountDetails;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class AccountService {
    private AmazonDynamoDB dynamoDB;
    private String accountTableName;
    private String accountMappingTable;

    public AccountService(AmazonDynamoDB dynamoDB, String accountTableName, String accountMappingTable,
                          String accountGitTableName) {
        this.dynamoDB = dynamoDB;
        this.accountTableName = accountTableName;
        this.accountMappingTable = accountMappingTable;
    }

    public AccountDetails getAccountDetails(String accountId) throws AccountNotFoundException {
        GetItemResult result = dynamoDB.getItem(new GetItemRequest(accountTableName, ImmutableMap.<String, AttributeValue>builder()
                .put(DynamoDBKeys.ACCOUNT_ID, AV.of(accountId))
                .build()));
        if (result.getItem() == null) {
            throw new AccountNotFoundException(accountId);
        }

        Map<String, AttributeValue> item = result.getItem();
        return AccountDetails.builder()
                .id(AV.getStringOrNull(item, DynamoDBKeys.ACCOUNT_ID))
                .build();
    }

    public ImmutableList<String> getAccountsForUser(String userId) {
        QueryRequest queryRequest = new QueryRequest()
                .withTableName(accountMappingTable)
                .withKeyConditionExpression("#userId = :userId")
                .withExpressionAttributeNames(ImmutableMap.of(
                        "#userId", DynamoDBKeys.USER_ID
                ))
                .withExpressionAttributeValues(ImmutableMap.of(":userId", AV.of(userId)));
        QueryResult result = dynamoDB.query(queryRequest);

        ImmutableList.Builder<String> list = ImmutableList.<String>builder();
        if (result.getItems() != null && result.getItems().size() != 0) {
            result.getItems().stream().forEach(i -> {
                list.add(i.get(DynamoDBKeys.ACCOUNT_ID).getS());
            });
        }

        return list.build();
    }

    public void createAccount(String accountId) throws AccountExistsException {
        try {
            getAccount(accountId);
            throw new AccountExistsException(accountId);
        } catch (AccountNotFoundException exp) {
            // This is what we want!
        }
        dynamoDB.putItem(new PutItemRequest(accountTableName, ImmutableMap.<String, AttributeValue>builder()
                .put(DynamoDBKeys.ACCOUNT_ID, AV.of(accountId))
                .build()));
    }

    public void attachUserToAccount(String accountId, String userId) {
        if (isUserInAccount(accountId, userId)) {
            return;
        }
        dynamoDB.putItem(new PutItemRequest(accountMappingTable, ImmutableMap.<String, AttributeValue>builder()
                .put(DynamoDBKeys.USER_ID, AV.of(userId))
                .put(DynamoDBKeys.ACCOUNT_ID, AV.of(accountId))
                .build()));
    }

    public boolean isUserInAccount(String accountId, String userId) {
        GetItemResult result = dynamoDB.getItem(accountMappingTable, ImmutableMap.<String, AttributeValue>builder()
                .put(DynamoDBKeys.ACCOUNT_ID, AV.of(userId))
                .put(DynamoDBKeys.USER_ID, AV.of(accountId))
                .build());
        return result.getItem() != null;
    }

    public Account getAccount(String accountId) throws AccountNotFoundException {
        GetItemResult result = dynamoDB.getItem(accountTableName, ImmutableMap.<String, AttributeValue>builder()
                .put(DynamoDBKeys.ACCOUNT_ID, AV.of(accountId))
                .build());
        if (result.getItem() == null) {
            throw new AccountNotFoundException(accountId);
        }
        return Account.builder().build();
    }
}
