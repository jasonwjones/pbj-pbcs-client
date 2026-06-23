package com.jasonwjones.pbcs.client.exceptions;

public class PbcsGeneralException extends PbcsClientException {

	public PbcsGeneralException(PbcsErrorResponse errorResponse) {
		super(errorResponse == null ? "No message" : errorResponse.getDetails() != null ? errorResponse.getDetails() : errorResponse.getMessage());
	}

	public PbcsGeneralException(String message) {
		super(message);
	}

}
