package com.jasonwjones.pbcs.api.v3;

import java.util.List;

/**
 * Base class to extend that provides structure for the typical repsonses that are a JSON
 * map with a "links" item and additional items specific to that call
 * @author jasonwjones
 *
 */
public abstract class AbstractHypermediaResponse<E> {
	
	private List<E> items;
	
	private List<HypermediaLink> links;
	
	private String type;
	
	public List<E> getItems() {
		return items;
	}

	public void setItems(List<E> items) {
		this.items = items;
	}

	public List<HypermediaLink> getLinks() {
		return links;
	}

	public void setLinks(List<HypermediaLink> links) {
		this.links = links;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
