package com.jasonwjones.pbcs.client.sso;

import com.jasonwjones.pbcs.client.sso.entity.AccessTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Exchanges a signed user JWT assertion for an Oracle Identity Cloud Service access token.
 *
 * <p>The caller is responsible for creating and signing the short-lived assertion using a key registered with IDCS.
 * The requested scope must be authorized for the client. Include {@code offline_access} in the configured/requested
 * scopes when a refresh token is required.</p>
 *
 * <pre>{@code
 * IDCSJwtFlow flow = new IDCSJwtFlow(clientId, clientSecret, tenant, refreshTokenStorage);
 * AccessToken token = flow.getToken(signedUserAssertion, epmScope);
 * PbcsConnectionToken connection = new PbcsConnectionToken(epmServer, token);
 * }</pre>
 *
 * <p>Client credentials, assertions, access tokens, and refresh tokens must not be embedded in source code or logged.</p>
 */
public class IDCSJwtFlow {

    private static final Logger logger = LoggerFactory.getLogger(IDCSJwtFlow.class);

    private static final String JWT_BEARER_GRANT = "urn:ietf:params:oauth:grant-type:jwt-bearer";

    private final String clientId;

    private final String tenant;

    private final RestTemplateWithUrlEncodedExtensions restTemplate;

    private final RefreshTokenStorage refreshTokenStorage;

    private final String tokenEndpoint;

    public IDCSJwtFlow(String clientId, String clientSecret, String tenant) {
        this(clientId, clientSecret, tenant, new SimpleRefreshTokenStorage());
    }

    public IDCSJwtFlow(String clientId, String clientSecret, String tenant, RefreshTokenStorage refreshTokenStorage) {
        this(clientId, clientSecret, tenant, refreshTokenStorage,
                "https://idcs-" + Objects.requireNonNull(tenant, "tenant cannot be null")
                        + ".identity.oraclecloud.com/oauth2/v1/token",
                new RestTemplateWithUrlEncodedExtensions());
    }

    IDCSJwtFlow(String clientId, String clientSecret, String tenant, RefreshTokenStorage refreshTokenStorage,
                String tokenEndpoint, RestTemplateWithUrlEncodedExtensions restTemplate) {
        this.clientId = Objects.requireNonNull(clientId, "client ID cannot be null");
        Objects.requireNonNull(clientSecret, "client secret cannot be null");
        this.tenant = Objects.requireNonNull(tenant, "tenant cannot be null");
        this.refreshTokenStorage = Objects.requireNonNull(refreshTokenStorage, "refresh token storage cannot be null");
        this.tokenEndpoint = Objects.requireNonNull(tokenEndpoint, "token endpoint cannot be null");
        this.restTemplate = Objects.requireNonNull(restTemplate, "REST template cannot be null");
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(clientId, clientSecret));
    }

    /**
     * Gets an access token using a cached refresh token when available, otherwise exchanges the supplied JWT assertion.
     *
     * @param assertion signed user JWT assertion
     * @param scope space-delimited IDCS scope value
     * @return an access token, refreshable when IDCS returns or a cache contains a refresh token
     */
    public AccessToken getToken(String assertion, String scope) {
        if (!StringUtils.hasText(scope)) {
            throw new IllegalArgumentException("Scope cannot be empty");
        }

        String existingRefreshToken = refreshTokenStorage.getRefreshToken(tenant, clientId, scope);
        if (StringUtils.hasText(existingRefreshToken)) {
            return new RefreshableTokenImpl(scope, existingRefreshToken);
        }
        if (!StringUtils.hasText(assertion)) {
            throw new IllegalArgumentException("JWT assertion cannot be empty when no refresh token is cached");
        }

        AccessTokenResponse response;
        try {
            response = requestToken(
                    "grant_type", JWT_BEARER_GRANT,
                    "assertion", assertion,
                    "scope", scope);
        } catch (HttpClientErrorException e) {
            throw new IDCSException("Unable to exchange JWT assertion for an IDCS token", e);
        }
        if (StringUtils.hasText(response.getRefreshToken())) {
            return new RefreshableTokenImpl(scope, response);
        }
        return new AccessTokenImpl(response.getAccessToken());
    }

    private AccessTokenResponse requestToken(String... params) {
        try {
            ResponseEntity<AccessTokenResponse> response =
                    restTemplate.postForEntityWithUrlEncodedParams(tokenEndpoint, AccessTokenResponse.class, params);
            AccessTokenResponse body = response.getBody();
            if (!response.getStatusCode().is2xxSuccessful()
                    || body == null
                    || !StringUtils.hasText(body.getAccessToken())) {
                throw new IDCSException("IDCS returned an invalid token response", null);
            }
            return body;
        } catch (IDCSException e) {
            throw e;
        } catch (HttpClientErrorException e) {
            throw e;
        } catch (RestClientException e) {
            throw new IDCSException("Unable to obtain IDCS token", e);
        }
    }

    public class RefreshableTokenImpl implements RefreshableToken {

        private final String scope;

        private Long refreshTime;

        private String accessToken;

        private String refreshToken;

        private Integer expiresIn;

        private RefreshableTokenImpl(String scope, String refreshToken) {
            this.scope = scope;
            this.refreshToken = refreshToken;
        }

        private RefreshableTokenImpl(String scope, AccessTokenResponse response) {
            this.scope = scope;
            updateFromTokenResponse(response);
        }

        private void updateFromTokenResponse(AccessTokenResponse accessTokenResponse) {
            accessToken = accessTokenResponse.getAccessToken();
            if (StringUtils.hasText(accessTokenResponse.getRefreshToken())) {
                refreshToken = accessTokenResponse.getRefreshToken();
            }
            expiresIn = accessTokenResponse.getExpiresIn();
            refreshTime = System.currentTimeMillis();
            if (StringUtils.hasText(refreshToken)) {
                refreshTokenStorage.put(tenant, clientId, scope, refreshToken);
            }
        }

        @Override
        public String getAccessToken() {
            if (isExpired()) refresh();
            return accessToken;
        }

        @Override
        public void refresh() {
            if (!StringUtils.hasText(refreshToken)) {
                throw new IDCSException("No refresh token is available", null);
            }
            try {
                AccessTokenResponse response = requestToken(
                        "grant_type", "refresh_token",
                        "refresh_token", refreshToken,
                        "scope", scope);
                updateFromTokenResponse(response);
            } catch (HttpClientErrorException e) {
                if (refreshTokenStorage.clear(tenant, clientId, scope)) {
                    logger.info("Cleared cached refresh token");
                }
                throw new IDCSException("Unable to refresh token", e);
            }
        }

        @Override
        public boolean isExpired() {
            if (accessToken == null || refreshTime == null) {
                return true;
            }
            return expiresIn != null
                    && System.currentTimeMillis() >= refreshTime + TimeUnit.SECONDS.toMillis(expiresIn);
        }

    }

}
