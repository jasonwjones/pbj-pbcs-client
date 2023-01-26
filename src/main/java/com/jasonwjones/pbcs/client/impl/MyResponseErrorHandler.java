package com.jasonwjones.pbcs.client.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import com.jasonwjones.pbcs.client.exceptions.PbcsInvalidCredentialsException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;

public class MyResponseErrorHandler implements ResponseErrorHandler {

	private static final Logger logger = LoggerFactory.getLogger(MyResponseErrorHandler.class);

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		//logger.error("Response error: {} {}", response.getStatusCode(), response.getStatusText());

		// pull the response body and pass separately since the first read of the stream off of
		// ClientHttpResponse would eat it and make subsequent attempts fail
		String responseBody = IOUtils.toString(response.getBody(), StandardCharsets.UTF_8);

//		logger.error("Headers: {}", response.getHeaders());
//		logger.error("Status: {} ({})", response.getStatusCode().value(), response.getStatusText());
//		logger.error("Body: {}", responseBody);

		if (response.getStatusCode().value() == 404) {
			throw new PbcsClientException("Couldn't find endpoint");
		} else if (response.getStatusCode().value() == 400) { // Bad Request
			// TODO: what if there is an error creating the error?
			throw PbcsClientException.createException(response, responseBody);
		} else if (response.getStatusCode().value() == 503) {
			throw new PbcsClientException("Service currently unavailable; likely in maintenance mode");
		} else if (response.getStatusCode().value() == 401) {
			// TODO: there is a little more info coming back on the www-authenticate header that might be useful to
			// show: Bearer error="invalid_token", error_description="Token Expired"
			throw new PbcsInvalidCredentialsException("Unable to login to PBCS due to invalid credentials");
		}
	}

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		return isError(response.getStatusCode());
	}

	public static boolean isError(HttpStatus status) {
		HttpStatus.Series series = status.series();
		return (HttpStatus.Series.CLIENT_ERROR.equals(series)
				|| HttpStatus.Series.SERVER_ERROR.equals(series));
	}

}