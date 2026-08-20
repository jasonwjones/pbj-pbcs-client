package com.jasonwjones.pbcs.aif;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jasonwjones.pbcs.api.v3.AbstractHypermediaResponse;

// TODO: multiCurrencyFlag, status, targetAppName
/**
 * Maps the application details returned by the unofficial data management (DM/AIF) "applications" endpoint,
 * including the up to six plan names an application can have, which of those plans each dimension is valid
 * for, and any additional application properties. The dimensions themselves are carried in the inherited
 * {@link #getItems()} list.
 */
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

	private List<AifAppProperty> appProperties;

	/**
	 * Constructs an empty instance for deserialization.
	 */
	public AifApplication() {
	}

	@JsonProperty("appDimensions")
	public List<AifDimension> getItems() {
		return super.getItems();
	}

	public void setItems(List<AifDimension> items) {
		super.setItems(items);
	}

	/**
	 * Gets the name of the application.
	 *
	 * @return the application name
	 */
	public String getApplicationName() {
		return applicationName;
	}

	/**
	 * Sets the name of the application.
	 *
	 * @param applicationName the application name
	 */
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	/**
	 * Gets the name of plan 1, if configured.
	 *
	 * @return the plan 1 name, null if not configured
	 */
	public String getPlan1Name() {
		return plan1Name;
	}

	/**
	 * Sets the name of plan 1.
	 *
	 * @param plan1Name the plan 1 name
	 */
	public void setPlan1Name(String plan1Name) {
		this.plan1Name = plan1Name;
	}

	/**
	 * Gets the name of plan 2, if configured.
	 *
	 * @return the plan 2 name, null if not configured
	 */
	public String getPlan2Name() {
		return plan2Name;
	}

	/**
	 * Sets the name of plan 2.
	 *
	 * @param plan2Name the plan 2 name
	 */
	public void setPlan2Name(String plan2Name) {
		this.plan2Name = plan2Name;
	}

	/**
	 * Gets the name of plan 3, if configured.
	 *
	 * @return the plan 3 name, null if not configured
	 */
	public String getPlan3Name() {
		return plan3Name;
	}

	/**
	 * Sets the name of plan 3.
	 *
	 * @param plan3Name the plan 3 name
	 */
	public void setPlan3Name(String plan3Name) {
		this.plan3Name = plan3Name;
	}

	/**
	 * Gets the name of plan 4, if configured.
	 *
	 * @return the plan 4 name, null if not configured
	 */
	public String getPlan4Name() {
		return plan4Name;
	}

	/**
	 * Sets the name of plan 4.
	 *
	 * @param plan4Name the plan 4 name
	 */
	public void setPlan4Name(String plan4Name) {
		this.plan4Name = plan4Name;
	}

	/**
	 * Gets the name of plan 5, if configured.
	 *
	 * @return the plan 5 name, null if not configured
	 */
	public String getPlan5Name() {
		return plan5Name;
	}

	/**
	 * Sets the name of plan 5.
	 *
	 * @param plan5Name the plan 5 name
	 */
	public void setPlan5Name(String plan5Name) {
		this.plan5Name = plan5Name;
	}

	/**
	 * Gets the name of plan 6, if configured.
	 *
	 * @return the plan 6 name, null if not configured
	 */
	public String getPlan6Name() {
		return plan6Name;
	}

	/**
	 * Sets the name of plan 6.
	 *
	 * @param plan6Name the plan 6 name
	 */
	public void setPlan6Name(String plan6Name) {
		this.plan6Name = plan6Name;
	}

	/**
	 * Indicates whether plan 1 is valid/enabled for this application.
	 *
	 * @return a non-zero value if plan 1 is valid, null if not applicable
	 */
	public Integer getValidForPlan1() {
		return validForPlan1;
	}

	/**
	 * Sets whether plan 1 is valid/enabled for this application.
	 *
	 * @param validForPlan1 the valid-for-plan-1 flag
	 */
	public void setValidForPlan1(Integer validForPlan1) {
		this.validForPlan1 = validForPlan1;
	}

	/**
	 * Indicates whether plan 2 is valid/enabled for this application.
	 *
	 * @return a non-zero value if plan 2 is valid, null if not applicable
	 */
	public Integer getValidForPlan2() {
		return validForPlan2;
	}

	/**
	 * Sets whether plan 2 is valid/enabled for this application.
	 *
	 * @param validForPlan2 the valid-for-plan-2 flag
	 */
	public void setValidForPlan2(Integer validForPlan2) {
		this.validForPlan2 = validForPlan2;
	}

	/**
	 * Indicates whether plan 3 is valid/enabled for this application.
	 *
	 * @return a non-zero value if plan 3 is valid, null if not applicable
	 */
	public Integer getValidForPlan3() {
		return validForPlan3;
	}

	/**
	 * Sets whether plan 3 is valid/enabled for this application.
	 *
	 * @param validForPlan3 the valid-for-plan-3 flag
	 */
	public void setValidForPlan3(Integer validForPlan3) {
		this.validForPlan3 = validForPlan3;
	}

	/**
	 * Indicates whether plan 4 is valid/enabled for this application.
	 *
	 * @return a non-zero value if plan 4 is valid, null if not applicable
	 */
	public Integer getValidForPlan4() {
		return validForPlan4;
	}

	/**
	 * Sets whether plan 4 is valid/enabled for this application.
	 *
	 * @param validForPlan4 the valid-for-plan-4 flag
	 */
	public void setValidForPlan4(Integer validForPlan4) {
		this.validForPlan4 = validForPlan4;
	}

	/**
	 * Indicates whether plan 5 is valid/enabled for this application.
	 *
	 * @return a non-zero value if plan 5 is valid, null if not applicable
	 */
	public Integer getValidForPlan5() {
		return validForPlan5;
	}

	/**
	 * Sets whether plan 5 is valid/enabled for this application.
	 *
	 * @param validForPlan5 the valid-for-plan-5 flag
	 */
	public void setValidForPlan5(Integer validForPlan5) {
		this.validForPlan5 = validForPlan5;
	}

	/**
	 * Indicates whether plan 6 is valid/enabled for this application.
	 *
	 * @return a non-zero value if plan 6 is valid, null if not applicable
	 */
	public Integer getValidForPlan6() {
		return validForPlan6;
	}

	/**
	 * Sets whether plan 6 is valid/enabled for this application.
	 *
	 * @param validForPlan6 the valid-for-plan-6 flag
	 */
	public void setValidForPlan6(Integer validForPlan6) {
		this.validForPlan6 = validForPlan6;
	}

	/**
	 * Gets the additional application properties returned alongside the core application details.
	 *
	 * @return the application properties, may be null if none were returned
	 */
	public List<AifAppProperty> getAppProperties() {
		return appProperties;
	}

	/**
	 * Sets the additional application properties.
	 *
	 * @param appProperties the application properties
	 */
	public void setAppProperties(List<AifAppProperty> appProperties) {
		this.appProperties = appProperties;
	}

	/**
	 * Gets the names of all the plans configured for this application, in plan 1-6 order, skipping any plan
	 * slots that are not configured.
	 *
	 * @return the list of configured plan names
	 */
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
