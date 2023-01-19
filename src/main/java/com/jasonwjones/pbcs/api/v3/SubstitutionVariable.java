package com.jasonwjones.pbcs.api.v3;

import org.springframework.util.Assert;

import java.util.Objects;

/**
 * Represents a substitution variable within a PBCS application.
 *
 * @author jasonwjones
 */
public class SubstitutionVariable {

	private String name;

	private String value;

	private String planType;

	public static final String DEFAULT_PLANTYPE = "ALL";

	public SubstitutionVariable() {}

	public SubstitutionVariable(String name, String value) {
		this.name = name;
		this.value = value;
		this.planType = DEFAULT_PLANTYPE;
	}

	/**
	 * Get the name of the substitution variable.
	 *
	 * @return the name of this variable
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the substitution variable.
	 *
	 * @param name the name to set
	 */
	public void setName(String name) {
		Assert.hasText(name, "Variable name cannot be empty");
		this.name = name;
	}

	/**
	 * Get the value of the substitution variable
	 *
	 * @return the value of this variable
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value of the substitution variable. Note that this method does
	 * not automatically change the value of the variable on the server, you
	 * will need to call the update method on a PBCS Application object
	 *
	 * @param value the value to set for the variable
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Returns the plan type (cube) that the variable is in, if any. For application-scoped variables, this will return
	 * the value <code>ALL</code> (which is what the PBCS REST API returns).
	 *
	 * @return the plan type of the variable
	 */
	public String getPlanType() {
		return planType;
	}

	/**
	 * Sets the plan type of the variable. Note: the results of changing this
	 * and updating the variable on the server are undefined/untested. If you
	 * need to change a plan type for some reason, you are much better off
	 * creating a new variable.
	 *
	 * @param planType the plan type to set
	 */
	public void setPlanType(String planType) {
		this.planType = planType;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SubstitutionVariable that = (SubstitutionVariable) o;
		return Objects.equals(name, that.name) && Objects.equals(value, that.value) && Objects.equals(planType, that.planType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, value, planType);
	}

	@Override
	public String toString() {
		return "SubstitutionVariable [name=" + name + ", value=" + value + ", planType=" + planType + "]";
	}

}