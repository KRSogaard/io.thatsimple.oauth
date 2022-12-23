package io.thatsimple.authservice.models.exceptions;

import lombok.*;

@Getter
public class UserExistsException extends Exception {
    private String email;

    public UserExistsException(String email) {
        super("The user with email '" + email + "' already exists");
        this.email = email;
    }
}
