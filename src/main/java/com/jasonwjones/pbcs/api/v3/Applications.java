package com.jasonwjones.pbcs.api.v3;

import java.util.List;

/**
 * Maps the response of the REST API's applications list endpoint, wrapping the list of {@link Application}
 * entries.
 */
public class Applications {

	// this payload seems to also include a key named 'type' with a value 'HP'. Not sure if this is an oversight or
	// intentional

	private List<Application> items;

	/**
	 * Constructs an empty instance for deserialization.
	 */
	public Applications() {
	}

	/**
	 * Gets the applications in this response.
	 *
	 * @return the applications
	 */
	public List<Application> getItems() {
		return items;
	}

	/**
	 * Sets the applications for this response.
	 *
	 * @param items the applications
	 */
	public void setItems(List<Application> items) {
		this.items = items;
	}

}
