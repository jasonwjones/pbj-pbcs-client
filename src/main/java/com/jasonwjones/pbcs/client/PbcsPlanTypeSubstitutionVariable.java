package com.jasonwjones.pbcs.client;

/**
 * A substitution variable defined for a single plan type, whose {@link #getScope() scope} is
 * {@link PbcsSubstitutionVariableScope#PLAN_TYPE}. Its parent is the plan type, and it belongs to that plan type's
 * application.
 *
 * @since 3.1.0
 */
public interface PbcsPlanTypeSubstitutionVariable extends PbcsApplicationSubstitutionVariable {

	/**
	 * Gets the name of the plan type the variable is defined for. Unlike {@link #getPlanType()}, this never needs a
	 * call to the server.
	 *
	 * @return the plan type name, such as <code>Plan1</code>
	 */
	String getPlanTypeName();

	/**
	 * Gets the plan type the variable is defined for. For a variable read through
	 * {@link PbcsApplication#getVariables()}, the plan type is looked up on first use, the same way as
	 * {@link PbcsApplication#getPlanType(String)}, which checks with the server that it exists.
	 *
	 * @return the plan type
	 */
	PbcsPlanType getPlanType();

	/**
	 * Makes a copy of this variable, for the same plan type, with a different value.
	 *
	 * @param value the new value
	 * @return the copy
	 */
	@Override
	PbcsPlanTypeSubstitutionVariable withValue(String value);

}
