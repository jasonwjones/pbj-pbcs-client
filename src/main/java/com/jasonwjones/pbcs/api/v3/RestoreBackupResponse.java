package com.jasonwjones.pbcs.api.v3;

import java.util.List;

public class RestoreBackupResponse {

	private List<String> items;
	private List<HypermediaLink> links;
	private String details;
	private String status;

	public List<String> getItems() {
		return items;
	}

	public void setItems(List<String> items) {
		this.items = items;
	}

	public List<HypermediaLink> getLinks() {
		return links;
	}

	public void setLinks(List<HypermediaLink> links) {
		this.links = links;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
