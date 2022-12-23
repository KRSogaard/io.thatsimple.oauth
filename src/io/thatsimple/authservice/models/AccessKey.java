package io.thatsimple.authservice.models;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class AccessKey {
    private String username;
    private String token;
    private String accountId;
    private String userId;
    private Instant created;
    private String scope;
    private Instant lastUsed;
}
