package io.thatsimple.authservice.models;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Account {
    private String name;
    private String primaryOwner;
}
