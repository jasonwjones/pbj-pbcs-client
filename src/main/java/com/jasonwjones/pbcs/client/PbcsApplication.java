package com.jasonwjones.pbcs.client;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.jasonwjones.pbcs.api.v3.SubstitutionVariable;
import com.jasonwjones.pbcs.api.v3.dataslices.DataSlice;
import com.jasonwjones.pbcs.api.v3.dataslices.ExportDataSlice;

/**
 * Models a PBCS application such as a Planning or an HFM application.
 *
 * @author jasonwjones
 *
 */
public interface PbcsApplication {

	PbcsPlanningClient getClient();

	/**
	 * Whether the application supports decision packages
	 *
	 * @return true if DPs are supported, false otherwise
	 */
	boolean isDpEnabled();

	/**
	 * Get the name of the application
	 *
	 * @return the name of the application
	 */
	String getName();

	/**
	 * Gets the product type. Possible values: HFM, HP
	 *
	 * @return the product type
	 */
	String getType();

	List<PbcsJobDefinition> getJobDefinitions();

	List<PbcsJobDefinition> getJobDefinitions(PbcsJobType jobType);

	/**
	 * Fetches the status of a job with the given ID
	 *
	 * @param jobId the ID of the job
	 * @return a job status
	 */
	PbcsJobStatus getJobStatus(Integer jobId);

	/**
	 * Launches a business rule on the application, providing no additional
	 * parameters
	 *
	 * @param ruleName the name of the business rule exactly as it appears in
	 *            the application
	 * @return a job launch result
	 */
	PbcsJobLaunchResult launchBusinessRule(String ruleName);

	/**
	 * Launches a business rule on the application, providing additional
	 * parameters
	 *
	 * @param ruleName the name of the business rule exactly as it appears in
	 *            the application
	 * @param parameters the parameters to pass along
	 * @return a job launch result
	 */
	PbcsJobLaunchResult launchBusinessRule(String ruleName, Map<String, String> parameters);

	PbcsJobLaunchResult launchRuleSet(String ruleSetName, Map<String, String> parameters);
	PbcsJobLaunchResult launchRuleSet(String ruleSetName);

	PbcsJobLaunchResult launchDataImport(String dataImportName, Optional<String> dataFile);
	PbcsJobLaunchResult launchDataImport(String dataImportName);

	PbcsJobLaunchResult importMetadata(String metadataImportName, String dataFile);

	PbcsJobLaunchResult launchDataRule(String dataRuleName, Map<String, String> parameters);

	PbcsJobLaunchResult importMetadata(String metadataImportName);

	PbcsJobLaunchResult exportData(String exportName);

	PbcsJobLaunchResult refreshCube();

	/**
	 * Refreshes the cube with the refresh name. If the refresh name listed in
	 * your cube doesn't work, try using 'CubeRefresh'. During testing this
	 * seemed to work even when the refresh name was actually different.
	 *
	 * @param cubeRefreshName the CUBE_REFRESH name
	 * @return a job launch result
	 */
	PbcsJobLaunchResult refreshCube(String cubeRefreshName);

	PbcsMemberProperties addMember(String dimensionName, String memberName, String parentName);

	PbcsMemberProperties getMember(String dimensionName, String memberName);

	void getUserPreferences();

	// /**
	// * Not implemented (stubbed out for future implementation). Also needed:
	// * Planning Units
	// */
	// TODO: Plan Type Map, copy data from ASO/BSO or vice versa
	// params:
	// {"jobType":"PLAN_TYPE_MAP","jobName":"MapReporting","parameters":{"cubeLinkName":"name","clearData":true}}
	// public void launchPlanTypeMap();
	// TODO: Launch Metadata Import
	// TODO: Export Metadata

	void exportMetadata(String jobName, String exportFileName);

	/**
	 * Exports a data slice from the cube.
	 *
	 * @param planType the plan type to export from
	 * @param dataSlice the export data slice definition
	 * @return a data slice object (pov, headers, rows) of the results
	 */
	DataSlice exportDataSlice(String planType, ExportDataSlice dataSlice);

	/**
	 * Gets all substitution variables in the application
	 *
	 * @return a list of the substitution variables, an empty list if there are
	 *         none
	 */
	Set<SubstitutionVariable> getSubstitutionVariables();

	/**
	 * Fetch a substitution variable with a particular name from this
	 * application
	 *
	 * @param name the name of the variable to fetch
	 * @return the variable object, if it exists
	 */
	SubstitutionVariable getSubstitutionVariable(String name);

	/**
	 * Update a set of substitution variables. This does not replace all the
	 * variables in the application, it just updates the ones that have been
	 * specified in the collection (contrary to what the REST API docs seem to
	 * imply)
	 *
	 * @param variables the variables to update
	 */
	void updateSubstitutionVariables(Collection<SubstitutionVariable> variables);

	/**
	 * Convenience method to update a single substitution variable value.
	 *
	 * @param name the name of the variable
	 * @param value the value of the variable
	 */
	void updateSubstitutionVariable(String name, String value);

	List<PbcsAppDimension> getDimensions();

	PbcsDimension getDimension(String dimensionName);

	List<PbcsDimension> getDimensions(String planType);

	List<PbcsPlanType> getPlanTypes();

	PbcsPlanType getPlanType(String planTypeName);

	PbcsPlanType getPlanType(String planTypeName, boolean skipCheck);

	PbcsPlanType getPlanType(String planTypeName, boolean skipCheck, List<String> dimensions);

}