package com.jasonwjones.pbcs.api.v3;

import java.util.List;

/**
 * Base class for request payloads that are simply a JSON list of items.
 *
 * @param <E> the item type
 */
abstract class AbstractHypermediaPayload<E> {

	private List<E> items;

	/**
	 * Gets the items in this payload.
	 *
	 * @return the items
	 */
	public List<E> getItems() {
		return items;
	}

	/**
	 * Sets the items in this payload.
	 *
	 * @param items the items
	 */
	public void setItems(List<E> items) {
		this.items = items;
	}

}
