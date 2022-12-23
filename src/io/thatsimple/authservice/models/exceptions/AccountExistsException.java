package io.thatsimple.authservice.models.exceptions;

public class AccountExistsException extends ArchipelagoException {
    public AccountExistsException(String accountId) {
        super("The account with id \"" + accountId + "\" already exists");
    }
}
