package com.jasonwjones.pbcs.client;

import java.util.List;
import java.util.Map;

public interface PbcsApplication {

	/**
	 * Whether the application supports decision packages
	 * 
	 * @return true if DPs are supported, false otherwise
	 */
	public boolean isDpEnabled();

	/**
	 * Get the name of the application
	 * 
	 * @return the name of the application
	 */
	public String getName();

	/**
	 * Gets the product type. Possible values: HFM, HP
	 * 
	 * @return the product type
	 */
	public String getType();

	public List<PbcsJobDefinition> getJobDefinitions();

	public List<PbcsJobDefinition> getJobDefinitions(PbcsJobType jobType);

	/**
	 * Fetches the status of a job with the given ID
	 * 
	 * @param jobId the ID of the job
	 * @return
	 */
	public PbcsJobStatus getJobStatus(Integer jobId);

	/**
	 * Launches a business rule on the application, providing no additional
	 * parameters
	 * 
	 * @param ruleName the name of the business rule exactly as it appears in
	 *            the application
	 */
	public PbcsJobLaunchResult launchBusinessRule(String ruleName);

	/**
	 * Launches a business rule on the application, providing additional
	 * parameters
	 * 
	 * @param ruleName the name of the business rule exactly as it appears in
	 *            the application
	 * @param parameters the parameters to pass along
	 */
	public PbcsJobLaunchResult launchBusinessRule(String ruleName, Map<String, String> parameters);

	public void launchRuleSet(String ruleSetName);
	// also add with params

	// launchPlanTypeMap, +parameters

	public void launchDataImport(String dataImportName);
	// + parameters

	public PbcsJobLaunchResult exportData(String exportName);
	// + parameters

	// importData
	// importData with parameters

	// export metadata
	// + parameters

	public PbcsJobLaunchResult refreshCube();

	/**
	 * Refreshes the cube with the refresh name. If the refresh name listed in
	 * your cube doesn't work, try using 'CubeRefresh'. During testing this
	 * seemed to work even when the refresh name was actually different.
	 * 
	 * @param cubeRefreshName the CUBE_REFRESH name
	 */
	public PbcsJobLaunchResult refreshCube(String cubeRefreshName);

	public PbcsMemberProperties addMember(String dimensionName, String memberName, String parentName);

	public PbcsMemberProperties getMemberProperties(String dimensionName, String memberName);

	// TODO: Planning Units

	// getUserPreferences
}