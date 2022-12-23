package io.thatsimple.authservice.services.auth;

import io.thatsimple.authservice.models.AuthorizeRequest;
import io.thatsimple.authservice.models.exceptions.TokenExpiredException;
import io.thatsimple.authservice.models.exceptions.TokenNotFoundException;
import io.thatsimple.authservice.models.exceptions.UserNotFoundException;
import io.thatsimple.authservice.services.auth.models.AuthCodeResult;
import io.thatsimple.authservice.services.auth.models.CodeResponse;
import io.thatsimple.authservice.services.auth.models.DeviceCode;
import io.thatsimple.authservice.services.auth.models.DeviceCodeResponse;


public interface AuthService {
    String createAuthToken(String userId, AuthorizeRequest request);
    AuthCodeResult getRequestFromAuthToken(String code) throws TokenNotFoundException, TokenExpiredException;
    String getUserFromAuthCookie(String authCookie) throws UserNotFoundException, TokenExpiredException, TokenNotFoundException;
    CodeResponse createAuthCookie(String userId);
    DeviceCodeResponse createDeviceCode(String clientId, String scope);
    DeviceCode getDeviceCode(String userCode) throws TokenNotFoundException, TokenExpiredException;
    void updateDeviceCode(String userCode, String userId);
    DeviceCode getDeviceCodeByDeviceId(String deviceCode) throws TokenNotFoundException, TokenExpiredException;
    void removeDeviceCode(String userCode);


    void clearExpiredCodes();
}
