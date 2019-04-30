package com.jasonwjones.pbcs.aif;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jasonwjones.pbcs.api.v3.AbstractHypermediaResponse;

// TODO: multiCurrencyFlag, status, targetAppName
public class AifApplication extends AbstractHypermediaResponse<AifDimension> {

	private String applicationName;
	
	private String plan1Name;

	private String plan2Name;
	
	private String plan3Name;
	
	private String plan4Name;

	private String plan5Name;
	
	private String plan6Name;
	
	private Integer validForPlan1;
	
	private Integer validForPlan2;
	
	private Integer validForPlan3;
	
	private Integer validForPlan4;
	
	private Integer validForPlan5;
	
	private Integer validForPlan6;
	
	@JsonProperty("appDimensions")
	public List<AifDimension> getItems() {
		return super.getItems();
	}
	
	public void setItems(List<AifDimension> items) {
		super.setItems(items);
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getPlan1Name() {
		return plan1Name;
	}

	public void setPlan1Name(String plan1Name) {
		this.plan1Name = plan1Name;
	}

	public String getPlan2Name() {
		return plan2Name;
	}

	public void setPlan2Name(String plan2Name) {
		this.plan2Name = plan2Name;
	}

	public String getPlan3Name() {
		return plan3Name;
	}

	public void setPlan3Name(String plan3Name) {
		this.plan3Name = plan3Name;
	}

	public String getPlan4Name() {
		return plan4Name;
	}

	public void setPlan4Name(String plan4Name) {
		this.plan4Name = plan4Name;
	}

	public String getPlan5Name() {
		return plan5Name;
	}

	public void setPlan5Name(String plan5Name) {
		this.plan5Name = plan5Name;
	}

	public String getPlan6Name() {
		return plan6Name;
	}

	public void setPlan6Name(String plan6Name) {
		this.plan6Name = plan6Name;
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
	
	public List<String> getAllPlans() {
		List<String> plans = new ArrayList<String>();
		if (plan1Name != null) plans.add(plan1Name);
		if (plan2Name != null) plans.add(plan2Name);
		if (plan3Name != null) plans.add(plan3Name);
		if (plan4Name != null) plans.add(plan4Name);
		if (plan5Name != null) plans.add(plan5Name);
		if (plan6Name != null) plans.add(plan6Name);
		return plans;
	}
	
}
