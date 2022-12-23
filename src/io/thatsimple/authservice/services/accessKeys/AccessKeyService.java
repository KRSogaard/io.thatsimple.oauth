package io.thatsimple.authservice.services.accessKeys;

import io.thatsimple.authservice.DynamoDBKeys;
import io.thatsimple.authservice.models.exceptions.AccessKeyNotFound;
import io.thatsimple.authservice.models.AccessKey;
import io.thatsimple.authservice.common.dynamodb.AV;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.Hashing;
import org.apache.commons.lang3.RandomStringUtils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AccessKeyService {
    private AmazonDynamoDB dynamoDB;
    private String accessKeysTableName;

    public AccessKeyService(AmazonDynamoDB dynamoDB, String accessKeysTableName) {
        this.dynamoDB = dynamoDB;
        this.accessKeysTableName = accessKeysTableName;
    }

    public AccessKey createAccessKey(String accountId, String userId, String scope) {
        String key = RandomStringUtils.random(12, true, false);
        String token = RandomStringUtils.random(32, true, true);
        String passwordSalt = RandomStringUtils.random(10, true, true);
        String password = hash(token, passwordSalt);

        dynamoDB.putItem(new PutItemRequest(accessKeysTableName, ImmutableMap.<String, AttributeValue>builder()
                .put(DynamoDBKeys.ACCOUNT_ID, AV.of(accountId))
                .put(DynamoDBKeys.KEY, AV.of(key))
                .put(DynamoDBKeys.TOKEN, AV.of(password))
                .put(DynamoDBKeys.TOKEN_SALT, AV.of(passwordSalt))
                .put(DynamoDBKeys.USER_ID, AV.of(userId))
                .put(DynamoDBKeys.SCOPES, AV.of(scope))
                .put(DynamoDBKeys.CREATED, AV.of(Instant.now()))
                .build()));

        return AccessKey.builder()
                .username(accountId + "." + key)
                .token(token)
                .accountId(accountId)
                .userId(userId)
                .created(Instant.now())
                .lastUsed(null)
                .scope(scope)
                .build();
    }

    public List<AccessKey> getAccessKeys(String accountId) {
        QueryRequest queryRequest = new QueryRequest()
                .withTableName(accessKeysTableName)
                .withKeyConditionExpression("#account = :account")
                .withExpressionAttributeNames(ImmutableMap.of(
                        "#account", DynamoDBKeys.ACCOUNT_ID
                ))
                .withExpressionAttributeValues(ImmutableMap.of(":account", AV.of(accountId)));
        QueryResult result = dynamoDB.query(queryRequest);
        if (result.getItems() == null || result.getItems().size() == 0) {
            return new ArrayList<>();
        }

        return result.getItems().stream().map(i -> AccessKey.builder()
                .username(AV.getStringOrNull(i, DynamoDBKeys.ACCOUNT_ID) + "." + AV.getStringOrNull(i, DynamoDBKeys.KEY))
                .accountId(AV.getStringOrNull(i, DynamoDBKeys.ACCOUNT_ID))
                .userId(AV.getStringOrNull(i, DynamoDBKeys.USER_ID))
                .created(AV.toInstantOrNull(i, DynamoDBKeys.CREATED))
                .lastUsed(AV.toInstantOrNull(i, DynamoDBKeys.LAST_USED))
                .scope(AV.getStringOrNull(i, DynamoDBKeys.SCOPES))
                .build()).collect(Collectors.toList());
    }

    public AccessKey authenticate(String username, String token) throws AccessKeyNotFound {
        String[] split = username.split("\\.", 2);
        if (split.length != 2) {
            throw new AccessKeyNotFound();
        }
        GetItemResult result = dynamoDB.getItem(new GetItemRequest(accessKeysTableName, ImmutableMap.<String, AttributeValue>builder()
                .put(DynamoDBKeys.ACCOUNT_ID, AV.of(split[0]))
                .put(DynamoDBKeys.KEY, AV.of(split[1]))
                .build()));
        Map<String, AttributeValue> item = result.getItem();
        if (item == null) {
            throw new AccessKeyNotFound();
        }

        String tokenSalt = AV.getStringOrNull(item, DynamoDBKeys.TOKEN_SALT);
        String hashedToken = hash(token, tokenSalt);
        String storedToken = AV.getStringOrNull(item, DynamoDBKeys.TOKEN);
        if (!hashedToken.equals(storedToken)) {
            throw new AccessKeyNotFound();
        }

        return AccessKey.builder()
                .username(username)
                .accountId(AV.getStringOrNull(item, DynamoDBKeys.ACCOUNT_ID))
                .userId(AV.getStringOrNull(item, DynamoDBKeys.USER_ID))
                .created(AV.toInstantOrNull(item, DynamoDBKeys.CREATED))
                .lastUsed(AV.toInstantOrNull(item, DynamoDBKeys.LAST_USED))
                .scope(AV.getStringOrNull(item, DynamoDBKeys.SCOPES))
                .build();
    }

    public void deleteAccessKey(String username) {
        String[] split = username.split("\\.", 2);
        if (split.length != 2) {
            return;
        }
        dynamoDB.deleteItem(new DeleteItemRequest(accessKeysTableName, ImmutableMap.<String, AttributeValue>builder()
                .put(DynamoDBKeys.ACCOUNT_ID, AV.of(split[0]))
                .put(DynamoDBKeys.KEY, AV.of(split[1]))
                .build()));
    }

    private String hash(String password, String salt) {
        return Hashing.sha256()
                .hashString(password + salt, StandardCharsets.UTF_8)
                .toString();
    }
}
