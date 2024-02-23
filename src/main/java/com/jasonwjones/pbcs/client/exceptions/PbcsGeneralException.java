package com.jasonwjones.pbcs.client.exceptions;

public class PbcsGeneralException extends PbcsClientException {

	private final PbcsErrorResponse errorResponse;

	public PbcsGeneralException(PbcsErrorResponse errorResponse) {
		super(errorResponse.getDetails() != null ? errorResponse.getDetails() : errorResponse.getMessage());
		this.errorResponse = errorResponse;
	}

	public PbcsErrorResponse getErrorResponse() {
		return errorResponse;
	}

}