package com.jasonwjones.pbcs.api.v3;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Maps a single application entry as returned by the REST API's applications endpoint, such as the
 * application's name, type, storage mode, and various server/configuration details.
 */
public class Application {

	// seems to always be EPBCS for PBCS app, or FCCS
	private String appType;

	private boolean dpEnabled;

	// seems to always have the value 'Multidim' (could be different for other modules?)
	private String appStorage;

	// seems to generally be https://www.oracle.com
	private String helpServerUrl;

	// seems to be Oracle's stupid typo
	// example value: https://appliedolapepm-test-appliedolapepm.epm.us-phoenix-1.ocs.oraclecloud.com:443
	// e.g: https://<SERVICE_NAME>-<TENANT_NAME>.<SERVICE_TYPE>.<dcX>.oraclecloud.com
	@JsonProperty("workpaceServerUrl")
	private String workspaceServerUrl;

	// seems to be a lame 'embedded JSON' value, which pops up from time to time in Oracle APIs
	private String webBotDetails;

	// per the docs:
	// Indicates if the application's login level is set to Administrators. Returns a Boolean value where true indicates
	// that the login level for the application is set to Administrators and false indicates that the login level is set
	// to All Users.
	private boolean adminMode;

	// probably always true?
	private boolean unicode;

	// such as "Vision"
	private String name;

	// such as "HP"
	private String type;

	/**
	 * Constructs an empty instance for deserialization.
	 */
	public Application() {
	}

	/**
	 * Gets the application type, e.g. "EPBCS" or "FCCS".
	 *
	 * @return the application type
	 */
	public String getAppType() {
		return appType;
	}

	/**
	 * Sets the application type.
	 *
	 * @param appType the application type
	 */
	public void setAppType(String appType) {
		this.appType = appType;
	}

	/**
	 * Whether Data Preparation is enabled for this application.
	 *
	 * @return true if Data Preparation is enabled, false otherwise
	 */
	public boolean isDpEnabled() {
		return dpEnabled;
	}

	/**
	 * Sets whether Data Preparation is enabled for this application.
	 *
	 * @param dpEnabled true if Data Preparation is enabled, false otherwise
	 */
	public void setDpEnabled(boolean dpEnabled) {
		this.dpEnabled = dpEnabled;
	}

	/**
	 * Gets the application's storage mode, typically "Multidim".
	 *
	 * @return the application storage mode
	 */
	public String getAppStorage() {
		return appStorage;
	}

	/**
	 * Sets the application's storage mode.
	 *
	 * @param appStorage the application storage mode
	 */
	public void setAppStorage(String appStorage) {
		this.appStorage = appStorage;
	}

	/**
	 * Gets the URL of the help server associated with this application.
	 *
	 * @return the help server URL
	 */
	public String getHelpServerUrl() {
		return helpServerUrl;
	}

	/**
	 * Sets the URL of the help server associated with this application.
	 *
	 * @param helpServerUrl the help server URL
	 */
	public void setHelpServerUrl(String helpServerUrl) {
		this.helpServerUrl = helpServerUrl;
	}

	/**
	 * Gets the workspace server URL for this application.
	 *
	 * @return the workspace server URL
	 */
	public String getWorkspaceServerUrl() {
		return workspaceServerUrl;
	}

	/**
	 * Sets the workspace server URL for this application.
	 *
	 * @param workspaceServerUrl the workspace server URL
	 */
	public void setWorkspaceServerUrl(String workspaceServerUrl) {
		this.workspaceServerUrl = workspaceServerUrl;
	}

	/**
	 * Gets the raw, embedded "web bot details" value for this application, as returned by the API.
	 *
	 * @return the web bot details
	 */
	public String getWebBotDetails() {
		return webBotDetails;
	}

	/**
	 * Sets the raw "web bot details" value for this application.
	 *
	 * @param webBotDetails the web bot details
	 */
	public void setWebBotDetails(String webBotDetails) {
		this.webBotDetails = webBotDetails;
	}

	/**
	 * Whether this application's login level is set to Administrators (as opposed to All Users).
	 *
	 * @return true if the login level is Administrators, false if it is All Users
	 */
	public boolean isAdminMode() {
		return adminMode;
	}

	/**
	 * Sets whether this application's login level is set to Administrators.
	 *
	 * @param adminMode true for Administrators, false for All Users
	 */
	public void setAdminMode(boolean adminMode) {
		this.adminMode = adminMode;
	}

	/**
	 * Whether this application is Unicode-enabled.
	 *
	 * @return true if Unicode-enabled, false otherwise
	 */
	public boolean isUnicode() {
		return unicode;
	}

	/**
	 * Sets whether this application is Unicode-enabled.
	 *
	 * @param unicode true if Unicode-enabled, false otherwise
	 */
	public void setUnicode(boolean unicode) {
		this.unicode = unicode;
	}

	/**
	 * Gets the name of the application, e.g. "Vision".
	 *
	 * @return the application name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the application.
	 *
	 * @param name the application name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the type of the application, e.g. "HP".
	 *
	 * @return the application type code
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type of the application.
	 *
	 * @param type the application type code
	 */
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Application [name=" + name + ", type=" + type + ", dpEnabled=" + dpEnabled + "]";
	}

}
