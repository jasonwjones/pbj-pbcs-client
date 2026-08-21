package com.jasonwjones.pbcs.api.v3;

/**
 * Represents the request payload for the Add Member REST API, which adds a new member under an existing
 * parent that has been enabled for dynamic children.
 */
public class MemberAdd {

	private String memberName;

	private String parentName;

	/**
	 * Constructs an instance for adding the given member under the given parent.
	 *
	 * @param memberName the name of the member to add
	 * @param parentName the name of the parent member to add it under
	 */
	public MemberAdd(String memberName, String parentName) {
		this.memberName = memberName;
		this.parentName = parentName;
	}

	/**
	 * Gets the name of the member to add.
	 *
	 * @return the member name
	 */
	public String getMemberName() {
		return memberName;
	}

	/**
	 * Sets the name of the member to add.
	 *
	 * @param memberName the member name
	 */
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	/**
	 * Gets the name of the parent member to add it under.
	 *
	 * @return the parent member name
	 */
	public String getParentName() {
		return parentName;
	}

	/**
	 * Sets the name of the parent member to add it under.
	 *
	 * @param parentName the parent member name
	 */
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

}
