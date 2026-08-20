package com.jasonwjones.pbcs.client.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasonwjones.pbcs.client.PbcsConnection;
import com.jasonwjones.pbcs.client.sso.AccessToken;
import org.apache.commons.codec.binary.Base64;

import java.util.Objects;

/**
 * A {@link PbcsConnection} backed by an OAuth/OIDC {@link AccessToken} rather than a username and password.
 * The username is derived from the token's JWT subject claim.
 */
public class PbcsConnectionToken implements PbcsConnection {

    private final String server;

    private final AccessToken accessToken;

    private final String username;

    /**
     * Constructs an instance for the given server and access token.
     *
     * @param server the server, not including http and not containing anything after the TLD
     * @param accessToken the access token to authenticate with; must be a JWT with three dot-separated parts
     * @throws NullPointerException if accessToken is null
     * @throws IllegalArgumentException if the access token is not a three-part JWT
     */
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

    /**
     * Decodes and parses the payload segment of a JWT, extracting just the subject claim.
     *
     * @param jwtToken the base64url-encoded JWT payload segment
     * @return the parsed token
     * @throws IllegalArgumentException if the segment cannot be decoded/parsed
     */
    public static SimpleJwtToken readJwt(String jwtToken) {
        try {
            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(Base64.decodeBase64(jwtToken), SimpleJwtToken.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token: " + jwtToken, e);
        }
    }

    /**
     * A minimal mapping of a JWT payload, capturing only the subject claim.
     */
    public static class SimpleJwtToken {

        @JsonProperty("sub")
        private String subject;

        /**
         * Constructs an empty instance for deserialization.
         */
        public SimpleJwtToken() {
        }

        /**
         * Gets the subject claim.
         *
         * @return the subject
         */
        public String getSubject() {
            return subject;
        }

    }

}