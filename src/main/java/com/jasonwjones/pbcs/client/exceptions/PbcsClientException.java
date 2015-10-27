package com.jasonwjones.pbcs.client.exceptions;

/**
 * Base class from which all PBCS Client exception should be derived from.
 * 
 * @author jasonwjones
 *
 */
@SuppressWarnings("serial")
public class PbcsClientException extends RuntimeException {

	public PbcsClientException(String message) {
		super(message);
	}

}
