package com.jasonwjones.common;

import com.jasonwjones.di.api.v1.JobDefinition;
import com.jasonwjones.pbcs.api.v3.AbstractHypermediaResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * A minimal REST helper wrapping a {@link RestTemplate} with a base URL, used by the data management
 * (DM/AIF) client.
 */
public class SimpleRestTemplate {

    private final RestTemplate restTemplate;

    private final String baseUrl;

    /**
     * Constructs an instance using the given template and base URL.
     *
     * @param restTemplate the REST template to use for calls
     * @param baseUrl the base URL to prepend to all URL suffixes
     */
    public SimpleRestTemplate(RestTemplate restTemplate, String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    /**
     * Issues a GET request relative to the base URL.
     *
     * @param urlSuffix the URL suffix, relative to the base URL, may contain URI template variables
     * @param responseType the expected response type
     * @param uriVariables the values to substitute into the URL suffix's URI template variables
     * @param <T> the response type
     * @return the response body
     * @throws RuntimeException if the call does not return a 2xx status
     */
    public final <T> T get(String urlSuffix, Class<T> responseType, Object... uriVariables) {
        ResponseEntity<T> response = restTemplate.getForEntity(baseUrl + urlSuffix, responseType, uriVariables);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("Unsuccessful call");
        }
    }

    /**
     * Issues a GET request relative to the base URL, expecting a hypermedia response wrapping a list of items.
     *
     * @param urlSuffix the URL suffix, relative to the base URL, may contain URI template variables
     * @param uriVariables the values to substitute into the URL suffix's URI template variables
     * @param <T> the item type
     * @return the items from the response
     * @throws RuntimeException if the call does not return a 2xx status
     */
    public final <T> List<T> list(String urlSuffix, Object... uriVariables) {
        ResponseEntity<AbstractHypermediaResponse<T>> response = restTemplate.exchange(baseUrl + urlSuffix, HttpMethod.GET, null, new ParameterizedTypeReference<AbstractHypermediaResponse<T>>(){});
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody().getItems();
        } else {
            throw new RuntimeException("Unsuccessful call");
        }
    }

}