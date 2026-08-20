package com.jasonwjones.pbcs.api.v3;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Maps a single dimension entry as returned by the REST API's plan-type-scoped dimension list endpoint
 * ({@code GET applications/{app}/plantypes/{cube}/dimensions}). Unlike the older, unofficial data
 * management (DM/AIF) dimension listing, this endpoint identifies attribute dimensions directly via
 * {@link #getDimType()} and requires no special privileges.
 */
public class PlanTypeDimension {

	@JsonProperty("dimName")
	private String dimensionName;

	private String name;

	private String dimType;

	private String density;

	private boolean enforceSecurity;

	private int objectTypeId;

	private int evaluationOrder;

	private int generation;

	private int level;

	private String objectType;

	private List<String> usedIn;

	private boolean valid;

	private boolean invalidDueToValidIntersection;

	private String path;

	private String displayPath;

	private String aliasPath;

	private String id;

	/**
	 * Constructs an empty instance for deserialization.
	 */
	public PlanTypeDimension() {
	}

	/**
	 * Gets the name of the dimension.
	 *
	 * @return the dimension name
	 */
	public String getDimensionName() {
		return dimensionName;
	}

	/**
	 * Sets the name of the dimension.
	 *
	 * @param dimensionName the dimension name
	 */
	public void setDimensionName(String dimensionName) {
		this.dimensionName = dimensionName;
	}

	/**
	 * Gets the name of the dimension's root member, as returned separately from {@link #getDimensionName()}.
	 * In practice this has always been observed to be equal to the dimension name.
	 *
	 * @return the root member name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the dimension's root member.
	 *
	 * @param name the root member name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the dimension's type classification, e.g. {@code "Account"}, {@code "Period"}, {@code "Entity"},
	 * {@code "Custom"} for user-defined dimensions, or {@code "Attribute Dimension"} for attribute
	 * dimensions. Use {@link com.jasonwjones.pbcs.client.PbcsMemberType#fromDimType(String)} to map this
	 * value onto this library's own dimension/member type model.
	 *
	 * @return the dimension type classification
	 */
	public String getDimType() {
		return dimType;
	}

	/**
	 * Sets the dimension's type classification.
	 *
	 * @param dimType the dimension type classification
	 */
	public void setDimType(String dimType) {
		this.dimType = dimType;
	}

	/**
	 * Gets the dimension's storage density, {@code "Dense"} or {@code "Sparse"}.
	 *
	 * @return the density
	 */
	public String getDensity() {
		return density;
	}

	/**
	 * Sets the dimension's storage density.
	 *
	 * @param density the density
	 */
	public void setDensity(String density) {
		this.density = density;
	}

	/**
	 * Whether security is enforced on this dimension.
	 *
	 * @return true if security is enforced, false otherwise
	 */
	public boolean isEnforceSecurity() {
		return enforceSecurity;
	}

	/**
	 * Sets whether security is enforced on this dimension.
	 *
	 * @param enforceSecurity true if security is enforced, false otherwise
	 */
	public void setEnforceSecurity(boolean enforceSecurity) {
		this.enforceSecurity = enforceSecurity;
	}

	/**
	 * Gets the internal object type ID. At this (dimension root) level this has only ever been observed to
	 * be {@code 2}, which does not correspond to {@link com.jasonwjones.pbcs.client.PbcsMemberType}'s
	 * numeric codes; this field is not useful for determining the dimension's type, use
	 * {@link #getDimType()} instead.
	 *
	 * @return the internal object type ID
	 */
	public int getObjectTypeId() {
		return objectTypeId;
	}

	/**
	 * Sets the internal object type ID.
	 *
	 * @param objectTypeId the internal object type ID
	 */
	public void setObjectTypeId(int objectTypeId) {
		this.objectTypeId = objectTypeId;
	}

	/**
	 * Gets the dimension's evaluation order.
	 *
	 * @return the evaluation order
	 */
	public int getEvaluationOrder() {
		return evaluationOrder;
	}

	/**
	 * Sets the dimension's evaluation order.
	 *
	 * @param evaluationOrder the evaluation order
	 */
	public void setEvaluationOrder(int evaluationOrder) {
		this.evaluationOrder = evaluationOrder;
	}

	/**
	 * Gets the generation of the dimension's root member (always 1 for a dimension root).
	 *
	 * @return the generation
	 */
	public int getGeneration() {
		return generation;
	}

	/**
	 * Sets the generation of the dimension's root member.
	 *
	 * @param generation the generation
	 */
	public void setGeneration(int generation) {
		this.generation = generation;
	}

	/**
	 * Gets the level of the dimension's root member (the depth of the dimension's hierarchy).
	 *
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Sets the level of the dimension's root member.
	 *
	 * @param level the level
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * Gets the object type, observed to always be {@code "Dimension"} at this level.
	 *
	 * @return the object type
	 */
	public String getObjectType() {
		return objectType;
	}

	/**
	 * Sets the object type.
	 *
	 * @param objectType the object type
	 */
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	/**
	 * Gets the names of the plan types (cubes) this dimension is used in.
	 *
	 * @return the plan type names
	 */
	public List<String> getUsedIn() {
		return usedIn;
	}

	/**
	 * Sets the names of the plan types this dimension is used in.
	 *
	 * @param usedIn the plan type names
	 */
	public void setUsedIn(List<String> usedIn) {
		this.usedIn = usedIn;
	}

	/**
	 * Whether this dimension is currently valid/consistent in the outline.
	 *
	 * @return true if valid, false otherwise
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * Sets whether this dimension is currently valid/consistent in the outline.
	 *
	 * @param valid true if valid, false otherwise
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	/**
	 * Whether this dimension is specifically invalid due to a valid intersection rule.
	 *
	 * @return true if invalid due to a valid intersection rule, false otherwise
	 */
	public boolean isInvalidDueToValidIntersection() {
		return invalidDueToValidIntersection;
	}

	/**
	 * Sets whether this dimension is invalid due to a valid intersection rule.
	 *
	 * @param invalidDueToValidIntersection true if invalid due to a valid intersection rule, false otherwise
	 */
	public void setInvalidDueToValidIntersection(boolean invalidDueToValidIntersection) {
		this.invalidDueToValidIntersection = invalidDueToValidIntersection;
	}

	/**
	 * Gets the outline path of the dimension's root member.
	 *
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Sets the outline path of the dimension's root member.
	 *
	 * @param path the path
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Gets the display outline path of the dimension's root member.
	 *
	 * @return the display path
	 */
	public String getDisplayPath() {
		return displayPath;
	}

	/**
	 * Sets the display outline path of the dimension's root member.
	 *
	 * @param displayPath the display path
	 */
	public void setDisplayPath(String displayPath) {
		this.displayPath = displayPath;
	}

	/**
	 * Gets the alias outline path of the dimension's root member.
	 *
	 * @return the alias path
	 */
	public String getAliasPath() {
		return aliasPath;
	}

	/**
	 * Sets the alias outline path of the dimension's root member.
	 *
	 * @param aliasPath the alias path
	 */
	public void setAliasPath(String aliasPath) {
		this.aliasPath = aliasPath;
	}

	/**
	 * Gets the internal identifier (GUID) for this dimension.
	 *
	 * @return the internal identifier
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the internal identifier for this dimension.
	 *
	 * @param id the internal identifier
	 */
	public void setId(String id) {
		this.id = id;
	}

}
