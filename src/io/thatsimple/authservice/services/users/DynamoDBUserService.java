package io.thatsimple.authservice.services.users;

import io.thatsimple.authservice.common.dynamodb.AV;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.services.dynamodbv2.model.*;
import com.google.common.collect.*;
import com.google.common.hash.Hashing;
import io.thatsimple.authservice.models.exceptions.UserExistsException;
import io.thatsimple.authservice.models.exceptions.UserNotFoundException;
import io.thatsimple.authservice.services.DBK;
import io.thatsimple.authservice.services.users.models.UserModel;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;

import java.nio.charset.*;
import java.util.*;

@Slf4j
public class DynamoDBUserService implements UserService {

    private AmazonDynamoDB dynamoDB;
    private String usersTableName;

    public DynamoDBUserService(AmazonDynamoDB dynamoDB,
                               String usersTableName) {
        this.dynamoDB = dynamoDB;
        this.usersTableName = usersTableName;
    }

    @Override
    public String authenticate(String email, String password) throws UserNotFoundException {
        String emailKey = email.toLowerCase().trim();
        QueryRequest queryRequest = new QueryRequest()
                .withIndexName("gsiEmail")
                .withTableName(usersTableName)
                .withKeyConditionExpression("#email = :email")
                .withExpressionAttributeNames(ImmutableMap.of(
                        "#email", DBK.EMAIL
                ))
                .withExpressionAttributeValues(ImmutableMap.of(":email", AV.of(emailKey)));
        QueryResult result = dynamoDB.query(queryRequest);
        if (result.getItems() == null || result.getItems().size() == 0) {
            log.debug("No user with the email '{}' was found", emailKey);
            throw new UserNotFoundException(emailKey);
        }

        Map<String, AttributeValue> item = result.getItems().stream().findFirst().get();
        String salt = item.get(DBK.PASSWORD_SALT).getS();
        String hashedPassword = hash(password, salt);
        String storedPassword = item.get(DBK.PASSWORD).getS();
        if (!hashedPassword.equals(storedPassword)) {
            log.debug("The password for '{}' did not match", emailKey);
            throw new UserNotFoundException(emailKey);
        }

        return item.get(DBK.USER_ID).getS();
    }

    @Override
    public String createUser(UserModel model) throws UserExistsException {
        String emailKey = model.getEmail().toLowerCase().trim();
        QueryRequest queryRequest = new QueryRequest()
                .withIndexName("gsiEmail")
                .withTableName(usersTableName)
                .withKeyConditionExpression("#email = :email")
                .withExpressionAttributeNames(ImmutableMap.of(
                        "#email", DBK.EMAIL
                ))
                .withExpressionAttributeValues(ImmutableMap.of(":email", AV.of(emailKey)));
        QueryResult result = dynamoDB.query(queryRequest);
        if (result.getItems() != null && result.getItems().size() != 0) {
            throw new UserExistsException(model.getEmail());
        }

        String userId = RandomStringUtils.random(20, true, true);
        String passwordSalt = RandomStringUtils.random(10, true, true);
        String password = hash(model.getPassword(), passwordSalt);

        dynamoDB.putItem(new PutItemRequest(usersTableName, ImmutableMap.<String, AttributeValue>builder()
                .put(DBK.USER_ID, AV.of(userId))
                .put(DBK.PASSWORD, AV.of(password))
                .put(DBK.PASSWORD_SALT, AV.of(passwordSalt))
                .put(DBK.NAME, AV.of(model.getName()))
                .put(DBK.EMAIL, AV.of(emailKey))
                .build()));

        return userId;
    }

    private String hash(String password, String salt) {
        return Hashing.sha256()
                .hashString(password + salt, StandardCharsets.UTF_8)
                .toString();
    }
}
