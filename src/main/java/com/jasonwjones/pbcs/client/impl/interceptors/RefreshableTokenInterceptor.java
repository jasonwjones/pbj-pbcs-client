package com.jasonwjones.pbcs.client.impl.interceptors;

import com.jasonwjones.pbcs.client.PbcsConnection;
import com.jasonwjones.pbcs.client.sso.RefreshableToken;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class RefreshableTokenInterceptor implements ClientHttpRequestInterceptor {

    private final PbcsConnection connection;

    public RefreshableTokenInterceptor(PbcsConnection connection) {
        this.connection = connection;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] body, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        httpRequest.getHeaders().set(HttpHeaders.AUTHORIZATION, "Bearer " + connection.getUsername());
        return clientHttpRequestExecution.execute(httpRequest, body);
    }

}