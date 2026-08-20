package com.jasonwjones.pbcs.api.v3;

import java.util.List;

/**
 * Base class to extend that provides structure for the typical responses that
 * are a JSON map with a "links" item and additional items specific to that call
 *
 * @author jasonwjones
 * @param <E> type param
 *
 */
public abstract class AbstractHypermediaResponse<E> {

	private List<E> items;

	private List<HypermediaLink> links;

	private String type;

	/**
	 * Constructs an empty instance for deserialization.
	 */
	protected AbstractHypermediaResponse() {
	}

	// these might only be on LCM (interop) stuff, 0 is success, -1 is in
	// progress, "+ve" is failed and has an error... maybe.
	/**
	 * Gets the items in this response.
	 *
	 * @return the items, may be null if none were returned
	 */
	public List<E> getItems() {
		return items;
	}

	/**
	 * Sets the items in this response.
	 *
	 * @param items the items
	 */
	public void setItems(List<E> items) {
		this.items = items;
	}

	/**
	 * Gets the hypermedia links included in this response.
	 *
	 * @return the links, may be null if none were returned
	 */
	public List<HypermediaLink> getLinks() {
		return links;
	}

	/**
	 * Sets the hypermedia links for this response.
	 *
	 * @param links the links
	 */
	public void setLinks(List<HypermediaLink> links) {
		this.links = links;
	}

	/**
	 * Gets the type of this response, as reported by the API.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type of this response.
	 *
	 * @param type the type
	 */
	public void setType(String type) {
		this.type = type;
	}

}