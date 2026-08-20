package com.jasonwjones.pbcs.client.exceptions;

/**
 * A general-purpose exception thrown when the REST API returns an error that doesn't warrant a more specific
 * exception type.
 */
public class PbcsGeneralException extends PbcsClientException {

	/**
	 * Constructs an instance from a deserialized error response.
	 *
	 * @param errorResponse the error response, may be null
	 */
	public PbcsGeneralException(PbcsErrorResponse errorResponse) {
		super(errorResponse == null ? "No message" : errorResponse.getDetails() != null ? errorResponse.getDetails() : errorResponse.getMessage());
	}

	/**
	 * Constructs an instance with the given message.
	 *
	 * @param message the detail message
	 */
	public PbcsGeneralException(String message) {
		super(message);
	}

}
