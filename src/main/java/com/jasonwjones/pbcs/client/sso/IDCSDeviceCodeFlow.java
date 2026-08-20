package com.jasonwjones.pbcs.client.sso;

import com.jasonwjones.pbcs.client.sso.entity.AccessTokenResponse;
import com.jasonwjones.pbcs.client.sso.entity.DeviceCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.concurrent.TimeUnit;

/**
 * This is a "micro" implementation of the OAuth2 device code flow for use with IDCS. It is implemented using native code
 * as opposed to the <a href="https://docs.oracle.com/en/solutions/authenticate-java-app-with-identity-cloud/test-java-application1.html">IDCS SDK</a>
 * so that we don't have to link in the IDCS JAR, which is not in Maven Central.
 */
public class IDCSDeviceCodeFlow {

    private static final Logger logger = LoggerFactory.getLogger(IDCSDeviceCodeFlow.class);

    private final String clientId;

    private final String tenant;

    private final RestTemplateWithUrlEncodedExtensions restTemplate = new RestTemplateWithUrlEncodedExtensions();

    private final RefreshTokenStorage refreshTokenStorage;

    private final String tokenEndpoint;

    /**
     * Constructs an instance using an in-memory refresh token store.
     *
     * @param clientId the OAuth client ID
     * @param tenant the IDCS tenant
     */
    public IDCSDeviceCodeFlow(String clientId, String tenant) {
        this(clientId, tenant, new SimpleRefreshTokenStorage());
    }

    /**
     * Constructs an instance using the given refresh token storage.
     *
     * @param clientId the OAuth client ID
     * @param tenant the IDCS tenant
     * @param refreshTokenStorage the storage to use for refresh tokens
     */
    public IDCSDeviceCodeFlow(String clientId, String tenant, RefreshTokenStorage refreshTokenStorage) {
        this.clientId = clientId;
        this.tenant = tenant;
        this.refreshTokenStorage = refreshTokenStorage;
        this.tokenEndpoint = "https://idcs-" + tenant + ".identity.oraclecloud.com/oauth2/v1/token";
    }

    /**
     * Gets a refreshable token for the given scope, using a cached refresh token if available.
     *
     * @param scope the OAuth scope to request
     * @return a refreshable token for the scope
     * @throws NoExistingRefreshTokenException if no cached refresh token exists; the exception carries the
     * device code details needed to have the user authorize a new one
     */
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

    /**
     * A {@link RefreshableToken} implementation that lazily exchanges a device code (or an existing refresh
     * token) for an access token, refreshing it as needed.
     */
    public class RefreshableTokenImpl implements RefreshableToken {

        private final String scope;

        private Long refreshTime;

        private String accessToken;

        private String refreshToken;

        private Integer expiresIn;

        /**
         * Constructs an instance for the given scope, initialized from either a device code or an existing
         * refresh token.
         *
         * @param scope the OAuth scope
         * @param deviceCodeOrRefreshToken the device code (if isDeviceCode is true) or an existing refresh token
         * @param isDeviceCode true if deviceCodeOrRefreshToken is a device code, false if it is a refresh token
         */
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
            return accessToken == null || refreshTime == null || !(System.currentTimeMillis() < refreshTime + TimeUnit.SECONDS.toMillis(expiresIn));
        }

    }

    /**
     * Thrown by {@link #getToken(String)} when no cached refresh token exists for the requested scope. Carries
     * the device code details the user needs to authorize a new one; call {@link #confirm()} once they have.
     */
    public class NoExistingRefreshTokenException extends RuntimeException {

        /**
         * The OAuth scope that was requested.
         */
        private final String scope;

        /**
         * The device code obtained to authorize a new refresh token.
         */
        private final String deviceCode;

        /**
         * The URI the user should visit to authorize the device.
         */
        private final String verificationUri;

        /**
         * The user code the user must enter at the verification URI.
         */
        private final String userCode;

        /**
         * Constructs an instance for the given scope and device code.
         *
         * @param scope the OAuth scope that was requested
         * @param deviceCode the device code obtained to authorize a new refresh token
         */
        public NoExistingRefreshTokenException(String scope, DeviceCode deviceCode) {
            this.deviceCode = deviceCode.getDeviceCode();
            this.verificationUri = deviceCode.getVerificationUri();
            this.scope = scope;
            this.userCode = deviceCode.getUserCode();
        }

        /**
         * Gets the URI the user should visit to authorize the device.
         *
         * @return the verification URI
         */
        public String getVerificationUri() {
            return verificationUri;
        }

        /**
         * Gets the user code the user must enter at the verification URI.
         *
         * @return the user code
         */
        public String getUserCode() {
            return userCode;
        }

        /**
         * Confirms that the user has authorized the device, exchanging the device code for a refreshable token.
         *
         * @return the resulting refreshable token
         */
        public RefreshableToken confirm() {
            return new RefreshableTokenImpl(scope, deviceCode, true);
        }

    }

}