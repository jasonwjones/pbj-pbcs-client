package com.jasonwjones.pbcs.api.v3;

/**
 * A concrete {@link AbstractHypermediaResponse} of {@link PlanTypeDimension} items, used so Jackson has a
 * non-generic type to deserialize the plan-type-scoped dimension list endpoint's responses into.
 */
public class PlanTypeDimensionsWrapper extends AbstractHypermediaResponse<PlanTypeDimension> {

	/**
	 * Constructs an empty instance for deserialization.
	 */
	public PlanTypeDimensionsWrapper() {
	}

}
