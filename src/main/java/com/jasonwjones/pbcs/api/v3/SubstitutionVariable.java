package com.jasonwjones.pbcs.api.v3;

import org.springframework.util.Assert;

/**
 * Represents a substitution variable within a PBCS application.
 * 
 * @author jasonwjones
 *
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
	 * Returns the plan type (cube) that the variable is in, if any
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((planType == null) ? 0 : planType.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		// just a default equals implementation by Eclipse, but in future would
		// be nice
		// to replace with helper method from Guava or something
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SubstitutionVariable other = (SubstitutionVariable) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (planType == null) {
			if (other.planType != null) {
				return false;
			}
		} else if (!planType.equals(other.planType)) {
			return false;
		}
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "SubstitutionVariable [name=" + name + ", value=" + value + ", planType=" + planType + "]";
	}

}
