package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.PbcsObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.util.StringJoiner;

public abstract class AbstractPbcsObject implements PbcsObject {

    protected final RestContext context;

    protected AbstractPbcsObject(RestContext context) {
        this.context = context;
    }

    protected final <T> T get(String urlSuffix, Class<T> responseType, Object... uriVariables) {
        ResponseEntity<T> response = context.getTemplate().getForEntity(this.context.getBaseUrl() + urlSuffix, responseType, uriVariables);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RestClientException("Unsuccessful call");
        }
    }

    protected final <T> T post(String urlSuffix, Object request, Class<T> responseType, Object... uriVariables) {
        ResponseEntity<T> response = context.getTemplate().postForEntity(this.context.getBaseUrl() + urlSuffix, request, responseType, uriVariables);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RestClientException("Unsuccessful call");
        }
    }

    @Override
    public String toString() {
        return getQualifiedName();
    }

}