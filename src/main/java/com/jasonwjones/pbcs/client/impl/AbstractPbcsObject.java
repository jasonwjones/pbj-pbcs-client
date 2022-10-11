package com.jasonwjones.pbcs.client.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

public abstract class AbstractPbcsObject {

    protected final RestContext context;

    protected AbstractPbcsObject(RestContext context) {
        this.context = context;
    }

    protected <T> T post(String url, Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
        ResponseEntity<T> response = context.getTemplate().postForEntity(url, request, responseType, uriVariables);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("Unsuccessful call");
        }
    }

}