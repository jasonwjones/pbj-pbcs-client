package com.jasonwjones.pbcs.client;

import java.util.List;

/**
 * The member response from the PBCS REST API contains some properties that have been added over the years are not
 * currently mapped in. Among them seem to be the following:
 *
 * <pre>
 * invalidDueToValidIntersection (boolean)
 * uniqueName: e.g. "[Cash from Current Operations].[NI]"
 * displayPath: e.g. "/Account/CF/Cash Ending Balance/Total Cash Flow/Net Cash from Operations/Cash from Current Operations/[Cash from Current Operations].[NI]",
 * aliasPath: e.g. "/Account/Cash Flow/Cash Ending Balance/Total Cash Flow/Net Cash from Operations/Cash from Current Operations/0000: Net Income",
 * oldName: e.g. "NI"
 * dataType: e.g. "Currency"
 * valid: boolean
 * id: "fd942166-21ae-4b36-b9f8-287164c1940c",
 * path: "/Account/CF/Cash Ending Balance/Total Cash Flow/Net Cash from Operations/Cash from Current Operations/[Cash from Current Operations].[NI]",
 * </pre>
 */
public interface PbcsMemberProperties {

	/**
	 * The name of this member.
	 *
	 * @return the name of the member
	 */
	String getName();

	/**
	 * The alias for this member.
	 *
	 * @return the member alias
	 */
	String getAlias();

	/**
	 * Gets the child of this member, as actual member objects. It is not specified for implementing classes if this is
	 * dynamic, lazy, or something else, so it's possible that successive calls to this method may result in additional
	 * round trips to the server
	 *
	 * @return the child of this member
	 */
	List<? extends PbcsMemberProperties> getChildren();

	/**
	 * Convenience method that checks whether the given member is a leaf node
	 * (level-0) or not. Effectively this should always be true in the case that
	 * <code>getChildren().size() == 0</code>. This should also be true in case
	 * <code>getChildren()</code> returns null, although valid implementations of
	 * this interface should return an empty collection instead of null.
	 *
	 * @return true if this member has no children and is a level-0/leaf node,
	 *         false otherwise.
	 */
	boolean isLeaf();

	/**
	 * Get description (if any) of this member.
	 *
	 * @return the member description
	 */
	String getDescription();

	/**
	 * The name of the parent of this member.
	 *
	 * @return the name of this member's parent
	 */
	String getParentName();

	/**
	 * Data type of the member. Observed values here include "Currency".
	 *
	 * @return the member data type
	 */
	String getDataType();

	// seems to be the object types from the HSP table:
	// https://www.epmmarshall.com/the-planning-repository-hsp_object-and-hsp_object_type/

	// Confirmed values:
	// 31: Scenario
	// 32: Account
	// 33: Entity
	// 34: Time Period (also seen on member such as H-T-D)
	// 35: Version
	// 38: Year
	// 45: Shared Member
	Integer getObjectType();

	/**
	 * Returns the data storage type of the member, such as "Store Data". This method will always return the literal value
	 * that is returned from the REST API, such as "Store Data" or "Never Share". If possible you should use the
	 * newer method {@link #getDataStorageType()} that returns an actual enum.
	 *
	 * @return the data storage type of the member
	 */
	String getDataStorage();

	/**
	 * Returns an enumeration value for the data storage type. If for some reason this can't be mapped, then the value
	 * {@link DataStorage#OTHER} will be returned.
	 *
	 * @return the data storage type enum value
	 */
	DataStorage getDataStorageType();

	/**
	 * Returns the name of the dimension for this member.
	 *
	 * @return the name of the dimension
	 */
	String getDimensionName();

	/**
	 * The "two pass" value of the member.
	 *
	 * @return true if this is a two pass member, false otherwise
	 */
	boolean isTwoPass();

	/**
	 * Return the names of the plans (cubes) that this member is used in.
	 *
	 * @return the list of plans that this member is used in.
	 */
	List<String> getUsedIn();

	/**
	 * Gets the calculated level of the member. The level appears to come back in the member info payload, however, it
	 * always has a value of 0. I believe this is an oversight on Oracle's part.
	 *
	 * @return the calculated level of this member
	 */
	int getLevel();

	/**
	 * Generation of the member. Note: this doesn't seem to be coming back in the JSON response anymore.
	 * TODO: check if this is coming back or needs to be implemented manually
	 * @return the generation of the member (1 for dimension, 2 for child of dimension, and so on). The outline itself
	 * is considered to be "generation 0".
	 */
	int getGeneration();

	/**
	 * Models the possible data storage types. For a member. There isn't actually an "Other" type, it's just included
	 * in case it's not possible to map the actual type for some reason. If for some reason in the future callers have
	 * an issue with getting the actual type, you can rely on the normal {@link #getDataStorage()} method to simply
	 * return the actual string value that was returned from the REST API.
	 *
	 * <p>The "name" value represents the actual JSON payload value for a get member operation.
	 */
	enum DataStorage {

		STORE_DATA("Store Data"),

		DYNAMIC_CALC("Dynamic Calc"),

		LABEL_ONLY("Label Only"),

		DYNAMIC_CALC_AND_STORE("Dynamic Calc and Store"),

		NEVER_SHARE("Never Share"),

		SHARED("Shared"),

		OTHER("Other");

		private final String name;

		DataStorage(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		/**
		 * Returns enumeration value for the given text. At present, the check is case-insensitive, just in case there
		 * is any inconsistency in the Oracle REST API. If the type cannot be mapped for some reason, then the pseudo-type
		 * OTHER will be returned.
		 *
		 * @param text the data storage text such as "Store Data"
		 * @return the corresponding enum value, or OTHER if it couldn't be found
		 */
		public static DataStorage valueOfOrOther(String text) {
			for (DataStorage dataStorage : values()) {
				if (dataStorage.getName().equalsIgnoreCase(text)) {
					return dataStorage;
				}
			}
			return OTHER;
		}

	}

}