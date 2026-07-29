package com.jasonwjones.pbcs.client.sso;

import com.jasonwjones.pbcs.client.sso.entity.AccessTokenResponse;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class IDCSAuthenticationFlowsTest {

    private static final String TOKEN_ENDPOINT = "https://identity.example.test/oauth2/v1/token";

    @Test
    public void authorizationCodeExchangeSendsDocumentedParameters() {
        RecordingRestTemplate restTemplate = new RecordingRestTemplate();
        restTemplate.respondWith(tokenResponse("access-token", null, 3600));
        IDCSAuthCodeFlow flow = new IDCSAuthCodeFlow(
                "client-id", "client-secret", TOKEN_ENDPOINT, restTemplate);

        AccessToken token = flow.getAccessTokenFromAuthCode(
                "authorization-code", "https://client.example.test/oauth/callback");

        assertThat(token.getAccessToken(), is("access-token"));
        assertThat(restTemplate.lastUrl, is(TOKEN_ENDPOINT));
        assertThat(restTemplate.lastParameters.get("grant_type"), is("authorization_code"));
        assertThat(restTemplate.lastParameters.get("code"), is("authorization-code"));
        assertThat(restTemplate.lastParameters.get("redirect_uri"),
                is("https://client.example.test/oauth/callback"));

        String credentials = Base64.getEncoder().encodeToString(
                "client-id:client-secret".getBytes(StandardCharsets.UTF_8));
        assertThat(restTemplate.lastHeaders.getFirst(HttpHeaders.AUTHORIZATION), is("Basic " + credentials));
    }

    @Test
    public void jwtExchangeStoresAndReturnsRefreshableToken() {
        RecordingRestTemplate restTemplate = new RecordingRestTemplate();
        restTemplate.respondWith(tokenResponse("access-token", "refresh-token", 3600));
        RecordingRefreshTokenStorage storage = new RecordingRefreshTokenStorage();
        IDCSJwtFlow flow = new IDCSJwtFlow(
                "client-id", "client-secret", "tenant", storage, TOKEN_ENDPOINT, restTemplate);

        AccessToken token = flow.getToken("signed-user-assertion", "epm-scope offline_access");

        assertThat(token, instanceOf(RefreshableToken.class));
        assertThat(token.getAccessToken(), is("access-token"));
        assertThat(storage.refreshToken, is("refresh-token"));
        assertThat(restTemplate.lastParameters.get("grant_type"),
                is("urn:ietf:params:oauth:grant-type:jwt-bearer"));
        assertThat(restTemplate.lastParameters.get("assertion"), is("signed-user-assertion"));
        assertThat(restTemplate.lastParameters.get("scope"), is("epm-scope offline_access"));
        assertThat(restTemplate.getInterceptors().get(0).getClass().getSimpleName(),
                containsString("BasicAuthenticationInterceptor"));
    }

    @Test
    public void cachedRefreshTokenIsUsedAndPreservedWhenNotRotated() {
        RecordingRestTemplate restTemplate = new RecordingRestTemplate();
        restTemplate.respondWith(tokenResponse("refreshed-access-token", null, 3600));
        RecordingRefreshTokenStorage storage = new RecordingRefreshTokenStorage();
        storage.refreshToken = "existing-refresh-token";
        IDCSJwtFlow flow = new IDCSJwtFlow(
                "client-id", "client-secret", "tenant", storage, TOKEN_ENDPOINT, restTemplate);

        AccessToken token = flow.getToken(null, "epm-scope");

        assertThat(token.getAccessToken(), is("refreshed-access-token"));
        assertThat(storage.refreshToken, is("existing-refresh-token"));
        assertThat(restTemplate.lastParameters.get("grant_type"), is("refresh_token"));
        assertThat(restTemplate.lastParameters.get("refresh_token"), is("existing-refresh-token"));
        assertThat(restTemplate.lastParameters.get("scope"), is("epm-scope"));
    }

    private static AccessTokenResponse tokenResponse(String accessToken, String refreshToken, int expiresIn) {
        AccessTokenResponse response = new AccessTokenResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(expiresIn);
        response.setTokenType("Bearer");
        return response;
    }

    private static class RecordingRestTemplate extends RestTemplateWithUrlEncodedExtensions {

        private final Queue<AccessTokenResponse> responses = new ArrayDeque<>();

        private String lastUrl;

        private HttpHeaders lastHeaders;

        private Map<String, String> lastParameters;

        private void respondWith(AccessTokenResponse response) {
            responses.add(response);
        }

        @Override
        public <T> ResponseEntity<T> postForEntityWithUrlEncodedParams(
                String url, Class<T> responseType, String... params) {
            return record(url, new HttpHeaders(), responseType, params);
        }

        @Override
        public <T> ResponseEntity<T> postForEntityWithUrlEncodedParams(
                String url, HttpHeaders headers, Class<T> responseType, String... params) {
            return record(url, headers, responseType, params);
        }

        private <T> ResponseEntity<T> record(
                String url, HttpHeaders headers, Class<T> responseType, String... params) {
            lastUrl = url;
            lastHeaders = headers;
            lastParameters = new LinkedHashMap<>();
            for (int index = 0; index < params.length; index += 2) {
                lastParameters.put(params[index], params[index + 1]);
            }
            return new ResponseEntity<>(responseType.cast(responses.remove()), HttpStatus.OK);
        }

    }

    private static class RecordingRefreshTokenStorage implements RefreshTokenStorage {

        private String refreshToken;

        @Override
        public void put(String tenant, String clientId, String scope, String refreshToken) {
            this.refreshToken = refreshToken;
        }

        @Override
        public String getRefreshToken(String tenant, String clientId, String scope) {
            return refreshToken;
        }

        @Override
        public boolean clear(String tenant, String clientId, String scope) {
            boolean containedToken = refreshToken != null;
            refreshToken = null;
            return containedToken;
        }

        @Override
        public void clear() {
            refreshToken = null;
        }

    }

}
