package io.thatsimple.authservice.models.rest;

import io.thatsimple.authservice.models.client.ActivateDeviceRequest;
import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ActivateDeviceRestRequest {
    private String userCode;
    private String userId;

    public static ActivateDeviceRestRequest from(ActivateDeviceRequest request) {
        return ActivateDeviceRestRequest.builder()
                .userCode(request.getUserCode())
                .userId(request.getUserId())
                .build();
    }
}
