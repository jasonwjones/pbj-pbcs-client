package com.jasonwjones.pbcs.client.sso;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * A {@link RestTemplate} extension adding convenience methods for POSTing form-urlencoded parameters, used
 * by the IDCS authentication flows.
 */
public class RestTemplateWithUrlEncodedExtensions extends RestTemplate {

    /**
     * Constructs an instance of this template.
     */
    public RestTemplateWithUrlEncodedExtensions() {
    }

    /**
     * POSTs the given name/value parameter pairs as form-urlencoded content.
     *
     * @param url the URL to POST to
     * @param responseType the expected response type
     * @param params alternating parameter names and values; must have an even length
     * @param <T> the response type
     * @return the response entity
     * @throws RestClientException if the call fails
     */
    public <T> ResponseEntity<T> postForEntityWithUrlEncodedParams(String url, Class<T> responseType, String... params) throws RestClientException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return postForEntityWithUrlEncodedParams(url, headers, responseType, params);
    }

    /**
     * POSTs the given name/value parameter pairs as form-urlencoded content, using the given headers.
     *
     * @param url the URL to POST to
     * @param headers the headers to send, in addition to the form-urlencoded content type
     * @param responseType the expected response type
     * @param params alternating parameter names and values; must have an even length
     * @param <T> the response type
     * @return the response entity
     * @throws RestClientException if the call fails
     * @throws IllegalArgumentException if an odd number of parameters is given
     */
    public <T> ResponseEntity<T> postForEntityWithUrlEncodedParams(String url, HttpHeaders headers, Class<T> responseType, String... params) throws RestClientException {
        if (params.length % 2 != 0) throw new IllegalArgumentException("Must provide even number of parameters");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        for (int i = 0; i < params.length; i += 2) {
            map.add(params[i], params[i + 1]);
        }

        HttpEntity<MultiValueMap<String, String>> entityRequest = new HttpEntity<>(map, headers);
        return postForEntity(url, entityRequest, responseType);
    }

}