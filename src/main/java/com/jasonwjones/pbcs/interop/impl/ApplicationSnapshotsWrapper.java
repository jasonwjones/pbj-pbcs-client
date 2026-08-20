package com.jasonwjones.pbcs.interop.impl;

import com.jasonwjones.pbcs.api.v3.AbstractHypermediaResponse;

/**
 * A concrete {@link AbstractHypermediaResponse} of {@link ApplicationSnapshot} items, used so Jackson has a
 * non-generic type to deserialize the interop (LCM) service's snapshot list responses into.
 */
public class ApplicationSnapshotsWrapper extends AbstractHypermediaResponse<ApplicationSnapshot> {

	/**
	 * Constructs an empty instance for deserialization.
	 */
	public ApplicationSnapshotsWrapper() {
	}

}
