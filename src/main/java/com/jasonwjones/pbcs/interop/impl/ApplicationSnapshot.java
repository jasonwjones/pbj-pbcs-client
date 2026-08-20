package com.jasonwjones.pbcs.interop.impl;

// TODO: rework to interface?
/**
 * Maps a single application snapshot (backup) entry as returned by the interop (LCM) service, such as an
 * LCM export archive or an externally uploaded file.
 */
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

	/**
	 * Constructs an empty instance for deserialization.
	 */
	public ApplicationSnapshot() {
	}

	/**
	 * Gets the last modified time of this snapshot, in epoch milliseconds. Only set for EXTERNAL snapshots.
	 *
	 * @return the last modified time, may be null
	 */
	public Long getLastModifiedTime() {
		return lastModifiedTime;
	}

	/**
	 * Sets the last modified time of this snapshot.
	 *
	 * @param lastModifiedTime the last modified time, in epoch milliseconds
	 */
	public void setLastModifiedTime(Long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	/**
	 * Gets the name of this snapshot.
	 *
	 * @return the snapshot name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this snapshot.
	 *
	 * @param name the snapshot name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the type of this snapshot, either {@code LCM} or {@code EXTERNAL}.
	 *
	 * @return the snapshot type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type of this snapshot.
	 *
	 * @param type the snapshot type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the size of this snapshot, in bytes. Only set for EXTERNAL snapshots.
	 *
	 * @return the size in bytes, may be null
	 */
	public Long getSize() {
		return size;
	}

	/**
	 * Sets the size of this snapshot.
	 *
	 * @param size the size in bytes
	 */
	public void setSize(Long size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return "ApplicationSnapshot [lastModifiedTime=" + lastModifiedTime + ", name=" + name + ", type=" + type
				+ ", size=" + size + "]";
	}

}
