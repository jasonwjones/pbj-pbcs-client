package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.exceptions.PbcsGeneralException;
import com.jasonwjones.pbcs.client.exceptions.PbcsInvalidCredentialsException;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

public class MyResponseErrorHandlerTest {

    private static final URI MEMBER_URL = URI.create("https://epm.example.com/HyperionPlanning/rest/v3/applications/Vision/dimensions/Entity/members/PLIC%20Group%20Life%20LOB");

    private final MyResponseErrorHandler handler = new MyResponseErrorHandler();

    @Test
    public void unauthorizedNamesTheRefusedCallAndAnExpiredToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.WWW_AUTHENTICATE, "Bearer error=\"invalid_token\", error_description=\"Token Expired\"");

        PbcsInvalidCredentialsException e = assertThrows(PbcsInvalidCredentialsException.class,
                () -> handler.handleError(MEMBER_URL, HttpMethod.GET, response(401, "Unauthorized", headers, "")));

        assertThat(e.getMessage(), is("EPM Cloud answered HTTP 401 Unauthorized to GET "
                + "/HyperionPlanning/rest/v3/applications/Vision/dimensions/Entity/members/PLIC Group Life LOB; "
                + "WWW-Authenticate: Bearer error=\"invalid_token\", error_description=\"Token Expired\""));
    }

    @Test
    public void unauthorizedWithNothingElseToSayStillNamesTheCall() {
        PbcsInvalidCredentialsException e = assertThrows(PbcsInvalidCredentialsException.class,
                () -> handler.handleError(MEMBER_URL, HttpMethod.GET, response(401, "Unauthorized", new HttpHeaders(), "")));

        assertThat(e.getMessage(), is("EPM Cloud answered HTTP 401 Unauthorized to GET "
                + "/HyperionPlanning/rest/v3/applications/Vision/dimensions/Entity/members/PLIC Group Life LOB"));
    }

    @Test
    public void unauthorizedCarriesWhatTheResponseSaid() {
        String body = "{\n  \"detail\": \"You are not authorized to perform this operation.\"\n}";

        PbcsInvalidCredentialsException e = assertThrows(PbcsInvalidCredentialsException.class,
                () -> handler.handleError(MEMBER_URL, HttpMethod.GET, response(401, "Unauthorized", new HttpHeaders(), body)));

        assertThat(e.getMessage(), is("EPM Cloud answered HTTP 401 Unauthorized to GET "
                + "/HyperionPlanning/rest/v3/applications/Vision/dimensions/Entity/members/PLIC Group Life LOB; "
                + "response: { \"detail\": \"You are not authorized to perform this operation.\" }"));
    }

    /**
     * What a live pod sent for a bearer token it couldn't use: the challenge has the detail, the body is a stock page.
     */
    @Test
    public void unauthorizedLeavesOutTheGatewaysHtmlPage() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.WWW_AUTHENTICATE, "Bearer error=\"invalid_session\", error_description=\"Header Invalid\"");
        headers.setContentType(MediaType.TEXT_HTML);
        String body = "<html>\n<head><title>401 Authorization Required</title></head>\n<body>\n"
                + "<center><h1>401 Authorization Required</h1></center>\n<hr>\n</body>\n</html>\n";

        PbcsInvalidCredentialsException e = assertThrows(PbcsInvalidCredentialsException.class,
                () -> handler.handleError(MEMBER_URL, HttpMethod.GET, response(401, "Unauthorized", headers, body)));

        assertThat(e.getMessage(), is("EPM Cloud answered HTTP 401 Unauthorized to GET "
                + "/HyperionPlanning/rest/v3/applications/Vision/dimensions/Entity/members/PLIC Group Life LOB; "
                + "WWW-Authenticate: Bearer error=\"invalid_session\", error_description=\"Header Invalid\""));
    }

    @Test
    public void otherErrorsStillDescribeTheCallAndResponse() {
        PbcsGeneralException e = assertThrows(PbcsGeneralException.class,
                () -> handler.handleError(MEMBER_URL, HttpMethod.GET, response(500, "Internal Server Error", new HttpHeaders(), "boom\n  again")));

        assertThat(e.getMessage(), is("HTTP 500 Internal Server Error from GET " + MEMBER_URL + ": boom again"));
    }

    private static ClientHttpResponse response(int status, String statusText, HttpHeaders headers, String body) {
        return new ClientHttpResponse() {
            @Override
            public HttpStatusCode getStatusCode() {
                return HttpStatusCode.valueOf(status);
            }

            @Override
            public String getStatusText() {
                return statusText;
            }

            @Override
            public void close() {
            }

            @Override
            public InputStream getBody() {
                return new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
            }

            @Override
            public HttpHeaders getHeaders() {
                return headers;
            }
        };
    }

}
