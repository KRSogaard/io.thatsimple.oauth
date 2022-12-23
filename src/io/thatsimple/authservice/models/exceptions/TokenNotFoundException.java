package io.thatsimple.authservice.models.exceptions;

import lombok.*;

@Getter
public class TokenNotFoundException extends Exception {
    String token;

    public TokenNotFoundException(String token) {
        super();
        this.token = token;
    }
}
