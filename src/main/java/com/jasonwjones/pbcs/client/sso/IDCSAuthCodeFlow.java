package com.jasonwjones.pbcs.client.sso;

import com.jasonwjones.pbcs.client.exceptions.PbcsInvalidCredentialsException;
import com.jasonwjones.pbcs.client.impl.PbcsConnectionToken;
import com.jasonwjones.pbcs.client.sso.entity.AccessTokenResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Exchanges an Oracle Identity Cloud Service authorization code for an access token.
 *
 * <p>The authorization code must first be obtained through the IDCS authorization endpoint. The redirect URI supplied
 * during the exchange must exactly match the URI used to obtain that code and one configured for the trusted
 * application. For example:</p>
 *
 * <pre>{@code
 * IDCSAuthCodeFlow flow = new IDCSAuthCodeFlow(clientId, clientSecret, tenant);
 * AccessToken token = flow.getAccessTokenFromAuthCode(authorizationCode, redirectUri);
 * PbcsConnectionToken connection = new PbcsConnectionToken(epmServer, token);
 * }</pre>
 *
 * <p>Client credentials and authorization codes are secrets and should be supplied by the calling application's
 * credential/configuration mechanism rather than embedded in source code.</p>
 */
public class IDCSAuthCodeFlow {

    private final String tokenEndpoint;

    private final String authorization;

    private final RestTemplateWithUrlEncodedExtensions restTemplate;

    public IDCSAuthCodeFlow(String clientId, String clientSecret, String tenant) {
        this(clientId, clientSecret,
                "https://idcs-" + Objects.requireNonNull(tenant, "tenant cannot be null")
                        + ".identity.oraclecloud.com/oauth2/v1/token",
                new RestTemplateWithUrlEncodedExtensions());
    }

    IDCSAuthCodeFlow(String clientId, String clientSecret, String tokenEndpoint,
                     RestTemplateWithUrlEncodedExtensions restTemplate) {
        Objects.requireNonNull(clientId, "client ID cannot be null");
        Objects.requireNonNull(clientSecret, "client secret cannot be null");
        this.tokenEndpoint = Objects.requireNonNull(tokenEndpoint, "token endpoint cannot be null");
        this.restTemplate = Objects.requireNonNull(restTemplate, "REST template cannot be null");

        HttpHeaders basicAuth = new HttpHeaders();
        basicAuth.setBasicAuth(clientId, clientSecret, StandardCharsets.UTF_8);
        this.authorization = basicAuth.getFirst(HttpHeaders.AUTHORIZATION);
    }

    /**
     * Exchanges an authorization code without sending a redirect URI.
     *
     * @param authCode authorization code returned by IDCS
     * @return the resulting access token
     * @deprecated IDCS authorization-code exchanges normally require the same redirect URI used when obtaining the code;
     * use {@link #getAccessTokenFromAuthCode(String, String)}.
     */
    @Deprecated
    public AccessToken getAccessTokenFromAuthCode(String authCode) {
        return getAccessTokenFromAuthCode(authCode, null);
    }

    /**
     * Exchanges an authorization code for an IDCS access token.
     *
     * @param authCode authorization code returned by IDCS
     * @param redirectUri redirect URI used to obtain the authorization code
     * @return the resulting access token
     */
    public AccessToken getAccessTokenFromAuthCode(String authCode, String redirectUri) {
        if (!StringUtils.hasText(authCode)) {
            throw new IllegalArgumentException("Authorization code cannot be empty");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set(HttpHeaders.AUTHORIZATION, authorization);

        List<String> params = new ArrayList<>(List.of(
                "grant_type", "authorization_code",
                "code", authCode));
        if (StringUtils.hasText(redirectUri)) {
            params.add("redirect_uri");
            params.add(redirectUri);
        }

        // this endpoint also returns an id_token parameter that is not currently mapped
        ResponseEntity<AccessTokenResponse> response = restTemplate.postForEntityWithUrlEncodedParams(
                tokenEndpoint, headers, AccessTokenResponse.class, params.toArray(String[]::new));
        AccessTokenResponse accessTokenResponse = response.getBody();
        if (response.getStatusCode().is2xxSuccessful()
                && accessTokenResponse != null
                && StringUtils.hasText(accessTokenResponse.getAccessToken())) {
            return new AccessTokenImpl(accessTokenResponse.getAccessToken());
        }
        throw new PbcsInvalidCredentialsException("Getting access token from authorization code failed");
    }

}
