package com.jasonwjones.pbcs.api.v3;

/**
 * A concrete {@link AbstractHypermediaResponse} of {@link SubstitutionVariable} items, used so Jackson has a
 * non-generic type to deserialize the REST API's substitution variables list responses into.
 */
public class SubstitutionVariablesWrapper extends AbstractHypermediaResponse<SubstitutionVariable> {

	/**
	 * Constructs an empty instance for deserialization.
	 */
	public SubstitutionVariablesWrapper() {
	}

}
