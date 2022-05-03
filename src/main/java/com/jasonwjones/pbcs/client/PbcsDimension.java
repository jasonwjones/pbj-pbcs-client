package com.jasonwjones.pbcs.client;

import java.util.Set;

/**
 * Represents a dimension in terms of a Data Management dimension (as opposed to a particular
 * instance of a dimension in an application/plan type).
 *
 * @author jasonwjones
 *
 */
public interface PbcsDimension {

	String getName();

	int getNumber();

	PbcsMemberProperties getMember(String memberName);

	default PbcsMemberProperties getRoot() {
		return getMember(getName());
	}

}