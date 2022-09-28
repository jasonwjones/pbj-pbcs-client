package com.jasonwjones.pbcs.client.sso;

import com.jasonwjones.pbcs.client.sso.entity.AccessTokenResponse;
import com.jasonwjones.pbcs.client.sso.entity.DeviceCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.concurrent.TimeUnit;

public class IDCSDeviceCodeFlow {

    private static final Logger logger = LoggerFactory.getLogger(IDCSDeviceCodeFlow.class);

    private final String clientId;

    private final String tenant;

    private final RestTemplateWithUrlEncodedExtensions restTemplate = new RestTemplateWithUrlEncodedExtensions();

    private final RefreshTokenStorage refreshTokenStorage;

    private final String tokenEndpoint;

    public IDCSDeviceCodeFlow(String clientId, String tenant) {
        this(clientId, tenant, new SimpleRefreshTokenStorage());
    }

    public IDCSDeviceCodeFlow(String clientId, String tenant, RefreshTokenStorage refreshTokenStorage) {
        this.clientId = clientId;
        this.tenant = tenant;
        this.refreshTokenStorage = refreshTokenStorage;
        this.tokenEndpoint = "https://idcs-" + tenant + ".identity.oraclecloud.com/oauth2/v1/token";
    }

    public RefreshableToken getToken(String scope) throws NoExistingRefreshTokenException {
        String existingRefreshCode = refreshTokenStorage.getRefreshToken(tenant, clientId, scope);
        if (existingRefreshCode == null) {
            DeviceCode request = initDeviceCodeFlow(scope);
            throw new NoExistingRefreshTokenException(scope, request);
        } else {
            return new RefreshableTokenImpl(scope, existingRefreshCode, false);
        }
    }

    private DeviceCode initDeviceCodeFlow(String scope) {
        String url = "https://idcs-" + tenant + ".identity.oraclecloud.com/oauth2/v1/device";
        try {
            String[] params = {
                    "response_type", "device_code",
                    "scope", scope,
                    "client_id", clientId
            };
            ResponseEntity<DeviceCode> response = restTemplate.postForEntityWithUrlEncodedParams(url, DeviceCode.class, params);
            return response.getBody();
        } catch (Exception e) {
            throw new IDCSException(e);
        }
    }

    public class RefreshableTokenImpl implements RefreshableToken {

        private final String scope;

        private Long refreshTime;

        private String accessToken;

        private String refreshToken;

        private Integer expiresIn;

        public RefreshableTokenImpl(String scope, String deviceCodeOrRefreshToken, boolean isDeviceCode) {
            this.scope = scope;
            if (isDeviceCode) {
                initialize(deviceCodeOrRefreshToken);
            } else {
                this.refreshToken = deviceCodeOrRefreshToken;
            }
        }

        private void initialize(String deviceCode) {
            String[] params = {
                    "grant_type", "urn:ietf:params:oauth:grant-type:device_code",
                    "device_code", deviceCode,
                    "client_id", clientId
            };
            ResponseEntity<AccessTokenResponse> accessTokenResponseResponseEntity = restTemplate.postForEntityWithUrlEncodedParams(tokenEndpoint, AccessTokenResponse.class, params);
            AccessTokenResponse currentResponse = accessTokenResponseResponseEntity.getBody();
            updateFromTokenResponse(currentResponse);
        }

        private void updateFromTokenResponse(AccessTokenResponse accessTokenResponse) {
            accessToken = accessTokenResponse.getAccessToken();
            refreshToken = accessTokenResponse.getRefreshToken();
            expiresIn = accessTokenResponse.getExpiresIn();
            refreshTime = System.currentTimeMillis();
            refreshTokenStorage.put(tenant, clientId, scope, refreshToken);
        }

        @Override
        public String getAccessToken() {
            if (isExpired()) refresh();
            return accessToken;
        }

        @Override
        public void refresh() {
            try {
                String[] params = {
                        "grant_type", "refresh_token",
                        "refresh_token", refreshToken,
                        "client_id", clientId
                };
                ResponseEntity<AccessTokenResponse> accessTokenResponseResponseEntity = restTemplate.postForEntityWithUrlEncodedParams(tokenEndpoint, AccessTokenResponse.class, params);
                updateFromTokenResponse(accessTokenResponseResponseEntity.getBody());
            } catch (HttpClientErrorException e) {
                if (refreshTokenStorage.clear(tenant, clientId, scope)) {
                    logger.info("Cleared cached refresh token");
                }
                throw new IDCSException("Unable to refresh token", e);
            }
        }

        @Override
        public boolean isExpired() {
            // If access token is null then we treat this as the first invocation of instancing from an existing refresh
            // token. We don't strictly need to check also for refreshTime also being null, but we do just as an extra
            // sanity check
            return accessToken == null || refreshTime == null || refreshTime + TimeUnit.SECONDS.toMillis(expiresIn) >= System.currentTimeMillis();
        }

    }

    public class NoExistingRefreshTokenException extends RuntimeException {

        private final String scope;

        private final String deviceCode;

        private final String verificationUri;

        private final String userCode;

        public NoExistingRefreshTokenException(String scope, DeviceCode deviceCode) {
            this.deviceCode = deviceCode.getDeviceCode();
            this.verificationUri = deviceCode.getVerificationUri();
            this.scope = scope;
            this.userCode = deviceCode.getUserCode();
        }

        public String getVerificationUri() {
            return verificationUri;
        }

        public String getUserCode() {
            return userCode;
        }

        public RefreshableToken confirm() {
            return new RefreshableTokenImpl(scope, deviceCode, true);
        }

    }

}