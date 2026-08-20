package com.jasonwjones.pbcs.api.v3;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Maps a single HATEOAS-style hypermedia link, as returned in the {@code links} array of many REST API
 * responses.
 */
public class HypermediaLink {

	@JsonProperty("rel")
	private String relation;

	@JsonProperty("href")
	private String hyperlink;

	/**
	 * The HTTP verb, such as GET, POST, etc.
	 */
	private String action;

	private Object data;

	/**
	 * Constructs an empty instance for deserialization.
	 */
	public HypermediaLink() {
	}

	/**
	 * Gets the relation of this link, describing its purpose relative to the containing resource.
	 *
	 * @return the relation
	 */
	public String getRelation() {
		return relation;
	}

	/**
	 * Sets the relation of this link.
	 *
	 * @param relation the relation
	 */
	public void setRelation(String relation) {
		this.relation = relation;
	}

	/**
	 * Gets the target URL of this link.
	 *
	 * @return the hyperlink URL
	 */
	public String getHyperlink() {
		return hyperlink;
	}

	/**
	 * Sets the target URL of this link.
	 *
	 * @param hyperlink the hyperlink URL
	 */
	public void setHyperlink(String hyperlink) {
		this.hyperlink = hyperlink;
	}

	/**
	 * Gets the HTTP verb to use with this link, such as GET or POST.
	 *
	 * @return the HTTP verb
	 */
	public String getAction() {
		return action;
	}

	/**
	 * Sets the HTTP verb to use with this link.
	 *
	 * @param action the HTTP verb
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * Gets any additional data payload associated with this link.
	 *
	 * @return the data payload, may be null
	 */
	public Object getData() {
		return data;
	}

	/**
	 * Sets the additional data payload associated with this link.
	 *
	 * @param data the data payload
	 */
	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "HypermediaLink [relation=" + relation + ", hyperlink=" + hyperlink + ", action=" + action + "]";
	}

}
