package com.jasonwjones.pbcs.client.exceptions;

/**
 * This exception is thrown when a Hyperion Planning API version is requested
 * but does not exist (in the REST API, a 404 is/should be thrown).
 * 
 * @author jasonwjones
 *
 */
@SuppressWarnings("serial")
public class PbcsNoSuchClientException extends PbcsClientException {

	public PbcsNoSuchClientException(String version) {
		super("There is no API available with version " + version);
	}

}
