package io.thatsimple.authservice.models.rest;

import io.thatsimple.authservice.models.client.LogInResponse;
import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class LogInRestResponse {
    private String authToken;

    public LogInResponse toInternal() {
        return LogInResponse.builder()
                .authToken(getAuthToken())
                .build();
    }
}
