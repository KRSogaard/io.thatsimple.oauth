package io.thatsimple.authservice.services.users;

import io.thatsimple.authservice.models.exceptions.UserExistsException;
import io.thatsimple.authservice.models.exceptions.UserNotFoundException;
import io.thatsimple.authservice.services.users.models.UserModel;

public interface UserService {
    String authenticate(String email, String password) throws UserNotFoundException;
    String createUser(UserModel build) throws UserExistsException;
}
