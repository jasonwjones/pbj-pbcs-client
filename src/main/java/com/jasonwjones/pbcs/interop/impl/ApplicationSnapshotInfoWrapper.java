package com.jasonwjones.pbcs.interop.impl;

import com.jasonwjones.pbcs.api.v3.AbstractHypermediaResponse;

/**
 * A concrete {@link AbstractHypermediaResponse} of {@link ApplicationSnapshotInfo} items, used so Jackson
 * has a non-generic type to deserialize the interop (LCM) service's snapshot info list responses into.
 */
public class ApplicationSnapshotInfoWrapper extends AbstractHypermediaResponse<ApplicationSnapshotInfo> {

	/**
	 * Constructs an empty instance for deserialization.
	 */
	public ApplicationSnapshotInfoWrapper() {
	}

}
