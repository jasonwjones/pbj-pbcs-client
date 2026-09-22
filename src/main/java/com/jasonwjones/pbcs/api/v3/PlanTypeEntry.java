package com.jasonwjones.pbcs.api.v3;

/**
 * One plan type as the plan-type list endpoint
 * ({@code applications/{application}/plantypes}) returns it.
 *
 * <p>This endpoint is newer than this library. Listing an application's plan types used to mean
 * reading {@code plan1Name} through {@code plan6Name} off a Data Management
 * ({@link com.jasonwjones.pbcs.aif.AifApplication AIF}) application record - six BSO slots,
 * which is neither all of an application's cubes nor a Planning API.
 */
public class PlanTypeEntry {

	private String planTypeName;

	private Integer planType;

	private String cubeName;

	private Integer numDimensions;

	private Integer cubeType;

	/**
	 * Constructs an empty instance for deserialization.
	 */
	public PlanTypeEntry() {
	}

	/**
	 * Gets the plan type's name.
	 *
	 * @return the plan type name
	 */
	public String getPlanTypeName() {
		return planTypeName;
	}

	/**
	 * Sets the plan type's name.
	 *
	 * @param planTypeName the plan type name
	 */
	public void setPlanTypeName(String planTypeName) {
		this.planTypeName = planTypeName;
	}

	/**
	 * Gets the plan type's identifier within the application.
	 *
	 * @return the plan type identifier
	 */
	public Integer getPlanType() {
		return planType;
	}

	/**
	 * Sets the plan type's identifier within the application.
	 *
	 * @param planType the plan type identifier
	 */
	public void setPlanType(Integer planType) {
		this.planType = planType;
	}

	/**
	 * Gets the name of the cube backing this plan type.
	 *
	 * @return the cube name
	 */
	public String getCubeName() {
		return cubeName;
	}

	/**
	 * Sets the name of the cube backing this plan type.
	 *
	 * @param cubeName the cube name
	 */
	public void setCubeName(String cubeName) {
		this.cubeName = cubeName;
	}

	/**
	 * Gets the number of dimensions the plan type has.
	 *
	 * @return the dimension count, null if the endpoint did not report one
	 */
	public Integer getNumDimensions() {
		return numDimensions;
	}

	/**
	 * Sets the number of dimensions the plan type has.
	 *
	 * @param numDimensions the dimension count
	 */
	public void setNumDimensions(Integer numDimensions) {
		this.numDimensions = numDimensions;
	}

	/**
	 * Gets the cube's storage model, as the endpoint's own numeric code.
	 *
	 * <p>Left as the raw number rather than mapped to an enum: {@code 0} and {@code 136} are the two
	 * seen in practice, for a standard Planning cube and an aggregate storage one respectively, and
	 * inventing names for a set we cannot enumerate would turn an unrecognised future value into a
	 * deserialization failure rather than a number a caller can still read.
	 *
	 * @return the cube type code, null if the endpoint did not report one
	 */
	public Integer getCubeType() {
		return cubeType;
	}

	/**
	 * Sets the cube's storage model code.
	 *
	 * @param cubeType the cube type code
	 */
	public void setCubeType(Integer cubeType) {
		this.cubeType = cubeType;
	}

}
