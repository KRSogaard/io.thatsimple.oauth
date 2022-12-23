package io.thatsimple.authservice.models.errors;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public abstract class RFC7807ExceptionHandler {

    protected  <T extends Exception> ResponseEntity<ProblemDetailRestResponse> createResponse(HttpServletRequest req,
                                                                                              ProblemDetailRestResponse.ProblemDetailRestResponseBuilder builder) {
        ProblemDetailRestResponse detail = builder
                .instance(req.getRequestURI())
                .build();
        return ResponseEntity.status(detail.getStatus())
                .contentType(ProblemDetailRestResponse.JSON_MEDIA_TYPE)
                .body(detail);
    }
}
