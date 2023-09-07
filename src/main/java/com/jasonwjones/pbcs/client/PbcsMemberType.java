package com.jasonwjones.pbcs.client;

/**
 * Types are in HSP_OBJECT_TYPE or can be helpfully found at
 * http://www.hyperionepm.com/archives/102
 *
 * @author Jason Jones
 *
 */
public enum PbcsMemberType {

	/**
	 * Unknown isn't a real row in the table -- just incude it in case there's
	 * an object type we don't have a mapping for
	 */
	UNKNOWN(0, "Unknown"), // not a real row in table, just give
	FOLDER(1, "Folder"),
	SCENARIO(31, "Scenario"),
	ACCOUNT(32, "Accont"),
	ENTITY(33, "Entity"),
	TIME_PERIOD(34, "Time Period"),
	VERSION(35, "Version"),
	YEAR(38, "Year");

	private int objectTypeId;

	private String typeName;

	private PbcsMemberType(int objectTypeId, String typeName) {
		this.objectTypeId = objectTypeId;
		this.typeName = typeName;
	}

	public int getObjectTypeId() {
		return objectTypeId;
	}

	public String getTypeName() {
		return typeName;
	}

	public static PbcsMemberType valueOf(int objectTypeId) {
		for (PbcsMemberType id : PbcsMemberType.values()) {
			if (id.getObjectTypeId() == objectTypeId) {
				return id;
			}
		}
		return PbcsMemberType.UNKNOWN;
	}

}