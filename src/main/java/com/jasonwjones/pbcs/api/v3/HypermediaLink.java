package com.jasonwjones.pbcs.api.v3;

import com.fasterxml.jackson.annotation.JsonProperty;

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

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public String getHyperlink() {
		return hyperlink;
	}

	public void setHyperlink(String hyperlink) {
		this.hyperlink = hyperlink;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "HypermediaLink [relation=" + relation + ", hyperlink=" + hyperlink + ", action=" + action + "]";
	}

}
