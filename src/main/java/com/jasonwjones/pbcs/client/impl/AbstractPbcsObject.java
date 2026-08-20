package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.PbcsObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

/**
 * Base class for PBCS object implementations that make REST calls, providing shared helpers for issuing
 * GET/POST requests relative to the object's REST context.
 */
public abstract class AbstractPbcsObject implements PbcsObject {

    /**
     * The REST context (template and base URL) used to make calls for this object.
     */
    protected final RestContext context;

    /**
     * Constructs an instance using the given REST context.
     *
     * @param context the REST context to use for calls made by this object
     */
    protected AbstractPbcsObject(RestContext context) {
        this.context = context;
    }

    /**
     * Issues a GET request relative to this object's base URL.
     *
     * @param urlSuffix the URL suffix, relative to the base URL, may contain URI template variables
     * @param responseType the expected response type
     * @param uriVariables the values to substitute into the URL suffix's URI template variables
     * @param <T> the response type
     * @return the response body
     */
    protected final <T> T get(String urlSuffix, Class<T> responseType, Object... uriVariables) {
        return exchange(urlSuffix, HttpMethod.GET, null, responseType, uriVariables);
    }

    /**
     * Issues a POST request relative to this object's base URL.
     *
     * @param urlSuffix the URL suffix, relative to the base URL, may contain URI template variables
     * @param request the request body
     * @param responseType the expected response type
     * @param uriVariables the values to substitute into the URL suffix's URI template variables
     * @param <T> the response type
     * @return the response body
     */
    protected final <T> T post(String urlSuffix, Object request, Class<T> responseType, Object... uriVariables) {
        return exchange(urlSuffix, HttpMethod.POST, request, responseType, uriVariables);
    }

    /**
     * Issues a request relative to this object's base URL using the given HTTP method.
     *
     * @param urlSuffix the URL suffix, relative to the base URL, may contain URI template variables
     * @param method the HTTP method to use
     * @param request the request body, may be null
     * @param responseType the expected response type
     * @param uriVariables the values to substitute into the URL suffix's URI template variables
     * @param <T> the response type
     * @return the response body
     * @throws org.springframework.web.client.RestClientException if the call does not return a 2xx status
     */
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