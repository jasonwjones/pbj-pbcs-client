package com.jasonwjones.pbcs.client;

import com.jasonwjones.pbcs.client.impl.grid.DataSliceGrid;

import java.util.List;

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

	PbcsDimension getDimension(String dimensionName);

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
	 * and provides the convenience of being able to set a single cell without all the ceremony of creating a grid
	 *
	 * @param pov the pov
	 * @param value the cell value
	 */
	void setCell(List<String> pov, String value);

	/**
	 * Retrieves a single cell of data at the given POV.
	 *
	 * @param dataPoint the data point
	 * @return the value of the cell, may be an empty string
	 */
	String getCell(List<String> dataPoint);

	DataSliceGrid retrieve();

	/**
	 * Retrieve a grid with the given data point. You should receive back a grid containing one data cell,
	 * surrounded by one member from the last two dimensions and with a POV header for all remaining dimensions.
	 *
	 * @param dataPoint the data point, such as ["Jan", "FY21", "USD", "Final", "Sales"]
	 * @return a data slice for that data cell
	 */
	DataSliceGrid retrieve(List<String> dataPoint);

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

}