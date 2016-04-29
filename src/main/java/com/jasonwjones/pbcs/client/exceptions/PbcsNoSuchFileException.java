package com.jasonwjones.pbcs.client.exceptions;

@SuppressWarnings("serial")
public class PbcsNoSuchFileException extends PbcsClientException {

	public PbcsNoSuchFileException(String filename) {
		super(filename);
	}

}
