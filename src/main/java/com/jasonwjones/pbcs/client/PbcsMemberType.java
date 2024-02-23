package com.jasonwjones.pbcs.client;

/**
 * Types are in HSP_OBJECT_TYPE or can be helpfully found at
 * <a href="http://www.hyperionepm.com/archives/102">...</a>
 *
 * @author Jason Jones
 *
 */
public enum PbcsMemberType {

	/**
	 * Unknown isn't a real row in the table -- just include it in case there's
	 * an object type we don't have a mapping for
	 */
	UNKNOWN(0, "Unknown"), // not a real row in table, just give

	SCENARIO(31, "Scenario"),

	ACCOUNT(32, "Account"),

	ENTITY(33, "Entity"),

	TIME_PERIOD(34, "Time Period"),

	VERSION(35, "Version"),

	YEAR(38, "Year"),

	// note that the Currency DIMENSION member itself seems to report a type of 9 while members in it are 37
	CURRENCY(37, "Currency"),

	SHARED(45, "Shared");

	private final int objectTypeId;

	private final String typeName;

	PbcsMemberType(int objectTypeId, String typeName) {
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