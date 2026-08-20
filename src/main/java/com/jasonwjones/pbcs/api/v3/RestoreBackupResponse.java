package com.jasonwjones.pbcs.api.v3;

import java.util.List;

/**
 * Maps the response for a restore backup request (or a subsequent job status poll), as returned by the
 * interop service.
 */
public class RestoreBackupResponse {

	private List<String> items;
	private List<HypermediaLink> links;
	private String details;
	private String status;

	/**
	 * Constructs an empty instance for deserialization.
	 */
	public RestoreBackupResponse() {
	}

	/**
	 * Gets any items returned with the response.
	 *
	 * @return the items
	 */
	public List<String> getItems() {
		return items;
	}

	/**
	 * Sets the items for this response.
	 *
	 * @param items the items
	 */
	public void setItems(List<String> items) {
		this.items = items;
	}

	/**
	 * Gets the hypermedia links included in this response, such as a job status link.
	 *
	 * @return the links
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
	 * Gets any additional details for this response.
	 *
	 * @return the details
	 */
	public String getDetails() {
		return details;
	}

	/**
	 * Sets additional details for this response.
	 *
	 * @param details the details
	 */
	public void setDetails(String details) {
		this.details = details;
	}

	/**
	 * Gets the status of the restore job. A value of {@code -1} indicates the job is still in progress.
	 *
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the status of the restore job.
	 *
	 * @param status the status
	 */
	public void setStatus(String status) {
		this.status = status;
	}
}
