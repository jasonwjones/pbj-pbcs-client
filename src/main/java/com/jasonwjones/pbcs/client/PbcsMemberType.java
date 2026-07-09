package com.jasonwjones.pbcs.client;

/**
 * Types are in <code>HSP_OBJECT_TYPE</code>. One such decoder can be found at <a href="https://www.epmmarshall.com/the-planning-repository-hsp_object-and-hsp_object_type/">Brian's page</a>.
 *
 * @author Jason Jones
 */
public enum PbcsMemberType {

	/**
	 * Unknown isn't a real row in the table, we just include it in case there's an object type for which we don't have
     * a mapping.
	 */
	UNKNOWN(0, "Unknown"), // not a real row in table, just give

	ATTRIBUTE(30, "Attribute"),

	SCENARIO(31, "Scenario"),

	ACCOUNT(32, "Account"),

	ENTITY(33, "Entity"),

	TIME_PERIOD(34, "Time Period"),

	VERSION(35, "Version"),

	YEAR(38, "Year"),

	// note that the Currency DIMENSION member itself seems to report a type of 9 while members in it are 37
	CURRENCY(37, "Currency"),

	/**
	 * Represents a shared member.
	 */
	SHARED(45, "Shared"),

	/**
	 * No specific dimension type. For example, the Product dimension would fall into this category.
	 * The full legacy text description (i.e., from <code>HSP_OBJECT</code> text is <code>User Defined Dimension Member</code>)
	 * but for slight consistency it has been trimmed here.
	 */
	USER_DEFINED(50, "User Defined");

	private final int objectTypeId;

	private final String typeName;

	PbcsMemberType(int objectTypeId, String typeName) {
		this.objectTypeId = objectTypeId;
		this.typeName = typeName;
	}

	/**
	 * Gets the internal object type ID. This more or less corresponds to the legacy object
	 * type ID from the <code>HSP_OBJECT</code> table in the Planning repository. This
	 * information is ostensibly included for posterity, it shouldn't be used for anything.
	 *
	 * @return the internal object type ID.
	 */
	public int getObjectTypeId() {
		return objectTypeId;
	}

	/**
	 * Returns a friendly description of th member/object type name, which is essentially the
	 * ordinal name but in title case and spaces instead of underscores.
	 *
	 * @return the type description
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * Parses an object type ID into an enum member. If the type cannot be determined, then this
	 * method will return {@link #UNKNOWN}, which isn't a real type, but is included so that some
	 * heretofore unknown type name won't throw an exception.
	 *
	 * @param objectTypeId the numeric type value, such as from a get member info response payload
	 * @return the type that corresponds to that numeric type, {@link #UNKNOWN} if it's unknown.
	 */
	public static PbcsMemberType valueOf(int objectTypeId) {
		for (PbcsMemberType id : PbcsMemberType.values()) {
			if (id.getObjectTypeId() == objectTypeId) {
				return id;
			}
		}
		return PbcsMemberType.UNKNOWN;
	}

}