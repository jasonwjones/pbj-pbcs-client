package com.jasonwjones.pbcs.aif;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AifDimension {

	@JsonProperty("dimensionName")
	private String name;

	@JsonProperty("balanceColName")
	private String balanceColumnName;

	private String dimensionClass;

	private Integer validForPlan1;

	private Integer validForPlan2;

	private Integer validForPlan3;

	private Integer validForPlan4;

	private Integer validForPlan5;

	private Integer validForPlan6;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBalanceColumnName() {
		return balanceColumnName;
	}

	public void setBalanceColumnName(String balanceColumnName) {
		this.balanceColumnName = balanceColumnName;
	}

	public String getDimensionClass() {
		return dimensionClass;
	}

	public void setDimensionClass(String dimensionClass) {
		this.dimensionClass = dimensionClass;
	}

	public Integer getValidForPlan1() {
		return validForPlan1;
	}

	public void setValidForPlan1(Integer validForPlan1) {
		this.validForPlan1 = validForPlan1;
	}

	public Integer getValidForPlan2() {
		return validForPlan2;
	}

	public void setValidForPlan2(Integer validForPlan2) {
		this.validForPlan2 = validForPlan2;
	}

	public Integer getValidForPlan3() {
		return validForPlan3;
	}

	public void setValidForPlan3(Integer validForPlan3) {
		this.validForPlan3 = validForPlan3;
	}

	public Integer getValidForPlan4() {
		return validForPlan4;
	}

	public void setValidForPlan4(Integer validForPlan4) {
		this.validForPlan4 = validForPlan4;
	}

	public Integer getValidForPlan5() {
		return validForPlan5;
	}

	public void setValidForPlan5(Integer validForPlan5) {
		this.validForPlan5 = validForPlan5;
	}

	public Integer getValidForPlan6() {
		return validForPlan6;
	}

	public void setValidForPlan6(Integer validForPlan6) {
		this.validForPlan6 = validForPlan6;
	}

}