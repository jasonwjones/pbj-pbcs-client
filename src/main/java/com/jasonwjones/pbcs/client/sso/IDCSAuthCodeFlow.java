package com.jasonwjones.pbcs.client.sso;

import com.jasonwjones.pbcs.client.exceptions.PbcsInvalidCredentialsException;
import com.jasonwjones.pbcs.client.impl.PbcsConnectionToken;
import com.jasonwjones.pbcs.client.sso.entity.AccessTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Base64;

public class IDCSAuthCodeFlow {

    private static final Logger logger = LoggerFactory.getLogger(IDCSAuthCodeFlow.class);

    private final String clientId;

    private final String clientSecret;

    private final String auth;

    private final String tenant;

    private final String tokenEndpoint;

    private final RestTemplateWithUrlEncodedExtensions restTemplate = new RestTemplateWithUrlEncodedExtensions();

    public IDCSAuthCodeFlow(String clientId, String clientSecret, String tenant) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.auth = "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        this.tenant = tenant;
        this.tokenEndpoint = "https://idcs-" + tenant + ".identity.oraclecloud.com/oauth2/v1/token";
    }

    public static void main(String[] args) {
        IDCSAuthCodeFlow authCodeFlow = new IDCSAuthCodeFlow("3aed5e1b786348bc926959032aa556cd", "ed12d3c0-ea24-4196-bf25-cf5dd1a5a940", "42ababaade214afaa94fdcb7ced3d7e3");
        AccessToken accessToken = authCodeFlow.getAccessTokenFromAuthCode("AgAgN2RjNjliZDE0ZjM4NDA1OGJiYjYyZDc0MzBkNDdlOGEIABAg8paYjtGRcHUwE0bWAr9vAAAAQNn18zDgcHRDSePgOxY7AUO6d9IuJ_MlAi1ELrK5WwhF-dISsBCmavc2sThb0z_A6AoZ1x10n8mcM-iKISTMEaQ=");
        System.out.println("AccessToken: " + accessToken);
    }

    public AccessToken getAccessTokenFromAuthCode(String authCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", auth);

        String[] params = {
                "grant_type", "authorization_code",
                "code", authCode
        };

        // this endpoint also returns an id_token parameter that is not currently mapped
        ResponseEntity<AccessTokenResponse> accessTokenResponseResponseEntity = restTemplate.postForEntityWithUrlEncodedParams(tokenEndpoint, headers, AccessTokenResponse.class, params);
        if (accessTokenResponseResponseEntity.getStatusCode().is2xxSuccessful()) {
            AccessTokenResponse accessTokenResponse = accessTokenResponseResponseEntity.getBody();
            return new AccessTokenImpl(accessTokenResponse.getAccessToken());
        }
        throw new PbcsInvalidCredentialsException("Getting access token from authorization code failed");
    }

}