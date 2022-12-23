package io.thatsimple.authservice.models.exceptions;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super();
    }
    public UnauthorizedException(String message) {
        super(message);
    }
}
