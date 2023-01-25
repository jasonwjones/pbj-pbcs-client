package com.jasonwjones.pbcs.client;

/**
 * Represents a dimension in terms of a Data Management dimension (as opposed to a particular
 * instance of a dimension in an application/plan type).
 *
 * @author jasonwjones
 *
 */
public interface PbcsDimension {

	/**
	 * The name of the dimension.
	 *
	 * @return the name of the dimension.
	 */
	String getName();

	/**
	 * Gets the explicit number of the dimension. This is not a PBCS construct; it is an arbitrary value assigned by
	 * the PBCS API to make some downstream things easier. This may change in the future.
	 *
	 * @return the number of the dimension
	 */
	int getNumber();

	/**
	 * Gets a member in the current dimension with the given name.
	 *
	 * @param memberName the member name
	 * @return a member object
	 */
	PbcsMemberProperties getMember(String memberName);

	/**
	 * Gets the root member of the dimension, which is assumed to be a member with the same name as the dimension itself.
	 * In other words, calling this member should be equivalent to calling <code>getMember(getName())</code>, which is
	 * how this method is implemented (and should never return null).
	 *
	 * @return the root/dimension member
	 */
	default PbcsMemberProperties getRoot() {
		return getMember(getName());
	}

}