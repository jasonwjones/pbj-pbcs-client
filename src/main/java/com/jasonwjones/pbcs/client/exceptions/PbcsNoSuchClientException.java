package com.jasonwjones.pbcs.client.exceptions;

@SuppressWarnings("serial")
public class PbcsNoSuchClientException extends PbcsClientException {

	public PbcsNoSuchClientException(String version) {
		super("There is no API available with version " + version);
	}

}
