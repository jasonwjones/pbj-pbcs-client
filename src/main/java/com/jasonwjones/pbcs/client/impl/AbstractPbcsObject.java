package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.PbcsObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

public abstract class AbstractPbcsObject implements PbcsObject {

    protected final RestContext context;

    protected AbstractPbcsObject(RestContext context) {
        this.context = context;
    }

    protected final <T> T get(String urlSuffix, Class<T> responseType, Object... uriVariables) {
        return exchange(urlSuffix, HttpMethod.GET, null, responseType, uriVariables);
    }

    protected final <T> T post(String urlSuffix, Object request, Class<T> responseType, Object... uriVariables) {
        return exchange(urlSuffix, HttpMethod.POST, request, responseType, uriVariables);
    }

    protected final <T> T exchange(String urlSuffix, HttpMethod method, Object request, Class<T> responseType, Object... uriVariables) {
        HttpEntity<Object> requestEntity = new HttpEntity<>(request);
        ResponseEntity<T> response = context.getTemplate().exchange(this.context.getBaseUrl() + urlSuffix, method, requestEntity, responseType, uriVariables);
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