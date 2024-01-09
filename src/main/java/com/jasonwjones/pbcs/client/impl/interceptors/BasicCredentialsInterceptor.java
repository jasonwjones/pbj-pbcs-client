package com.jasonwjones.pbcs.client.impl.interceptors;

import com.jasonwjones.pbcs.client.PbcsConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Base64;

public class BasicCredentialsInterceptor implements ClientHttpRequestInterceptor {

    private final PbcsConnection connection;

    public BasicCredentialsInterceptor(PbcsConnection connection) {
        this.connection = connection;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        // in the PBCS gen 1 architecture, the username was the identity name plus a period plus the username. In the
        // gen 2 architecture, it's just the username. Clients should specify null or a blank string in order to cause
        // the gen 2 handling to be used
        final String fullUsername = StringUtils.hasText(connection.getIdentityDomain()) ? connection.getIdentityDomain() + "." + connection.getUsername() : connection.getUsername() + ":" + connection.getPassword();
        final String auth = Base64.getEncoder().encodeToString(fullUsername.getBytes());
        request.getHeaders().set(HttpHeaders.AUTHORIZATION, "Basic " + auth);
        return execution.execute(request, body);
   }

}