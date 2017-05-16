package com.jasonwjones.pbcs.api.v3;

public class SubstitutionVariable {

	private String name;

	private String value;

	private String planType;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getPlanType() {
		return planType;
	}

	public void setPlanType(String planType) {
		this.planType = planType;
	}

	@Override
	public String toString() {
		return "SubstitutionVariable [name=" + name + ", value=" + value + ", planType=" + planType + "]";
	}
	
}
