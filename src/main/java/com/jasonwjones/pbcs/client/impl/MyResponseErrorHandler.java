package com.jasonwjones.pbcs.client.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;

public class MyResponseErrorHandler implements ResponseErrorHandler {
	private static final Logger log = LoggerFactory.getLogger(MyResponseErrorHandler.class);

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		log.error("Response error: {} {}", response.getStatusCode(), response.getStatusText());
		if (response.getStatusCode().value() == 404) {
			throw new PbcsClientException("Couldn't find endpoint");	
		} else if (response.getStatusCode().value() == 503) {
			throw new PbcsClientException("Service currently unavailable; likely in maintenance mode");
		}
	}

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		// log.info("Whatever");
		return isError(response.getStatusCode());
		// return false;
	}

	public static boolean isError(HttpStatus status) {
		HttpStatus.Series series = status.series();
		return (HttpStatus.Series.CLIENT_ERROR.equals(series)
				|| HttpStatus.Series.SERVER_ERROR.equals(series));
	}
}
