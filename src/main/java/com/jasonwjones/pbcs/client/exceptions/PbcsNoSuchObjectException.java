package com.jasonwjones.pbcs.client.exceptions;

@SuppressWarnings("serial")
public class PbcsNoSuchObjectException extends PbcsClientException {

	public PbcsNoSuchObjectException(String objectName, String objectType) {
		super("There is no such " + objectType + " with name " + objectName);
	}

}
