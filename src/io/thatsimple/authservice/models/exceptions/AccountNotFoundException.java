package io.thatsimple.authservice.models.exceptions;

public class AccountNotFoundException extends ArchipelagoException {
    public AccountNotFoundException(String accountId) {
        super("The account with id \"" + accountId + "\" was not found");
    }
}
