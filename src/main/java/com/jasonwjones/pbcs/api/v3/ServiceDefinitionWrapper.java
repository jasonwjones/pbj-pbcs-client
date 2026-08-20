package com.jasonwjones.pbcs.api.v3;

/**
 * A concrete {@link AbstractHypermediaResponse} of {@link String} items, used so Jackson has a non-generic
 * type to deserialize the interop service's services list responses into.
 */
public class ServiceDefinitionWrapper extends AbstractHypermediaResponse<String> {

	/**
	 * Constructs an empty instance for deserialization.
	 */
	public ServiceDefinitionWrapper() {
	}

}
