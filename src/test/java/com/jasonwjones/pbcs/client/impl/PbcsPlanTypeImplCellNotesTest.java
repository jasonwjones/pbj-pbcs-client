package com.jasonwjones.pbcs.client.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import org.junit.Test;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

/**
 * Cell note reads and writes against canned responses, in the shapes a live pod returned.
 */
public class PbcsPlanTypeImplCellNotesTest {

    private static final List<String> CELL = Arrays.asList("Actual", "FY23", "Final", "USD", "000", "P_000", "Mar", "4110");

    // What a live pod answers a notes-only import with: nothing accepted, nothing updated.
    private static final String IMPORT_RESPONSE = "{\"numAcceptedCells\":0,\"numRejectedCells\":0,\"rejectedCells\":[],\"rejectedCellsWithDetails\":[],\"numUpdatedCells\":0}";

    private static final String EXPORT_WITH_NOTES = "{\"pov\":[\"Actual\",\"FY23\",\"Final\",\"USD\",\"000\",\"P_000\"],\"columns\":[[\"Mar\"]],"
            + "\"rows\":[{\"headers\":[\"4110\"],\"data\":[\"\"],\"cellNotes\":[[{\"contents\":\"first\"},{\"contents\":\"second\"}]]}]}";

    private static final String EXPORT_WITHOUT_NOTES = "{\"pov\":[\"Actual\",\"FY23\",\"Final\",\"USD\",\"000\",\"P_000\"],\"columns\":[[\"Mar\"]],"
            + "\"rows\":[{\"headers\":[\"4110\"],\"data\":[\"\"]}]}";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final StubServer server = new StubServer();

    @Test
    public void setCellNotesOverwritesWithABlankValueAndReadsTheCellBack() throws Exception {
        server.respond("importdataslice", IMPORT_RESPONSE);
        server.respond("exportdataslice", EXPORT_WITH_NOTES);

        List<String> notes = planType().setCellNotes(CELL, Arrays.asList("first", "second"));

        assertThat(notes, contains("first", "second"));
        assertThat(server.endpoints, contains("importdataslice", "exportdataslice"));

        JsonNode importRequest = objectMapper.readTree(server.bodies.get(0));
        assertThat(importRequest.get("cellNotesOption").asText(), is("Overwrite"));
        JsonNode row = importRequest.get("dataGrid").get("rows").get(0);
        assertThat("a blank value leaves the cell's value alone", row.get("data").toString(), is("[\"\"]"));
        assertThat(row.get("cellNotes").toString(), is("[[{\"contents\":\"first\"},{\"contents\":\"second\"}]]"));

        JsonNode exportRequest = objectMapper.readTree(server.bodies.get(1));
        assertThat(exportRequest.get("exportPlanningData").asBoolean(), is(true));
    }

    @Test
    public void appendIsSentAsAppend() throws Exception {
        server.respond("importdataslice", IMPORT_RESPONSE);
        server.respond("exportdataslice", EXPORT_WITH_NOTES);

        planType().setCellNotes(CELL, List.of("second"), PbcsPlanType.CellNotesOption.APPEND);

        assertThat(objectMapper.readTree(server.bodies.get(0)).get("cellNotesOption").asText(), is("Append"));
    }

    @Test
    public void skipIsRefusedBeforeAnythingIsSent() {
        try {
            planType().setCellNotes(CELL, List.of("ignored"), PbcsPlanType.CellNotesOption.SKIP);
            throw new AssertionError("expected an IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertThat(server.endpoints, is(empty()));
        }
    }

    @Test
    public void aCellWithoutNotesReadsAsEmpty() {
        server.respond("exportdataslice", EXPORT_WITHOUT_NOTES);

        assertThat(planType().getCellNotes(CELL), is(empty()));
    }

    @Test
    public void aCellTheExportLeavesOutReadsAsEmpty() {
        server.respond("exportdataslice", "{\"pov\":[\"Actual\",\"FY23\",\"Final\",\"USD\",\"000\",\"P_000\"],\"columns\":[[\"Mar\"]],\"rows\":[]}");

        assertThat(planType().getCellNotes(CELL), is(empty()));
    }

    private PbcsPlanTypeImpl planType() {
        RestTemplate template = new RestTemplate();
        template.getInterceptors().add(server);
        RestContext context = new RestContext(template, "example.test", "https://example.test/HyperionPlanning/rest/v3/", null);
        return new PbcsPlanTypeImpl(context, application(), new PlanTypeConfigurationImpl.Builder("Plan1").build()) {
            // Alias resolution would ask the server for alias tables; these tests use member names only.
            @Override
            protected List<String> canonicalMemberNames(List<String> names, boolean includeDefaultTable) {
                return names;
            }
        };
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

    /** Answers each endpoint with a canned body and records what was sent, without any network. */
    private static class StubServer implements ClientHttpRequestInterceptor {

        private final Map<String, String> responses = new HashMap<>();

        private final List<String> endpoints = new ArrayList<>();

        private final List<String> bodies = new ArrayList<>();

        void respond(String endpoint, String body) {
            responses.put(endpoint, body);
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
