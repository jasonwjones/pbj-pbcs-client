package com.jasonwjones.pbcs.interop.impl;

/**
 * Maps the capabilities and identity of a single application snapshot (backup) as returned by the interop
 * (LCM) service, indicating which operations (download, upload, export, import) are permitted for it.
 */
public class ApplicationSnapshotInfo {

	private boolean canDownload;

	private boolean canUpload;

	private boolean canExport;

	private boolean canImport;

	private String name;

	private String type;

	/**
	 * Constructs an empty instance for deserialization.
	 */
	public ApplicationSnapshotInfo() {
	}

	/**
	 * Whether this snapshot can be downloaded.
	 *
	 * @return true if downloadable, false otherwise
	 */
	public boolean isCanDownload() {
		return canDownload;
	}

	/**
	 * Sets whether this snapshot can be downloaded.
	 *
	 * @param canDownload true if downloadable, false otherwise
	 */
	public void setCanDownload(boolean canDownload) {
		this.canDownload = canDownload;
	}

	/**
	 * Whether this snapshot can be uploaded to.
	 *
	 * @return true if uploadable, false otherwise
	 */
	public boolean isCanUpload() {
		return canUpload;
	}

	/**
	 * Sets whether this snapshot can be uploaded to.
	 *
	 * @param canUpload true if uploadable, false otherwise
	 */
	public void setCanUpload(boolean canUpload) {
		this.canUpload = canUpload;
	}

	/**
	 * Whether this snapshot can be exported (i.e., an LCM export can be run into it).
	 *
	 * @return true if exportable, false otherwise
	 */
	public boolean isCanExport() {
		return canExport;
	}

	/**
	 * Sets whether this snapshot can be exported.
	 *
	 * @param canExport true if exportable, false otherwise
	 */
	public void setCanExport(boolean canExport) {
		this.canExport = canExport;
	}

	/**
	 * Whether this snapshot can be imported (i.e., an LCM import can be run from it).
	 *
	 * @return true if importable, false otherwise
	 */
	public boolean isCanImport() {
		return canImport;
	}

	/**
	 * Sets whether this snapshot can be imported.
	 *
	 * @param canImport true if importable, false otherwise
	 */
	public void setCanImport(boolean canImport) {
		this.canImport = canImport;
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
	 * Gets the type of this snapshot.
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

}
