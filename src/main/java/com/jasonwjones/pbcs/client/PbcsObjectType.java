package com.jasonwjones.pbcs.client;

/**
 * Types are in HSP_OBJECT_TYPE or can be helpfully found at 
 * http://www.hyperionepm.com/archives/102
 * 
 * @author Jason Jones
 *
 */
public enum PbcsObjectType {

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

	private PbcsObjectType(int objectTypeId, String typeName) {
		this.objectTypeId = objectTypeId;
		this.typeName = typeName;
	}

	public int getObjectTypeId() {
		return objectTypeId;
	}

	public String getTypeName() {
		return typeName;
	}
	
	public static PbcsObjectType valueOf(int objectTypeId) {
		for (PbcsObjectType id : PbcsObjectType.values()) {
			if (id.getObjectTypeId() == objectTypeId) {
				return id;
			}
		}
		return PbcsObjectType.UNKNOWN;
	}
	
}
