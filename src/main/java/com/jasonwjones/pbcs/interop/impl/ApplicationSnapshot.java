package com.jasonwjones.pbcs.interop.impl;

// TODO: rework to interface?
public class ApplicationSnapshot {

	/**
	 * Only set if EXTERNAL
	 */
	private Long lastModifiedTime;

	private String name;

	/**
	 * Possible values: LCM, EXTERNAL
	 */
	private String type;

	/**
	 * Only set if EXTERNAL
	 */
	private Long size;

	public Long getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(Long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

}
