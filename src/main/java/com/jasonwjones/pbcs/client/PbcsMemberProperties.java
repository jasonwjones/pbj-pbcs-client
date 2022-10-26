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
	public Integer getObjectType();

	// such as STOREDATA
	// TODO: enum
	// observed values: Never Share, Dynamic Calc, Store Data, Label Only, Dynamic Calc and Store
	public String getDataStorage();

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

}