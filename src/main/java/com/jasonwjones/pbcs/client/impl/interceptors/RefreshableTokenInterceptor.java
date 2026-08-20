package com.jasonwjones.pbcs.client.impl.interceptors;

import com.jasonwjones.pbcs.client.PbcsConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.Objects;

/**
 * A {@link ClientHttpRequestInterceptor} that adds an HTTP Bearer {@code Authorization} header built from a
 * token-based {@link PbcsConnection}'s current password (access token) value.
 */
public class RefreshableTokenInterceptor implements ClientHttpRequestInterceptor {

    private final PbcsConnection connection;

    /**
     * Constructs an instance using the token from the given connection.
     *
     * @param connection the token-based connection to source the bearer token from
     * @throws NullPointerException if connection is null
     * @throws IllegalArgumentException if connection is not a token-based connection
     */
    public RefreshableTokenInterceptor(PbcsConnection connection) {
        this.connection = Objects.requireNonNull(connection, "connection is null");
        if (!connection.isToken()) throw new IllegalArgumentException("Connection must be a token");
    }

    @Override
    public @NonNull ClientHttpResponse intercept(HttpRequest request, @NonNull byte[] body, ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().set(HttpHeaders.AUTHORIZATION, "Bearer " + connection.getPassword());
        return execution.execute(request, body);
    }

}