package com.jasonwjones.pbcs.interop.impl;

public class ApplicationSnapshotInfo {

	private boolean canDownload;
	
	private boolean canUpload;
	
	private boolean canExport;
	
	private boolean canImport;
	
	private String name;
	
	private String type;

	public boolean isCanDownload() {
		return canDownload;
	}

	public void setCanDownload(boolean canDownload) {
		this.canDownload = canDownload;
	}

	public boolean isCanUpload() {
		return canUpload;
	}

	public void setCanUpload(boolean canUpload) {
		this.canUpload = canUpload;
	}

	public boolean isCanExport() {
		return canExport;
	}

	public void setCanExport(boolean canExport) {
		this.canExport = canExport;
	}

	public boolean isCanImport() {
		return canImport;
	}

	public void setCanImport(boolean canImport) {
		this.canImport = canImport;
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

}
