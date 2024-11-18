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

public class RefreshableTokenInterceptor implements ClientHttpRequestInterceptor {

    private final PbcsConnection connection;

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