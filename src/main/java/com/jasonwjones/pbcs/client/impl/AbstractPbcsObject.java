package com.jasonwjones.pbcs.client.impl;

import org.springframework.http.ResponseEntity;

public abstract class AbstractPbcsObject {

    protected final RestContext context;

    protected AbstractPbcsObject(RestContext context) {
        this.context = context;
    }

    protected final <T> ResponseEntity<T> getForEntity(String urlSuffix, Class<T> responseType, Object... uriVariables) {
        return context.getTemplate().getForEntity(this.context.getBaseUrl()  + urlSuffix, responseType, uriVariables);
    }

    protected final <T> T get(String urlSuffix, Class<T> responseType, Object... uriVariables) {
        ResponseEntity<T> response = context.getTemplate().getForEntity(this.context.getBaseUrl()  + urlSuffix, responseType, uriVariables);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("Unsuccessful call");
        }
    }

    protected final <T> T post(String urlSuffix, Object request, Class<T> responseType, Object... uriVariables) {
        ResponseEntity<T> response = context.getTemplate().postForEntity(this.context.getBaseUrl()  + urlSuffix, request, responseType, uriVariables);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("Unsuccessful call");
        }
    }

}