package io.thatsimple.authservice.services.keys;

import io.thatsimple.authservice.models.exceptions.KeyNotFoundException;
import io.thatsimple.authservice.services.keys.models.JWKKey;
import io.thatsimple.authservice.services.keys.models.KeyDetails;

import java.util.*;

public interface KeyService {
    KeyDetails getSigningKey();
    List<JWKKey> getActiveKeys();
    KeyDetails getKey(String keyId) throws KeyNotFoundException;
}
