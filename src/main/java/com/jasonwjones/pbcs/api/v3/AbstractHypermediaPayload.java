package com.jasonwjones.pbcs.api.v3;

import java.util.List;

abstract class AbstractHypermediaPayload<E> {

	private List<E> items;	
	
	public List<E> getItems() {
		return items;
	}

	public void setItems(List<E> items) {
		this.items = items;
	}

}
