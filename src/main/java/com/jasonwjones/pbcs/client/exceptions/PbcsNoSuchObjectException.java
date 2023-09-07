package com.jasonwjones.pbcs.client.exceptions;

import com.jasonwjones.pbcs.client.impl.PbcsObjectType;

/**
 * General exception thrown when an object doesn't exist (but generally should). This will most often be members that
 * don't exist but also applies to applications, plan types, and other items.
 */
public class PbcsNoSuchObjectException extends PbcsClientException {

	private final String objectName;

	public PbcsNoSuchObjectException(String objectName, PbcsObjectType objectType) {
		this(objectName, objectType.name());
	}

	public PbcsNoSuchObjectException(String objectName, String objectType) {
		super("There is no such " + objectType + " with name " + objectName + " (or you do not have access to it)");
		this.objectName = objectName;
	}

	/**
	 * Gets the name of the object that doesn't exist (e.g. the name of a member that was requested but doesn't exist).
	 *
	 * @return the name of the invalid object
	 */
	public String getObjectName() {
		return objectName;
	}

}