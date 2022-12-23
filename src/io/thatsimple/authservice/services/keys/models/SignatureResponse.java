package io.thatsimple.authservice.services.keys.models;

import lombok.*;

@Builder
@Data
public class SignatureResponse {
    private String keyId;
    private String signature;
}
