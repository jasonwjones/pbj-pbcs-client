package com.jasonwjones.pbcs.api.v3;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class PbcsMemberPropertiesImpl {

	private String name;

	private String alias;

	private List<PbcsMemberPropertiesImpl> children;

	private String description;

	private String parentName;

	private String dataType;

	private Integer objectType;

	private String dataStorage;

	@JsonProperty("dimName")
	private String dimensionName;

	private boolean twoPass;

	private List<String> usedIn;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private int generation;

	public List<PbcsMemberPropertiesImpl> getChildren() {
		// probably unneeded after splitting member/properties but doesn't hurt
		if (children != null) {
			return children;
		}
		return Collections.emptyList();
	}

	public void setChildren(List<PbcsMemberPropertiesImpl> children) {
		this.children = children;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public Integer getObjectType() {
		return objectType;
	}

	public void setObjectType(Integer objectType) {
		this.objectType = objectType;
	}

	public String getDataStorage() {
		return dataStorage;
	}

	public void setDataStorage(String dataStorage) {
		this.dataStorage = dataStorage;
	}

	public String getDimensionName() {
		return dimensionName;
	}

	public void setDimensionName(String dimensionName) {
		this.dimensionName = dimensionName;
	}

	public boolean isTwoPass() {
		return twoPass;
	}

	public void setTwoPass(Boolean twoPass) {
		this.twoPass = twoPass;
	}

	public List<String> getUsedIn() {
		return usedIn;
	}

	public void setUsedIn(List<String> usedIn) {
		this.usedIn = usedIn;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public int getGeneration() {
		return generation;
	}

	public void setGeneration(int generation) {
		this.generation = generation;
	}

}