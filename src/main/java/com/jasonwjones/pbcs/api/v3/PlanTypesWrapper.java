package com.jasonwjones.pbcs.api.v3;

/**
 * A concrete {@link AbstractHypermediaResponse} of {@link PlanTypeEntry} items, used so Jackson has a
 * non-generic type to deserialize the plan type list endpoint's responses into.
 */
public class PlanTypesWrapper extends AbstractHypermediaResponse<PlanTypeEntry> {

	/**
	 * Constructs an empty instance for deserialization.
	 */
	public PlanTypesWrapper() {
	}

}
