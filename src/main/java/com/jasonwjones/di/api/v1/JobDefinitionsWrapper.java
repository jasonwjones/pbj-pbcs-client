package com.jasonwjones.di.api.v1;

import com.jasonwjones.pbcs.api.v3.AbstractHypermediaResponse;

/**
 * A concrete {@link AbstractHypermediaResponse} of {@link JobDefinition} items, used so Jackson has a
 * non-generic type to deserialize the data management (DM/AIF) jobs list responses into.
 */
public class JobDefinitionsWrapper extends AbstractHypermediaResponse<JobDefinition> {

	/**
	 * Constructs an empty instance for deserialization.
	 */
	public JobDefinitionsWrapper() {
	}

}
