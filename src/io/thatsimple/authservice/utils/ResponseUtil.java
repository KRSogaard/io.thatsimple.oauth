package io.thatsimple.authservice.utils;

import io.thatsimple.authservice.models.rest.ErrorRestResponse;
import org.springframework.http.*;

import java.net.*;

public class ResponseUtil {
    public static ResponseEntity<String> respons(HttpStatus code, Object obj) {
        return ResponseEntity.status(code).body(JSONUtil.serialize(obj));
    }

    public static ResponseEntity<String> redirect(String url) {
        try {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(new URI(url))
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static ResponseEntity<String> unauthorizedAuthToken() {
        return ResponseUtil.respons(HttpStatus.UNAUTHORIZED, ErrorRestResponse.builder()
                .error("invalid_client")
                .error_description("Auth Token not approved")
                .build());
    }

    public static ResponseEntity<String> invalidGrantType() {
        return ResponseUtil.respons(HttpStatus.BAD_REQUEST, ErrorRestResponse.builder()
                .error("invalid_request")
                .error_description("Invalid grant type")
                .build());
    }
}
