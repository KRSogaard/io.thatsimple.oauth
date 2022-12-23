package io.thatsimple.authservice.controllers;

import io.thatsimple.authservice.AccountService;
import io.thatsimple.authservice.models.exceptions.AccountExistsException;
import io.thatsimple.authservice.models.exceptions.UserExistsException;
import io.thatsimple.authservice.models.rest.RegisterAccountRestRequest;
import io.thatsimple.authservice.services.users.UserService;
import io.thatsimple.authservice.services.users.models.UserModel;
import io.thatsimple.authservice.utils.Rando;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    private UserService userService;
    private AccountService accountService;

    public UserController(UserService userService,
                          AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    @PostMapping("/register")
    public void register(@RequestBody RegisterAccountRestRequest model) throws UserExistsException {
        Preconditions.checkNotNull(model);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(model.getName()), "Name is required");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(model.getEmail()), "Email is required");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(model.getPassword()), "Password is required");

        String userId = userService.createUser(UserModel.builder()
                .name(model.getName())
                .email(model.getEmail())
                .password(model.getPassword())
                .build());

        String accountName = model.getName()
                .replaceAll("[^A-Za-z0-9-]", "-")
                .replaceAll("[-]+", "-");
        accountName = accountName.substring(0, Math.min(accountName.length(), 15));

        String base = accountName;
        boolean accountCreated = false;
        while (!accountCreated) {
            try {
                log.info("Creating account '{}'", accountName);
                accountService.createAccount(accountName);
                accountCreated = true;
            } catch (AccountExistsException e) {
                log.warn("The account id '{}' already existed", accountName);
                accountName = base + "-" + Rando.getRandomString().substring(0, 4);
            }
        }

        accountService.attachUserToAccount(accountName, userId);
    }
}
