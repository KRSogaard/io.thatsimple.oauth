package io.thatsimple.authservice.models.rest;

import lombok.*;

@Builder
@Data
public class ErrorRestResponse {
    private String error;
    private String error_description;
}
