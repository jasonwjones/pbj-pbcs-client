package com.jasonwjones.pbcs.client.exceptions;

import com.jasonwjones.pbcs.client.impl.PbcsObjectType;

@SuppressWarnings("serial")
public class PbcsNoSuchObjectException extends PbcsClientException {

	public PbcsNoSuchObjectException(String objectName, PbcsObjectType objectType) {
		this(objectName, objectType.name());
	}

	public PbcsNoSuchObjectException(String objectName, String objectType) {
		super("There is no such " + objectType + " with name " + objectName + " (or you do not have access to it)");
	}

}