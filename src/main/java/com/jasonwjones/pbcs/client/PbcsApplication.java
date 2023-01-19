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

	/**
	 * Gets the client used to connect to this application.
	 *
	 * @return the current client
	 */
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

	/**
	 * Gets the list of job definitions for this application
	 *
	 * @return the list of job definitions
	 */
	List<PbcsJobDefinition> getJobDefinitions();

	/**
	 * Returns the list of job definitions with the given type.
	 *
	 * @param jobType the job type to filter on
	 * @return the list of job definitions of that type for this application
	 */
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

	/**
	 * The INTEGRATION job type is an enhanced version of DATARULE job type (see Running Data Rules). It is recommended that you use the INTEGRATION job type for future integration jobs
	 *
	 * @param integrationName - name of the integration
	 * @param parameters      - parameters for integration
	 * @return job result
	 */
	PbcsJobLaunchResult launchIntegration(String integrationName, Map<String, String> parameters);

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

	/**
	 * Not currently implemented.
	 *
	 * @param jobName the job name
	 * @param exportFileName the export file name
	 */
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
	 * Gets all substitution variables in the application, including variables that are defined for a specific plan.
	 *
	 * @return a set of the substitution variables, an empty set if there are none
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

	/**
	 * Gets the list of dimensions for the entire application.
	 *
	 * @return the dimensions for the application
	 */
	List<PbcsAppDimension> getDimensions();

	/**
	 * Gets a dimension of the application with the given name.
	 *
	 * @param dimensionName the name of the dimension
	 * @return the dimension
	 */
	PbcsDimension getDimension(String dimensionName);

	/**
	 * Gets the dimensions of the given plan type. Generally you will want to use the {@link PbcsPlanType#getDimensions()}
	 * method instead of this.
	 *
	 * @param planType the plan name
	 * @return the list of dimensions
	 */
	List<PbcsDimension> getDimensions(String planType);

	/**
	 * Get the list of cubes/plans in this application.
	 *
	 * @return the list of plans for this application
	 */
	List<PbcsPlanType> getPlanTypes();

	/**
	 * Gets the plan with the given name.
	 *
	 * @param planTypeName the plan name
	 * @return the plan/cube
	 */
	PbcsPlanType getPlanType(String planTypeName);

	/**
	 * Gets the plan with the given name.
	 *
	 * @param planTypeName the plan name
	 * @param skipCheck true if the plan name should be validated
	 * @return the plan
	 * @deprecated use the {@link #getPlanType(PlanTypeConfiguration)} method
	 */
	PbcsPlanType getPlanType(String planTypeName, boolean skipCheck);

	/**
	 * Gets the plan using the given name, skip check value, and explicit dimensions
	 *
	 * @param planTypeName the name of the plan
	 * @param skipCheck true if the plan name should be verified or not
	 * @param dimensions explicit dimension list to initialize with
	 * @return the plan type
	 * @deprecated use the {@link #getPlanType(PlanTypeConfiguration)} method
	 */
	@Deprecated
	PbcsPlanType getPlanType(String planTypeName, boolean skipCheck, List<String> dimensions);

	/**
	 * Gets the plan/cube using the given configuration. This method mostly exists so that we don't have to keep
	 * changing signatures of methods as more options become available.
	 *
	 * @param configuration the configuration to get the plan with
	 * @return the plan
	 */
	PbcsPlanType getPlanType(PlanTypeConfiguration configuration);

	/**
	 * The number of overloads of {@link #getPlanType(String)} started to proliferate, so there is now a generic
	 * configuration object where the plan definition is specified and then passed to the {@link #getPlanType(PlanTypeConfiguration)}
	 * method.
	 */
	interface PlanTypeConfiguration {

		/**
		 * Get name of the plan type (cube).
		 *
		 * @return the name of the plan type
		 */
		String getName();

		/**
		 * Determines if a check (a REST call) should be performed to validate that the given plan name is actually valid
		 * or if we should just assume that it is. You'll get a slightly faster response when creating a {@link PbcsPlanType}
		 * because you'll skip a REST call.
		 *
		 * @return true if the validity check should be skipped, false otherwise.
		 */
		boolean isSkipCheck();

		/**
		 * The list of explicit dimensions that are being set for the plan. It's not required to set dimensions to use
		 * a plan type, but you must set them if you want dimension resolution without a dimension name to work such as
		 * in {@link PbcsPlanType#getMember(String)}.
		 *
		 * @return the list of explicit (known) dimensions for the plan, empty collection if none are being set
		 */
		List<String> getExplicitDimensions();

		/**
		 * Gets the member dimension cache that will be used for the plan type. The default implementation is generally
		 * just a simple {@link com.jasonwjones.pbcs.client.memberdimensioncache.InMemoryMemberDimensionCache} but for
		 * performance or other reasons, the caller may want to specify their own resolver
		 * .
		 * @return the dimension name cache to use for the plan
		 */
		PbcsPlanType.MemberDimensionCache getMemberDimensionCache();

	}

}