package com.jasonwjones.pbcs.api.v3;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

/**
 * Maps the member properties payload returned by the REST API's member details endpoints.
 */
public class PbcsMemberPropertiesImpl {

	private String name;

	private String alias;

	private List<PbcsMemberPropertiesImpl> children;

	private String description;

	private String parentName;

	private String oldName;

	private String dataType;

	private Integer objectType;

	private String dataStorage;

	@JsonProperty("dimName")
	private String dimensionName;

	private boolean twoPass;

	private List<String> usedIn;

	private CurrencySettings currencySettings;

	private int generation;

	/**
	 * Constructs an empty instance for deserialization.
	 */
	public PbcsMemberPropertiesImpl() {
	}

	/**
	 * Gets the member name.
	 *
	 * @return the member name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the member name.
	 *
	 * @param name the member name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the member's children.
	 *
	 * @return the children, empty list if none
	 */
	public List<PbcsMemberPropertiesImpl> getChildren() {
		// probably unneeded after splitting member/properties but doesn't hurt
		if (children != null) {
			return children;
		}
		return Collections.emptyList();
	}

	/**
	 * Sets the member's children.
	 *
	 * @param children the children
	 */
	public void setChildren(List<PbcsMemberPropertiesImpl> children) {
		this.children = children;
	}

	/**
	 * Gets the member's description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the member's description.
	 *
	 * @param description the description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the name of the member's parent.
	 *
	 * @return the parent name, may be null
	 */
	public String getParentName() {
		return parentName;
	}

	/**
	 * Sets the name of the member's parent.
	 *
	 * @param parentName the parent name
	 */
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	/**
	 * Gets the member's previous name, if it was recently renamed.
	 *
	 * @return the old name, may be null
	 */
	public String getOldName() {
		return oldName;
	}

	/**
	 * Sets the member's previous name.
	 *
	 * @param oldName the old name
	 */
	public void setOldName(String oldName) {
		this.oldName = oldName;
	}

	/**
	 * Gets the member's data type.
	 *
	 * @return the data type
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * Sets the member's data type.
	 *
	 * @param dataType the data type
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	/**
	 * Gets the member's internal object type ID.
	 *
	 * @return the object type ID
	 */
	public Integer getObjectType() {
		return objectType;
	}

	/**
	 * Sets the member's internal object type ID.
	 *
	 * @param objectType the object type ID
	 */
	public void setObjectType(Integer objectType) {
		this.objectType = objectType;
	}

	/**
	 * Gets the member's data storage type, e.g. "Store", "Dynamic Calc", "Shared", etc.
	 *
	 * @return the data storage type
	 */
	public String getDataStorage() {
		return dataStorage;
	}

	/**
	 * Sets the member's data storage type.
	 *
	 * @param dataStorage the data storage type
	 */
	public void setDataStorage(String dataStorage) {
		this.dataStorage = dataStorage;
	}

	/**
	 * Gets the name of the dimension this member belongs to.
	 *
	 * @return the dimension name
	 */
	public String getDimensionName() {
		return dimensionName;
	}

	/**
	 * Sets the name of the dimension this member belongs to.
	 *
	 * @param dimensionName the dimension name
	 */
	public void setDimensionName(String dimensionName) {
		this.dimensionName = dimensionName;
	}

	/**
	 * Whether this member is a two-pass calculation member.
	 *
	 * @return true if two-pass, false otherwise
	 */
	public boolean isTwoPass() {
		return twoPass;
	}

	/**
	 * Sets whether this member is a two-pass calculation member.
	 *
	 * @param twoPass true if two-pass, false otherwise
	 */
	public void setTwoPass(Boolean twoPass) {
		this.twoPass = twoPass;
	}

	/**
	 * Gets the plan types this member is used in.
	 *
	 * @return the plan type names
	 */
	public List<String> getUsedIn() {
		return usedIn;
	}

	/**
	 * Sets the plan types this member is used in.
	 *
	 * @param usedIn the plan type names
	 */
	public void setUsedIn(List<String> usedIn) {
		this.usedIn = usedIn;
	}

	/**
	 * Gets the currency settings for this member, if applicable.
	 *
	 * @return the currency settings, may be null
	 */
	public CurrencySettings getCurrencySettings() {
		return currencySettings;
	}

	/**
	 * Sets the currency settings for this member.
	 *
	 * @param currencySettings the currency settings
	 */
	public void setCurrencySettings(CurrencySettings currencySettings) {
		this.currencySettings = currencySettings;
	}

	/**
	 * Gets the member's alias.
	 *
	 * @return the alias, may be null
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * Sets the member's alias.
	 *
	 * @param alias the alias
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * Gets the member's generation.
	 *
	 * @return the generation
	 */
	public int getGeneration() {
		return generation;
	}

	/**
	 * Sets the member's generation.
	 *
	 * @param generation the generation
	 */
	public void setGeneration(int generation) {
		this.generation = generation;
	}

}
