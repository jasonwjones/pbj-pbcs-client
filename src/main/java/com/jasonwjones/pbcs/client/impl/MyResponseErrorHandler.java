package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;
import com.jasonwjones.pbcs.client.exceptions.PbcsInvalidCredentialsException;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MyResponseErrorHandler implements ResponseErrorHandler {

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		// pull the response body and pass separately since the first read of the stream off of
		// ClientHttpResponse would eat it and make subsequent attempts fail
		String responseBody = IOUtils.toString(response.getBody(), StandardCharsets.UTF_8);

		switch (response.getStatusCode().value()) {
			case 404:
				throw new PbcsClientException("Couldn't find endpoint");
			case 400: // Bad Request
				throw PbcsClientException.createException(response, responseBody);
			case 503:
				throw new PbcsClientException("Service currently unavailable; likely in maintenance mode");
			case 401:
				// TODO: there is a little more info coming back on the www-authenticate header that might be useful to
				// show: Bearer error="invalid_token", error_description="Token Expired"
				throw new PbcsInvalidCredentialsException("Unable to login to PBCS due to invalid credentials");
		}
	}

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		return isError(response.getStatusCode());
	}

	private static boolean isError(HttpStatus status) {
		HttpStatus.Series series = status.series();
		return (HttpStatus.Series.CLIENT_ERROR.equals(series)
				|| HttpStatus.Series.SERVER_ERROR.equals(series));
	}

}