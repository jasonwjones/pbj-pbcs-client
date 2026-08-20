package com.jasonwjones.pbcs.client.exceptions;

/**
 * Thrown when the PBCS environment is currently unavailable, such as when it is in maintenance mode.
 */
@SuppressWarnings("serial")
public class PbcsServiceUnavailableException extends PbcsClientException {

	/**
	 * Constructs an instance with the given message.
	 *
	 * @param message the detail message
	 */
	public PbcsServiceUnavailableException(String message) {
		super(message);
	}

}
