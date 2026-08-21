package com.jasonwjones.pbcs.client.exceptions;

/**
 * Thrown when {@link com.jasonwjones.pbcs.client.PbcsApplication#addMember(String, String, String)} fails because
 * the given parent member is not enabled for dynamic children in the outline. Confirmed against a live EPM Cloud
 * tenant, the REST API returns a 400 with a detail message of the form "Cannot add member &lt;X&gt; because its
 * parent &lt;Y&gt; is not enabled for dynamic children."
 */
@SuppressWarnings("serial")
public class PbcsMemberAddException extends PbcsClientException {

	/**
	 * The name of the member that failed to be added.
	 */
	private final String memberName;

	/**
	 * The name of the parent member, which is not enabled for dynamic children.
	 */
	private final String parentName;

	/**
	 * The dimension the member was being added to.
	 */
	private final String dimensionName;

	/**
	 * Constructs an instance for the given member, parent, and dimension.
	 *
	 * @param memberName the name of the member that failed to be added
	 * @param parentName the name of the parent member, which is not enabled for dynamic children
	 * @param dimensionName the dimension the member was being added to
	 */
	public PbcsMemberAddException(String memberName, String parentName, String dimensionName) {
		super("Cannot add member " + memberName + " to dimension " + dimensionName + " because its parent " + parentName + " is not enabled for dynamic children");
		this.memberName = memberName;
		this.parentName = parentName;
		this.dimensionName = dimensionName;
	}

	/**
	 * Gets the name of the member that failed to be added.
	 *
	 * @return the member name
	 */
	public String getMemberName() {
		return memberName;
	}

	/**
	 * Gets the name of the parent member, which is not enabled for dynamic children.
	 *
	 * @return the parent member name
	 */
	public String getParentName() {
		return parentName;
	}

	/**
	 * Gets the name of the dimension the member was being added to.
	 *
	 * @return the dimension name
	 */
	public String getDimensionName() {
		return dimensionName;
	}

}
