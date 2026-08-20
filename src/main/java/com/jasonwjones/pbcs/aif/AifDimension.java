package com.jasonwjones.pbcs.aif;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Maps a single dimension entry as returned by the unofficial data management (DM/AIF) "applications"
 * endpoint, describing the dimension's name, its balance column name, its class, and which of an
 * application's up to six plans the dimension is valid for.
 */
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

	/**
	 * Constructs an empty instance for deserialization.
	 */
	public AifDimension() {
	}

	/**
	 * Gets the name of the dimension.
	 *
	 * @return the dimension name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the dimension.
	 *
	 * @param name the dimension name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the balance column name associated with this dimension.
	 *
	 * @return the balance column name
	 */
	public String getBalanceColumnName() {
		return balanceColumnName;
	}

	/**
	 * Sets the balance column name associated with this dimension.
	 *
	 * @param balanceColumnName the balance column name
	 */
	public void setBalanceColumnName(String balanceColumnName) {
		this.balanceColumnName = balanceColumnName;
	}

	/**
	 * Gets the dimension class (e.g., account, entity, or a custom dimension class).
	 *
	 * @return the dimension class
	 */
	public String getDimensionClass() {
		return dimensionClass;
	}

	/**
	 * Sets the dimension class.
	 *
	 * @param dimensionClass the dimension class
	 */
	public void setDimensionClass(String dimensionClass) {
		this.dimensionClass = dimensionClass;
	}

	/**
	 * Indicates whether this dimension is valid/enabled for plan 1.
	 *
	 * @return a non-zero value if valid for plan 1, null if not applicable
	 */
	public Integer getValidForPlan1() {
		return validForPlan1;
	}

	/**
	 * Sets whether this dimension is valid/enabled for plan 1.
	 *
	 * @param validForPlan1 the valid-for-plan-1 flag
	 */
	public void setValidForPlan1(Integer validForPlan1) {
		this.validForPlan1 = validForPlan1;
	}

	/**
	 * Indicates whether this dimension is valid/enabled for plan 2.
	 *
	 * @return a non-zero value if valid for plan 2, null if not applicable
	 */
	public Integer getValidForPlan2() {
		return validForPlan2;
	}

	/**
	 * Sets whether this dimension is valid/enabled for plan 2.
	 *
	 * @param validForPlan2 the valid-for-plan-2 flag
	 */
	public void setValidForPlan2(Integer validForPlan2) {
		this.validForPlan2 = validForPlan2;
	}

	/**
	 * Indicates whether this dimension is valid/enabled for plan 3.
	 *
	 * @return a non-zero value if valid for plan 3, null if not applicable
	 */
	public Integer getValidForPlan3() {
		return validForPlan3;
	}

	/**
	 * Sets whether this dimension is valid/enabled for plan 3.
	 *
	 * @param validForPlan3 the valid-for-plan-3 flag
	 */
	public void setValidForPlan3(Integer validForPlan3) {
		this.validForPlan3 = validForPlan3;
	}

	/**
	 * Indicates whether this dimension is valid/enabled for plan 4.
	 *
	 * @return a non-zero value if valid for plan 4, null if not applicable
	 */
	public Integer getValidForPlan4() {
		return validForPlan4;
	}

	/**
	 * Sets whether this dimension is valid/enabled for plan 4.
	 *
	 * @param validForPlan4 the valid-for-plan-4 flag
	 */
	public void setValidForPlan4(Integer validForPlan4) {
		this.validForPlan4 = validForPlan4;
	}

	/**
	 * Indicates whether this dimension is valid/enabled for plan 5.
	 *
	 * @return a non-zero value if valid for plan 5, null if not applicable
	 */
	public Integer getValidForPlan5() {
		return validForPlan5;
	}

	/**
	 * Sets whether this dimension is valid/enabled for plan 5.
	 *
	 * @param validForPlan5 the valid-for-plan-5 flag
	 */
	public void setValidForPlan5(Integer validForPlan5) {
		this.validForPlan5 = validForPlan5;
	}

	/**
	 * Indicates whether this dimension is valid/enabled for plan 6.
	 *
	 * @return a non-zero value if valid for plan 6, null if not applicable
	 */
	public Integer getValidForPlan6() {
		return validForPlan6;
	}

	/**
	 * Sets whether this dimension is valid/enabled for plan 6.
	 *
	 * @param validForPlan6 the valid-for-plan-6 flag
	 */
	public void setValidForPlan6(Integer validForPlan6) {
		this.validForPlan6 = validForPlan6;
	}

}
