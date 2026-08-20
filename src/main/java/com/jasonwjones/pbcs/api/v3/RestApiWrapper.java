package com.jasonwjones.pbcs.api.v3;

/**
 * A concrete {@link AbstractHypermediaResponse} of {@link Api} items, used so Jackson has a non-generic type
 * to deserialize the REST API's root/discovery endpoint responses into.
 */
public class RestApiWrapper extends AbstractHypermediaResponse<Api> {

	/**
	 * Constructs an empty instance for deserialization.
	 */
	public RestApiWrapper() {
	}

}
