package com.jasonwjones.pbcs.client;

import java.util.List;

// TODO: rename to just PbcsMember
public interface PbcsMemberProperties {

	public String getName();

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

	public String getDataType();

	// such as 33
	public Integer getObjectType();

	// such as STOREDATA
	public String getDataStorage();

	// mapped from dimName
	public String getDimensionName();

	// from twoPass
	public boolean isTwoPass();
	
	public List<String> getUsedIn();

	public int getLevel();
			
}
