package com.jasonwjones.pbcs.client.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

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
		String responseBody = inputStreamToString(response.getBody());
		
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
			throw new PbcsClientException("Unable to login to PBCS due to invalid credentials");
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
	
	private static String inputStreamToString(InputStream inputStream) throws IOException  {
		final int bufferSize = 1024;
		final char[] buffer = new char[bufferSize];
		final StringBuilder out = new StringBuilder();
		Reader in = new InputStreamReader(inputStream, "UTF-8");
		for (; ; ) {
		    int rsz = in.read(buffer, 0, buffer.length);
		    if (rsz < 0)
		        break;
		    out.append(buffer, 0, rsz);
		}
		return out.toString();
	}
	
}
