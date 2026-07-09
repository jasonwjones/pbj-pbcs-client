package com.jasonwjones.pbcs.client.impl.interceptors;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;

import java.io.IOException;

public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    @NonNull
    public ClientHttpResponse intercept(@NonNull HttpRequest request, @NonNull byte[] body, @NonNull ClientHttpRequestExecution execution) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("Making {} to EPM API {}", request.getMethod(), request.getURI());
            if (body.length > 0) {
                String stringBody = new String(body);
                logger.debug("Request body: {}", stringBody);
            }
        }

        ClientHttpResponse response = execution.execute(request, body);

        if (logger.isDebugEnabled()) {
            String responseBody = new String(IOUtils.toByteArray(response.getBody()));
            logger.debug("Received {} from {} to {}: {}", response.getStatusCode(), request.getMethod(), request.getURI(), responseBody);
        }

        return response;
    }

}