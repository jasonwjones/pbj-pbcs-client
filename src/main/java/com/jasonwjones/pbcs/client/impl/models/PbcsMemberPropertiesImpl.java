package com.jasonwjones.pbcs.client.impl.models;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jasonwjones.pbcs.client.PbcsMemberProperties;
import com.jasonwjones.pbcs.client.PbcsMemberType;
import com.jasonwjones.pbcs.client.PbcsObjectType;

public class PbcsMemberPropertiesImpl implements PbcsMemberProperties {

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

	@Override
	public PbcsObjectType getObjectType() {
		return PbcsObjectType.MEMBER;
	}

	public void setName(String name) {
		this.name = name;
	}

	private int generation;

	public List<PbcsMemberPropertiesImpl> getChildren() {
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

	public Integer getObjectNumericType() {
		return objectType;
	}

	@Override
	public PbcsMemberType getType() {
		return PbcsMemberType.valueOf(objectType);
	}

	public void setObjectType(Integer objectType) {
		this.objectType = objectType;
	}

	public String getDataStorage() {
		return dataStorage;
	}

	@JsonIgnore
	@Override
	public DataStorage getDataStorageType() {
		return DataStorage.valueOfOrOther(dataStorage);
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

	@JsonIgnore
	@Override
	public boolean isLeaf() {
		return getChildren().isEmpty();
	}

	@Override
	public List<String> getUsedIn() {
		return usedIn;
	}

	public void setUsedIn(List<String> usedIn) {
		this.usedIn = usedIn;
	}

	@JsonIgnore
	@Override
	public int getLevel() {
		if (children == null || children.isEmpty()) {
			return 0;
		} else {
			int minLevel = -1;
			for (PbcsMemberProperties child : children) {
				if (minLevel == -1) {
					minLevel = child.getLevel();
				} else {
					minLevel = Math.min(minLevel, child.getLevel());
				}
			}
			return minLevel + 1;
		}
	}

	@Override
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public int getGeneration() {
		return generation;
	}

	public void setGeneration(int generation) {
		this.generation = generation;
	}

	@Override
	public String toString() {
		String aliasText = alias != null ? " (alias: " + alias + ")" : "";
		return name + aliasText;
	}

}