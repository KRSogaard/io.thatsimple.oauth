package io.thatsimple.authservice.services.clients.models;

import lombok.*;

@Data
@Builder
public class ClientIdAndSecret {
    private String id;
    private String secret;
}
