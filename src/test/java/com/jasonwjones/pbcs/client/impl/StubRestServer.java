package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.PbcsApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Answers each REST endpoint with a canned body and records what was sent, without any network, so a
 * plan type's requests can be checked against the shapes a live pod accepts.
 */
class StubRestServer implements ClientHttpRequestInterceptor {

    private final Map<String, String> responses = new HashMap<>();

    /** The last path segment of each request, in the order they were made. */
    final List<String> endpoints = new ArrayList<>();

    /** The body of each request, in the order they were made. */
    final List<String> bodies = new ArrayList<>();

    /**
     * Answers every request to the given endpoint with the given JSON.
     *
     * @param endpoint the last path segment, such as {@code importdataslice}
     * @param body the JSON to answer with
     */
    void respond(String endpoint, String body) {
        responses.put(endpoint, body);
    }

    /**
     * A plan type on the Vision application whose requests come here. Alias resolution is switched off,
     * since it would ask the server for alias tables; tests using it must use member names.
     *
     * @return the plan type
     */
    PbcsPlanTypeImpl planType() {
        RestTemplate template = new RestTemplate();
        template.getInterceptors().add(this);
        RestContext context = new RestContext(template, "example.test", "https://example.test/HyperionPlanning/rest/v3/", null);
        return new PbcsPlanTypeImpl(context, application(), new PlanTypeConfigurationImpl.Builder("Plan1").build()) {
            @Override
            protected List<String> canonicalMemberNames(List<String> names, boolean includeDefaultTable) {
                return names;
            }
        };
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
        String path = request.getURI().getPath();
        String endpoint = path.substring(path.lastIndexOf('/') + 1);
        endpoints.add(endpoint);
        bodies.add(new String(body, StandardCharsets.UTF_8));
        String response = responses.get(endpoint);
        if (response == null) throw new AssertionError("Unexpected request to " + path);
        return new JsonResponse(response);
    }

    private static PbcsApplication application() {
        return (PbcsApplication) Proxy.newProxyInstance(
                PbcsApplication.class.getClassLoader(),
                new Class<?>[] {PbcsApplication.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getName" -> "Vision";
                    case "getParent" -> null;
                    default -> throw new UnsupportedOperationException(method.getName());
                });
    }

    private static class JsonResponse implements ClientHttpResponse {

        private final byte[] body;

        JsonResponse(String body) {
            this.body = body.getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public HttpStatusCode getStatusCode() {
            return HttpStatus.OK;
        }

        @Override
        public String getStatusText() {
            return "OK";
        }

        @Override
        public void close() {
        }

        @Override
        public InputStream getBody() {
            return new ByteArrayInputStream(body);
        }

        @Override
        public HttpHeaders getHeaders() {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return headers;
        }

    }

}
