package com.jasonwjones.pbcs.client;

import com.jasonwjones.pbcs.api.v3.SubstitutionVariable;
import com.jasonwjones.pbcs.client.impl.grid.DataSliceGrid;

import java.util.List;
import java.util.Set;

/**
 * Represents a particular plan type (cube) contained as part of a {@link PbcsApplication}. The PBCS REST API doesn't
 * necessarily make a strong distinction between the application and the plan type, but it is modeled explicitly in this
 * API as it keeps a lot of semantics cleaner, particularly with respect to which dimensions are in which cube.
 */
public interface PbcsPlanType {

	/**
	 * The name of this plan/cube.
	 *
	 * @return the name of the cube
	 */
	String getName();

	/**
	 * Gets the list of dimensions for this plan/cube. This will be either the explicitly specified dimensions
	 * for this cube (if defined), otherwise a call is made to the unofficial data management (DM) endpoint
	 * that provides dimensional information. In the past I've seen issues with permissions where low-level
	 * users couldn't access this endpoint, so be careful how you architect your solutions with this.
	 *
	 * <p>Additionally, in the past I've also seen issues where the cube may have not been synced properly
	 * and the list of dimensions was bogus. I don't know if this was a timing/configuration thing or
	 * some other issue where the DM dimensions were out of sync.
	 *
	 * @return the list of dimensions for this plan/cube
	 */
	List<PbcsDimension> getDimensions();

	/**
	 * Gets the list of jobs that are specific to this plan type.
	 *
	 * @return the list of jobs specific to this plan, empty list if none
	 */
	List<PbcsJobDefinition> getJobs();

	/**
	 * Gets a dimension with the given name.
	 *
	 * @param dimensionName the name of the dimension, such as Period, Scenario, etc.
	 * @return a dimension object for that dimension
	 */
	PbcsDimension getDimension(String dimensionName);

	/**
	 * Checks if this plan has been configured with explicit dimensions or not.
	 *
	 * @return true if explicit dimensions are being used, false otherwise
	 */
	boolean isExplicitDimensions();

	/**
	 * Gets the application that owns this plan/cube.
	 *
	 * @return the application for this plan/cube
	 */
	PbcsApplication getApplication();

	/**
	 * Gets a single data cell from the top of the house across all dimensions. This isn't generally going to
	 * yield any particularly useful information, but rather, is meant as a quick and easy way to hit the cube
	 * with the configured dimensions and make sure they're all represent and that you can in fact perform
	 *
	 * @return the cell value for a top of the house retrieve, may be the empty string if no value present
	 */
	String getCell();

	/**
	 * Sets the value for a single cell. Internally, this method just wraps the "import data slice" endpoint
	 * and provides the convenience of being able to set a single cell without all the ceremony of creating a grid. This
	 * method uses the default import options defined in {@link com.jasonwjones.pbcs.client.impl.PbcsPlanTypeImpl#DEFAULT_IMPORT_OPTIONS},
	 * use the related method if you need full control over how the data import is specified.
	 *
	 * @param pov the pov
	 * @param value the cell value
	 * @return the data import results
	 * @see #setCell(List, String, ImportDataOptions) if you need full control over the import data behavior
	 */
	ImportDataResult setCell(List<String> pov, String value);

	/**
	 * Sets the value for a single cell. Internally, this method just wraps the "import data slice" endpoint
	 * and provides the convenience of being able to set a single cell without all the ceremony of creating a grid
	 *
	 * @param pov the pov
	 * @param value the cell value
	 * @param importDataOptions the options to use when importing
	 * @return the data import results
	 */
	ImportDataResult setCell(List<String> pov, String value, ImportDataOptions importDataOptions);

	/**
	 * Update multiple cells using the POV and the given grid. A simple parsing strategy will be used on the grid.
	 *
	 * @param pov the pov
	 * @param values the cell values
	 * @return the data import results
	 * @see #setCell(List, String, ImportDataOptions) if you need full control over the import data options
	 */
	ImportDataResult setCells(List<String> pov, Grid<String> values);

	/**
	 * Update multiple cells using the POV and the given grid. A simple parsing strategy will be used on the grid.
	 *
	 * @param pov the pov
	 * @param values the cell values
	 * @return the data import results
	 */
	ImportDataResult setCells(List<String> pov, Grid<String> values, ImportDataOptions importDataOptions);

	/**
	 * Retrieves a single cell of data at the given POV.
	 *
	 * @param dataPoint the data point
	 * @return the value of the cell, may be an empty string
	 * @see #setCell(List, String) for setting a single cell value
	 */
	String getCell(List<String> dataPoint);

	/**
	 * Perform a "default" retrieve against this plan/cube. This only works when the dimensions have been specified
	 * using explicit dimensions (e.g. using {@link PbcsApplication#getPlanType(PbcsApplication.PlanTypeConfiguration)})
	 * where the configuration has a list of dimensions provided. Internally, the retrieve is performed using the standard
	 * "export data slice" REST endpoint; the dimensions list is needed in order to fill out a grid with one cell
	 * using a member from each dimension. This method is essentially offered for convenience and "smoke testing".
	 *
	 * @return a data slice for a default retrieve from the cube
	 */
	DataSliceGrid retrieve();

	/**
	 * Retrieve a grid with the given data point. You should receive back a grid containing one data cell,
	 * surrounded by one member from the last two dimensions and with a POV header for all remaining dimensions.
	 *
	 * @param dataPoint the data point, such as ["Jan", "FY21", "USD", "Final", "Sales"]
	 * @return a data slice for that data cell
	 */
	DataSliceGrid retrieve(List<String> dataPoint);

	/**
	 * Perform a retrieve using the given POV and grid definition.
	 *
	 * @param pov the POV
	 * @param grid a grid
	 * @return a data slice for the defined POV/grid
	 */
	DataSliceGrid retrieve(List<String> pov, Grid<String> grid);

	/**
	 * This is the canonical call to get member information from the corresponding PBCS endpoint for doing
	 * so. This particular endpoint has the dimension name in its URL, which ostensibly means that the
	 * dimension name must be known ahead of time in order to query a member.
	 *
	 * @param dimensionName the dimension name
	 * @param memberName the member name
	 * @return member properties for the member if found, null if not
	 */
	PbcsMemberProperties getMember(String dimensionName, String memberName);

	/**
	 * Provided as a convenience to simply iterate the list of dimensions and try them until the member is
	 * found. As this can have serious performance implications, you are strongly urged to cache the results
	 * of this if you are going to be hitting it often.
	 *
	 * @param memberName the member name
	 * @return member properties if member found, null if not
	 */
	PbcsMemberProperties getMember(String memberName);

	/**
	 * Perform a member query. The notion of querying members doesn't really exist in the PBCS REST API; these querying
	 * capabilities are provided as a bit of a convenience on top of the basic member relation methods that are available
	 * such as {@link PbcsMemberProperties#getChildren()}.
	 *
	 * @param memberName the base member name or alias to perform the query with
	 * @param queryType the type of query
	 * @return the list of members that result (or an empty list if there are none)
	 */
	List<PbcsMemberProperties> queryMembers(String memberName, PbcsMemberQueryType queryType);

	/**
	 * Similar to {@link #getMember(String)}, this is provided as a convenience to try and find a member using its name
	 * or alias. The implementation for this method is a little different compared to its counterpart. This method will
	 * perform a brute-force search through the explicit dimensions, fetch the root member, and then traverse the
	 * hierarchy looking for any member with a matching name or alias. The comparison is performed in a case-insensitive
	 * manner.
	 *
	 * <p>As with the other convenience method, this search process is potentially expensive in that it's possible
	 * every dimension needs to be searched to find the member. Further, this method does not currently participate in
	 * the member to dimension cache process, so if you plan on hitting this method a lot (such as to resolve names
	 * across a large data grid) you will almost certainly want to use a cache, such as exists in <code>CachingPbcsPlanType</code>
	 * (not currently part of this library).
	 *
	 * @param memberOrAliasName the member name or alias to search for
	 * @return the member for the given name or alias, or null if none is found across all the known dimensions
	 * @since 1.0.10
	 */
	PbcsMemberProperties getMemberOrAlias(String memberOrAliasName);

	/**
	 * Gets the substitution variables that are specific to this cube/plan. This will not return the variables that
	 * are set for the overall application, you should use {@link PbcsApplication#getSubstitutionVariables()} for that.
	 *
	 * @return substitution variables specific to this plan
	 */
	Set<SubstitutionVariable> getSubstitutionVariables();

	/**
	 * A member dimension cache is a simple cache that caches the dimension for member names. Due to the way the PBCS
	 * REST API is structured, the dimension for a given member name must be known in order to get member details because
	 * the dimension name is in the REST URL. However, in order to provide some friendliness in the PBJ API, you can
	 * request a member using only its name using {@link #getMember(String)}. Under the hood, a brute-force search will
	 * be conducted using the explicitly-specified dimensions. A member dimension cache can be used to cache results in
	 * memory, in a properties file, or some other implementation can be provided for performance or other reasons.
	 */
	interface MemberDimensionCache {

		/**
		 * Gets the name of the dimension that the given member is associated with.
		 *
		 * @param memberName the member name to get the dimension of
		 * @return the name of that member's dimension, null if none is found
		 */
		String getDimensionName(String memberName);

		/**
		 * Sets the known dimension for a given member.
		 *
		 * @param memberName the member name
		 * @param dimensionName the dimension of the member
		 */
		void setDimension(String memberName, String dimensionName);

	}

	/**
	 * Represents the results of importing data to the plan/cube. The values for accepted and rejected cells are
	 * inherently part of the response to the <code>importDataSlice</code> REST API call, so they are simply passed
	 * through in this object.
	 */
	interface ImportDataResult {

		/**
		 * The number of cells that were accepted to be updated in the cube.
		 *
		 * @return the number of cells
		 */
		int getAcceptedCells();

		/**
		 * The number of cells that were rejected to be updated in the cube.
		 *
		 * @return the number of cells
		 */
		int getRejectedCells();

	}

	/**
	 * Represents all the options that can be used on the <code>importDataSlice</code> REST endpoint. Most of these
	 * options map 1:1 to options in the request payload, however, some are purely niceties in this library, such as
	 * the ability to throw an exception on any rejected data in the request.
	 */
	interface ImportDataOptions {

		/**
		 * Set to true if values should be added to existing values (no effect for date and text types). Despite the
		 * name, this has nothing to do with running a calc all on the cube.
		 *
		 * @return true to add values, false otherwise
		 */
		boolean isAggregateData();

		CellNotesOption getCellNotesOption();

		String getDateFormat();

		boolean isStrictDateValidation();

		/**
		 * Don't actually import the data
		 *
		 * @return true if it's a dry run, false otherwise
		 */
		boolean isDryRun();

		/**
		 * True if rejected cells should be included in response
		 *
		 * @return if rejected cells should be included in response
		 */
		boolean isIncludeRejectedCells();

		/**
		 * True if rejected cells should have additional details
		 *
		 * @return true if response should include the additional details, false otherwise
		 */
		boolean isIncludeRejectedCellsWithDetails();

		/**
		 * Per the docs, this will normally be null but may be used by planners in Data Management.
		 *
		 * @return the post data import rule names
		 */
		String getPostDataImportRuleNames();

		/**
		 * If true, then a {@link com.jasonwjones.pbcs.client.exceptions.PbcsDataImportException} should be thrown if
		 * any of the cells were rejected.
		 *
		 * @return true if exception should be thrown for rejected data, false otherwise
		 */
		boolean isThrowExceptionIfAnyRejectedCells();

	}

	enum CellNotesOption {

		OVERWRITE("Overwrite"),

		APPEND("Append"),

		SKIP("Skip");

		private final String apiCode;

		CellNotesOption(String apiCode) {
			this.apiCode = apiCode;
		}

		public String getApiCode() {
			return apiCode;
		}

	}

}