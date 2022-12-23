package io.thatsimple.authservice.models.errors;

import io.thatsimple.authservice.models.exceptions.UnauthorizedException;
import org.springframework.http.HttpStatus;

public class CommonExceptionHandler {

    public static ProblemDetailRestResponse.ProblemDetailRestResponseBuilder from(IllegalArgumentException exp) {
        return ProblemDetailRestResponse.builder()
                .error("client/illegalArgument")
                .title("The provided data was not acceptable")
                .status(HttpStatus.NOT_ACCEPTABLE.value())
                .detail(exp.getMessage());
    }

    public static ProblemDetailRestResponse.ProblemDetailRestResponseBuilder from(UnauthorizedException ex) {
        return ProblemDetailRestResponse.builder()
                .error("unauthorized")
                .title("The request was not authorized")
                .status(HttpStatus.UNAUTHORIZED.value())
                .detail("The request was not authorized");
    }
}
