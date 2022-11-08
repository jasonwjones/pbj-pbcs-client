package com.jasonwjones.pbcs.client;

import java.util.List;

// TODO: rename to just PbcsMember
// TODO: compare payload vs properties here: "{"dataStorage":"Store Data","dimName":"Scenario","usedIn":["Plan1","Vision"],"invalidDueToValidIntersection":false,"objectType":31,"twoPass":false,"displayPath":"/Scenario/Actual","generation":2,"aliasPath":"/Scenario/Actual","level":0,"oldName":"Actual","dataType":"Unspecified","valid":true,"parentName":"Scenario","name":"Actual","id":"f5aa5599-da40-4224-9ee8-4d77503261a8","path":"/Scenario/Actual","links":[{"rel":"self","href":"https://appliedolapepm-test-appliedolapepm.epm.us-phoenix-1.ocs.oraclecloud.com:443/HyperionPlanning/rest/v3/applications/Vision/dimensions/Scenario/members/Actual","action":"GET"}]}[\r][\n]"
public interface PbcsMemberProperties {

	public String getName();

	public String getAlias();

	public List<? extends PbcsMemberProperties> getChildren();

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
	public boolean isLeaf();

	public String getDescription();

	public String getParentName();

	// Unspecified, ...?
	public String getDataType();

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
	public Integer getObjectType();

	// such as STOREDATA
	// TODO: enum
	// observed values: Never Share, Dynamic Calc, Store Data, Label Only, Dynamic Calc and Store
	// Shared
	public String getDataStorage();

	DataStorage getDataStorageType();

	// mapped from dimName
	public String getDimensionName();

	// from twoPass
	public boolean isTwoPass();

	public List<String> getUsedIn();

	/**
	 * Gets the calculated level of the member. The level appears to come back in the member info payload, however, it
	 * always has a value of 0. I believe this is an oversight on Oracle's part.
	 *
	 * @return the calculated level of this member
	 */
	int getLevel();

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