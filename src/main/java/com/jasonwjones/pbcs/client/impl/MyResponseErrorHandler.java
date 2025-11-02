package com.jasonwjones.pbcs.client.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;
import com.jasonwjones.pbcs.client.exceptions.PbcsGeneralException;
import com.jasonwjones.pbcs.client.exceptions.PbcsInvalidCredentialsException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class MyResponseErrorHandler implements ResponseErrorHandler {

	private static final Logger logger = LoggerFactory.getLogger(MyResponseErrorHandler.class);

	public static final String X_EPM_ACTION_HEADER = "X-EPM_ACTION";

	public static final String ACTION_EXPORT_DATA_SLICE = "Export Data Slice";

	// added because some exceptions seem to have 'detail' property, and JsonAlias isn't available yet (need Jackson 2.9+)
	private static final ObjectMapper mapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	@Override
	public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
		// pull the response body and pass separately since the first read of the stream off of
		// ClientHttpResponse would eat it and make subsequent attempts fail
		String responseBody = IOUtils.toString(response.getBody(), StandardCharsets.UTF_8);

		PbcsClientException.PbcsErrorResponse errorResponse = null;
		try {
			 errorResponse = mapper.readValue(responseBody, PbcsClientException.PbcsErrorResponse.class);
		} catch (IOException e) {
			logger.warn("Unable to process error response: {}", e.getMessage());
		}

		switch (response.getStatusCode().value()) {
			case 404:
				throw new PbcsClientException("Endpoint not found: " + url.getPath());
			case 503:
				throw new PbcsClientException("Service currently unavailable; likely in maintenance mode");
			case 401:
				// TODO: there is a little more info coming back on the www-authenticate header that might be useful to
				// show: Bearer error="invalid_token", error_description="Token Expired"
				throw new PbcsInvalidCredentialsException("Unable to login to PBCS due to invalid credentials");
			case 400: // Bad Request
			default:
				throw new PbcsGeneralException(errorResponse);
		}
	}

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() ||
                response.getStatusCode().is5xxServerError();
	}

}