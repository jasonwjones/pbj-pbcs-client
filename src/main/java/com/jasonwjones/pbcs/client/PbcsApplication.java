package com.jasonwjones.pbcs.client;

import java.util.Collection;
import java.util.List;
import java.util.Map;
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
public interface PbcsApplication extends PbcsObject {

	/**
	 * Gets the client used to connect to this application.
	 *
	 * @return the current client
	 */
	PbcsPlanningClient getClient();

	@Override
	PbcsPlanningClient getParent();

	/**
	 * Whether the application supports decision packages. I really have no idea what
	 * these are and if they are used.
	 *
	 * @return true if DPs are supported, false otherwise
	 */
	boolean isDpEnabled();

	/**
	 * Gets the product type. Possible values: HFM, HP.
	 *
	 * @return the product type
	 */
	String getType();

	/**
	 * Gets the app type, which is essentially whether it's a Planning or FCCS application.
	 *
	 * @return the app type
	 */
	PbcsAppType getAppType();

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
	 * Despite the name, this seems to be a call to refresh the entire application. The documentation makes mention of
	 * 'the' planning cube, but I think in practice this is really the whole application.
	 *
	 * @return a job for tracking the refresh operation
	 */
	PbcsJobStatus refreshCube();

	/**
	 * Refreshes the cube with the refresh name. If the refresh name listed in
	 * your cube doesn't work, try using 'CubeRefresh'. During testing this
	 * seemed to work even when the refresh name was actually different.
	 *
	 * @param cubeRefreshName the CUBE_REFRESH name
	 * @return a job launch result
	 */
	PbcsJobStatus refreshCube(String cubeRefreshName);

    /**
     * Launches a business rule on the application, providing no additional
     * parameters
     *
     * @param ruleName the name of the business rule exactly as it appears in
     *            the application
     * @return a job launch result
     */
    PbcsJobStatus launchBusinessRule(String ruleName);

    /**
     * Launches a business rule on the application, providing additional
     * parameters
     *
     * @param ruleName the name of the business rule exactly as it appears in
     *            the application
     * @param parameters the parameters to pass along
     * @return a job launch result
     */
    PbcsJobStatus launchBusinessRule(String ruleName, Map<String, String> parameters);

	PbcsMember addMember(String dimensionName, String memberName, String parentName);

	/**
	 * Get information about a member. This is considered the canonical way to get a member from the PBCS/EPM Cloud
	 * member info API since it can make a straight call to the proper end point. The REST API endpoint has the dimension
	 * name in it, which necessitates knowing the dimension name ahead of time (unfortunately).
	 *
	 * <p>You will almost always be better served using methods on the {@link PbcsPlanType}, such as the equivalent method
	 * {@link PbcsPlanType#getMember(String, String)}.</p>
	 *
	 * @param dimensionName the name of the dimension that the member is in
	 * @param memberName the member name to look up
	 * @return the member
	 */
	PbcsMember getMember(String dimensionName, String memberName);

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
	 * Get the list of cubes/plans in this application. The endpoint for this REST call doesn't seem to exist for FCCS
	 * apps (need to confirm this, there may have been some sort of testing issue).
	 *
	 * @return the list of plans for this application
	 */
	List<PbcsPlanType> getPlanTypes();

	/**
	 * Gets the plan with the given name. This method is easy to use, but callers are strongly encourage to use the
	 * full {@link #getPlanType(PlanTypeConfiguration)} method, which provides much more control over how the cube
	 * is instantiated and validated.
	 *
	 * @param planTypeName the plan name
	 * @return the plan/cube
	 */
	PbcsPlanType getPlanType(String planTypeName);

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
	 *
	 * <p>As can be noticed in the extensive number of options, there is a fair bit of "ceremony"
	 * in constructing a {@link PbcsPlanType} object. Some of the objects may carry relatively
	 * significant performance penalties (e.g. validating all dimensions). It is recommended that
	 * in environments that may be instantiating plan objects repeatedly (e.g. in a data gateway
	 * servlet), some form of connection pooling will be used.
	 */
	interface PlanTypeConfiguration {

		/**
		 * Get name of the plan type (cube).
		 *
		 * @return the name of the plan type
		 */
		String getName();

		/**
		 * If true, an explicit dimension plan type will be created and the AIF endpoint will be queried to get the list
		 * of dimensions to populate. This option is off by default. "Regular" users may not have permissions to query
		 * the endpoint, so this method will generally only work for users with Service Administrator privileges and
		 * above. If set to true, the value of {@link #getExplicitDimensions()} will be ignored. Note that attribute
		 * dimensions are not returned from this [undocumented] REST endpoint, so if you need attribute dimensions then
		 * you should still populate them through {@link #getExplicitAttributeDimensions()}.
		 *
		 * @return the value for query dimensions
		 */
		boolean isQueryDimensions();

		/**
		 * Determines if a check (a REST call) should be performed to validate that the given plan name is actually valid
		 * or if we should just assume that it is. You'll get a slightly faster response when creating a {@link PbcsPlanType}
		 * because you'll skip a REST call.
		 *
		 * @return true if the validity check should be skipped, false otherwise.
		 */
		boolean isSkipCheck();

		/**
		 * If explicit dimensions are defined, then each one will be validated by issuing a "get member" call using the
		 * dimension name as the dimension and root member. This does not ensure that a list of dimensions is complete,
		 * just that all the defined dimensions are valid.
		 *
		 * @return true to perform dimension validation
		 */
		boolean isValidateDimensions();

		/**
		 * The list of explicit dimensions that are being set for the plan. It's not required to set dimensions to use
		 * a plan type, but you must set them if you want dimension resolution without a dimension name to work such as
		 * in {@link PbcsPlanType#getMember(String)}.
		 *
		 * @return the list of explicit (known) dimensions for the plan, empty collection if none are being set
		 */
		List<String> getExplicitDimensions();

		/**
		 * You can specify a list of dimensions that are explicitly attribute dimensions, and as
		 * such their {@link PbcsDimension#getDimensionType()} will return {@link PbcsMemberType#ATTRIBUTE}.
		 * Being able to differentiate attribute dimensions is useful for certain grid parsing operations
		 * that may need to know which dimension axes are optional. Not all attribute dimensions have
		 * to be specified.
		 *
		 * @return the list of dimension names that are attribute dimensions
		 */
		List<String> getExplicitAttributeDimensions();

		/**
		 * Gets the member dimension cache that will be used for the plan type. The default implementation is generally
		 * just a simple {@link com.jasonwjones.pbcs.client.memberdimensioncache.InMemoryMemberDimensionCache} but for
		 * performance or other reasons, the caller may want to specify their own resolver
		 * .
		 * @return the dimension name cache to use for the plan
		 */
		PbcsPlanType.MemberDimensionCache getMemberDimensionCache();

		/**
		 * Gets the member resolver that has been set for this configuration, if any.
		 *
		 * @return the member resolver
		 */
		PbcsPlanType.MemberResolver getMemberResolver();

		/**
		 * The number of threads that will be used in a {@link PbcsPlanType#getMemberOrAlias(String)} search operation.
		 *
		 * @return the threads for a fixed thread pool executor
		 */
		int getMemberSearchThreads();

		/**
		 * Controls whether aliases are omitted when {@link PbcsExplicitDimensionsPlanType#cache()} pre-populates the
		 * configured member resolver. This can prevent ambiguous entries when an outline, such as an FCCS Currency
		 * dimension, contains duplicate aliases. Member names are always cached.
		 *
		 * @return true to omit aliases from bulk member caching
		 */
		default boolean isIgnoreAliases() {
			return false;
		}

	}

}
