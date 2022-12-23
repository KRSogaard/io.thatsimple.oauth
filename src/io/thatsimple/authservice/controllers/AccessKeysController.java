package io.thatsimple.authservice.controllers;

import io.thatsimple.authservice.models.AccessKey;
import io.thatsimple.authservice.models.rest.AccessKeyRestResponse;
import io.thatsimple.authservice.models.rest.AccessKeysRestResponse;
import io.thatsimple.authservice.models.rest.CreateAccessKeyRestRequest;
import io.thatsimple.authservice.services.accessKeys.AccessKeyService;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accessKeys")
@Slf4j
public class AccessKeysController {

    private AccessKeyService accessKeyService;

    public AccessKeysController(AccessKeyService accessKeyService) {
        this.accessKeyService = accessKeyService;
    }

    @PostMapping("/{accountId}")
    public AccessKeyRestResponse createAccessKey(
            @PathVariable("accountId") String accountId,
            @RequestBody CreateAccessKeyRestRequest model) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(model.getUserId()));
        AccessKey key = accessKeyService.createAccessKey(accountId, model.getUserId(), model.getScope());
        return AccessKeyRestResponse.from(key);
    }

    @GetMapping("/{accountId}")
    public AccessKeysRestResponse getAccessKeys(
            @PathVariable("accountId") String accountId) {
        List<AccessKey> keys = accessKeyService.getAccessKeys(accountId);
        return AccessKeysRestResponse.from(keys);
    }

    @DeleteMapping("/{accountId}/{username}")
    public void deleteAccessKey(
            @PathVariable("accountId") String accountId,
            @PathVariable("username") String username) {

        String[] split = username.split("\\.", 2);
        String verifiedUsername = accountId + "." + split[1];
        if (!username.equalsIgnoreCase(verifiedUsername)) {
            log.warn("Username did not match account id '{}' was provided, '{}' was calculated", username, verifiedUsername);
            throw new IllegalArgumentException("Username was not verified");
        }
        accessKeyService.deleteAccessKey(verifiedUsername);
    }
}
