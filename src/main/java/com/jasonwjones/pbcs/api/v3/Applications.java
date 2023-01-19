package com.jasonwjones.pbcs.api.v3;

import java.util.List;

public class Applications {

	// this payload seems to also include a key named 'type' with a value 'HP'. Not sure if this is an oversight or
	// intentional

	private List<Application> items;

	public List<Application> getItems() {
		return items;
	}

	public void setItems(List<Application> items) {
		this.items = items;
	}

}