package com.jasonwjones.pbcs.client.exceptions;

import com.jasonwjones.pbcs.client.PbcsObjectType;

/**
 * General exception thrown when an object doesn't exist (but generally should). This will most often be members that
 * don't exist but also applies to applications, plan types, and other items.
 */
public class PbcsNoSuchObjectException extends PbcsClientException {

	/**
	 * The name of the object that doesn't exist.
	 */
	private final String objectName;

	/**
	 * The type of the object that doesn't exist.
	 */
	private final PbcsObjectType objectType;

	/**
	 * Constructs an instance for the given object name and type.
	 *
	 * @param objectName the name of the object that doesn't exist
	 * @param objectType the type of the object that doesn't exist
	 */
	public PbcsNoSuchObjectException(String objectName, PbcsObjectType objectType) {
		super("There is no such " + objectType + " with name " + objectName + " (or you do not have access to it)");
		this.objectName = objectName;
		this.objectType = objectType;
	}

	/**
	 * Gets the name of the object that doesn't exist (e.g. the name of a member that was requested but doesn't exist).
	 *
	 * @return the name of the invalid object
	 */
	public String getObjectName() {
		return objectName;
	}

	/**
	 * Gets the type of the object that was requested but doesn't exist.
	 *
	 * @return the invalid object type
	 */
	public PbcsObjectType getObjectType() {
		return objectType;
	}

}