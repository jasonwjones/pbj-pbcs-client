package com.jasonwjones.pbcs.client.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasonwjones.pbcs.client.PbcsConnection;
import com.jasonwjones.pbcs.client.sso.AccessToken;
import org.apache.commons.codec.binary.Base64;

import java.util.Objects;

public class PbcsConnectionToken implements PbcsConnection {

    private final String server;

    private final AccessToken accessToken;

    private final String username;

    public PbcsConnectionToken(String server, AccessToken accessToken) {
        this.server = server;
        this.accessToken = Objects.requireNonNull(accessToken, "access token cannot be null");

        String[] tokens = accessToken.getAccessToken().split("\\.");
        if (tokens.length != 3) throw new IllegalArgumentException("Expecting access token with three blocks");

        this.username = readJwt(tokens[1]).getSubject();
    }

    @Override
    public String getServer() {
        return server;
    }

    @Override
    public String getIdentityDomain() {
        return null;
    }

    /**
     * Returns the subject from the JWT token used to build this object.
     *
     * @return the subject (should be the username).
     */
    @Override
    public String getUsername() {
         return username;
    }

    /**
     * Returns the original full JWT token.
     *
     * @return the original full token
     */
    @Override
    public String getPassword() {
        return accessToken.getAccessToken();
    }

    @Override
    public boolean isToken() {
        return true;
    }

    public static SimpleJwtToken readJwt(String jwtToken) {
        try {
            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(Base64.decodeBase64(jwtToken), SimpleJwtToken.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token: " + jwtToken, e);
        }
    }

    public static class SimpleJwtToken {

        @JsonProperty("sub")
        private String subject;

        public String getSubject() {
            return subject;
        }

    }

}