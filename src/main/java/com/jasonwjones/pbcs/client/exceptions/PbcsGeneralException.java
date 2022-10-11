package com.jasonwjones.pbcs.client.exceptions;

public class PbcsGeneralException extends PbcsClientException {

	private final PbcsErrorResponse errorResponse;

	public PbcsGeneralException(PbcsErrorResponse errorResponse) {
		super(errorResponse.getDetails());
		this.errorResponse = errorResponse;
	}

	@Override
	public String getMessage() {
		return errorResponse.getMessage();
	}

}