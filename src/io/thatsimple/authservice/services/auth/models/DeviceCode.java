package io.thatsimple.authservice.services.auth.models;

import lombok.*;

import java.time.Instant;

@Value
@Builder
public class DeviceCode {
    private String deviceCode;
    private String userCode;
    private String scopes;
    private Instant expires;
    private String userId;
    private String clientId;
    private Instant updatedAt;
}
