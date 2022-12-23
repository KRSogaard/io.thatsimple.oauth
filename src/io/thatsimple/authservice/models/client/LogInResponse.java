package io.thatsimple.authservice.models.client;

import lombok.*;

@Builder
@Value
public class LogInResponse {
    private String authToken;
}
