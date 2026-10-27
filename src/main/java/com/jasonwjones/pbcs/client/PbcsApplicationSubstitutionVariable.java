package com.jasonwjones.pbcs.client;

/**
 * A substitution variable that belongs to an application. A variable whose {@link #getScope() scope} is
 * {@link PbcsSubstitutionVariableScope#APPLICATION} is defined for the whole application, and its parent is the
 * application. A {@link PbcsPlanTypeSubstitutionVariable} also belongs to an application, through its plan type.
 *
 * @since 3.1.0
 */
public interface PbcsApplicationSubstitutionVariable extends PbcsSubstitutionVariable {

	/**
	 * Gets the application the variable belongs to.
	 *
	 * @return the application
	 */
	PbcsApplication getApplication();

	/**
	 * Makes a copy of this variable, at the same scope, with a different value.
	 *
	 * @param value the new value
	 * @return the copy
	 */
	@Override
	PbcsApplicationSubstitutionVariable withValue(String value);

}
