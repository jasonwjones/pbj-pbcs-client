package com.jasonwjones.common;

import com.jasonwjones.di.api.v1.JobDefinition;
import com.jasonwjones.pbcs.api.v3.AbstractHypermediaResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class SimpleRestTemplate {

    private final RestTemplate restTemplate;

    private final String baseUrl;

    public SimpleRestTemplate(RestTemplate restTemplate, String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public final <T> T get(String urlSuffix, Class<T> responseType, Object... uriVariables) {
        ResponseEntity<T> response = restTemplate.getForEntity(baseUrl + urlSuffix, responseType, uriVariables);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("Unsuccessful call");
        }
    }

    public final <T> List<T> list(String urlSuffix, Object... uriVariables) {
        ResponseEntity<AbstractHypermediaResponse<T>> response = restTemplate.exchange(baseUrl + urlSuffix, HttpMethod.GET, null, new ParameterizedTypeReference<AbstractHypermediaResponse<T>>(){});
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody().getItems();
        } else {
            throw new RuntimeException("Unsuccessful call");
        }
    }

}