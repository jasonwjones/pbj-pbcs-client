package com.jasonwjones.pbcs.api.v3;

/**
 * A concrete {@link AbstractHypermediaResponse} of {@link JobDefinition} items, used so Jackson has a
 * non-generic type to deserialize the REST API's job definitions list responses into.
 */
public class JobDefinitionsWrapper extends AbstractHypermediaResponse<JobDefinition> {

	/**
	 * Constructs an empty instance for deserialization.
	 */
	public JobDefinitionsWrapper() {
	}

}
