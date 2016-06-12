package com.jasonwjones.pbcs.client.impl.models;

import java.util.Collections;
import java.util.List;

import com.jasonwjones.pbcs.client.PbcsMemberProperties;

public class PbcsMemberPropertiesImpl implements PbcsMemberProperties {

	private String name;

	private List<PbcsMemberPropertiesImpl> children;

	private String description;

	private String parentName;

	private String dataType;

	private Integer objectType;

	private String dataStorage;

	private String dimensionName;

	private boolean twoPass;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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

	@Override
	public boolean isLeaf() {
		return getChildren().isEmpty();
	}
		
}
