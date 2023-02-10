package com.jasonwjones.pbcs.api.v3;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Application {

	// seems to always be EPBCS for PBCS app, or FCCS
	private String appType;

	private boolean dpEnabled;

	// seems to always have the value 'Multidim' (could be different for other modules?)
	private String appStorage;

	// seems to generally be https://www.oracle.com
	private String helpServerUrl;

	// seems to be Oracle's stupid typo
	@JsonProperty("workpaceServerUrl")
	private String workspaceServerUrl;

	// seems to be a lame 'embedded JSON' value, which pops up from time to time in Oracle APIs
	private String webBotDetails;

	private boolean adminMode;

	// probably always true?
	private boolean unicode;

	// such as "Vision"
	private String name;

	// such as "HP"
	private String type;

	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	public boolean isDpEnabled() {
		return dpEnabled;
	}

	public void setDpEnabled(boolean dpEnabled) {
		this.dpEnabled = dpEnabled;
	}

	public String getAppStorage() {
		return appStorage;
	}

	public void setAppStorage(String appStorage) {
		this.appStorage = appStorage;
	}

	public String getHelpServerUrl() {
		return helpServerUrl;
	}

	public void setHelpServerUrl(String helpServerUrl) {
		this.helpServerUrl = helpServerUrl;
	}

	public String getWorkspaceServerUrl() {
		return workspaceServerUrl;
	}

	public void setWorkspaceServerUrl(String workspaceServerUrl) {
		this.workspaceServerUrl = workspaceServerUrl;
	}

	public String getWebBotDetails() {
		return webBotDetails;
	}

	public void setWebBotDetails(String webBotDetails) {
		this.webBotDetails = webBotDetails;
	}

	public boolean isAdminMode() {
		return adminMode;
	}

	public void setAdminMode(boolean adminMode) {
		this.adminMode = adminMode;
	}

	public boolean isUnicode() {
		return unicode;
	}

	public void setUnicode(boolean unicode) {
		this.unicode = unicode;
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

	@Override
	public String toString() {
		return "Application [name=" + name + ", type=" + type + ", dpEnabled=" + dpEnabled + "]";
	}

}