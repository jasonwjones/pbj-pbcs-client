package com.jasonwjones.pbcs.client.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;
import com.jasonwjones.pbcs.client.exceptions.PbcsGeneralException;
import com.jasonwjones.pbcs.client.exceptions.PbcsInvalidCredentialsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * Translates non-2xx PBCS REST API responses into the appropriate {@link PbcsClientException} subtype.
 */
public class MyResponseErrorHandler implements ResponseErrorHandler {

	private static final Logger logger = LoggerFactory.getLogger(MyResponseErrorHandler.class);

	private static final int MAX_RESPONSE_BODY_SNIPPET_LENGTH = 1000;

	/**
	 * The name of the response header PBCS uses to indicate which action was being performed.
	 */
	public static final String X_EPM_ACTION_HEADER = "X-EPM_ACTION";

	/**
	 * The {@link #X_EPM_ACTION_HEADER} value indicating an export data slice call.
	 */
	public static final String ACTION_EXPORT_DATA_SLICE = "Export Data Slice";

	/**
	 * Constructs an instance of this error handler.
	 */
	public MyResponseErrorHandler() {
	}

	// added because some exceptions seem to have 'detail' property, and JsonAlias isn't available yet (need Jackson 2.9+)
	private static final ObjectMapper mapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	@Override
	public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
		// pull the response body and pass separately since the first read of the stream off of
		// ClientHttpResponse would eat it and make subsequent attempts fail
		String responseBody = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
		int statusCode = response.getStatusCode().value();
		String statusText = response.getStatusText();

		PbcsClientException.PbcsErrorResponse errorResponse = null;
		try {
			errorResponse = mapper.readValue(responseBody, PbcsClientException.PbcsErrorResponse.class);
		} catch (IOException e) {
			logger.debug("Unable to process error response as JSON; preserving raw HTTP response details");
		}

		switch (statusCode) {
			case 404:
				throw new PbcsClientException("Endpoint not found: " + url.getPath());
			case 503:
				throw new PbcsClientException("Service currently unavailable; likely in maintenance mode");
			case 401:
				throw new PbcsInvalidCredentialsException(unauthorizedMessage(url, method, response.getHeaders(), responseBody));
			case 400: // Bad Request
			default: {
				if (errorResponse != null) {
					throw new PbcsGeneralException(errorResponse);
				}

				String responseBodySnippet = snippet(responseBody);
				String message = "HTTP " + statusCode + " " + statusText
						+ " from " + method + " " + url;
				if (!responseBodySnippet.isEmpty()) {
					message += ": " + responseBodySnippet;
				}
				throw new PbcsGeneralException(message);
			}
		}
	}

	/**
	 * Describes a 401 by the call that was refused and whatever EPM Cloud said about it. A 401 is not always bad
	 * credentials: an expired token is refused the same way, and says so only in its WWW-Authenticate challenge
	 * ({@code Bearer error="invalid_token", error_description="Token Expired"}). A fixed "invalid credentials"
	 * message hid that, and which call it was.
	 *
	 * @param url the URL of the refused call, may be null
	 * @param method the HTTP method of the refused call
	 * @param headers the response headers
	 * @param responseBody the response body, may be empty
	 * @return the message
	 */
	static String unauthorizedMessage(URI url, HttpMethod method, HttpHeaders headers, String responseBody) {
		StringBuilder message = new StringBuilder("EPM Cloud answered HTTP 401 Unauthorized to ")
				.append(method).append(' ').append(url == null ? "(unknown URL)" : url.getPath());
		String challenge = headers.getFirst(HttpHeaders.WWW_AUTHENTICATE);
		if (challenge != null && !challenge.isBlank()) {
			message.append("; ").append(HttpHeaders.WWW_AUTHENTICATE).append(": ").append(challenge.trim());
		}
		// the gateway's own 401 is a stock HTML page that says nothing the status doesn't
		boolean html = headers.getContentType() != null && headers.getContentType().isCompatibleWith(MediaType.TEXT_HTML);
		String responseBodySnippet = snippet(responseBody);
		if (!html && !responseBodySnippet.isEmpty()) {
			message.append("; response: ").append(responseBodySnippet);
		}
		return message.toString();
	}

	private static String snippet(String responseBody) {
		String snippet = responseBody.replaceAll("\\s+", " ").trim();
		if (snippet.length() > MAX_RESPONSE_BODY_SNIPPET_LENGTH) {
			snippet = snippet.substring(0, MAX_RESPONSE_BODY_SNIPPET_LENGTH) + "...";
		}
		return snippet;
	}

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		return response.getStatusCode().is4xxClientError() ||
				response.getStatusCode().is5xxServerError();
	}

}