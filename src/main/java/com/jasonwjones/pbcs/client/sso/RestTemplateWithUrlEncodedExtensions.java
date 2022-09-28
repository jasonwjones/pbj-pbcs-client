package com.jasonwjones.pbcs.client.sso;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class RestTemplateWithUrlEncodedExtensions extends RestTemplate {

    public <T> ResponseEntity<T> postForEntityWithUrlEncodedParams(String url, Class<T> responseType, String... params) throws RestClientException {
        if (params.length % 2 != 0) throw new IllegalArgumentException("Must provide even number of parameters");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        for (int i = 0; i < params.length; i += 2) {
            map.add(params[i], params[i + 1]);
        }

        HttpEntity<MultiValueMap<String, String>> entityRequest = new HttpEntity<>(map, headers);
        return postForEntity(url, entityRequest, responseType);
    }

}