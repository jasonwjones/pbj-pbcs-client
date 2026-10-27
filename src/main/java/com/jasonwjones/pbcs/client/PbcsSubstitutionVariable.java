package com.jasonwjones.pbcs.client;

/**
 * A substitution variable: a named value that business rules, forms and reports can refer to (as
 * <code>&amp;CurYear</code>, for example) in place of a hard-coded member.
 * <p>
 * Every variable has a name and a value, and is defined at a {@link #getScope() scope}. EPM Cloud keeps variables for
 * a whole application and for a single plan type, so every variable this library returns is at least a
 * {@link PbcsApplicationSubstitutionVariable}, and a plan type's variables are
 * {@link PbcsPlanTypeSubstitutionVariable}s. Check {@link #getScope()} and cast to the narrower type for its plan
 * type. This type itself doesn't assume an application, which leaves room for a server scope: on-premises Essbase has
 * one, and EPM Cloud's REST API doesn't expose it.
 * <p>
 * Instances are immutable snapshots of the server's values. To change a variable, pass a copy from
 * {@link #withValue(String)} to {@link PbcsApplication#updateVariables(java.util.Collection)}, or use
 * {@link PbcsApplication#updateVariable(String, String)} or {@link PbcsPlanType#updateVariable(String, String)}.
 *
 * @since 3.1.0
 */
public interface PbcsSubstitutionVariable extends PbcsObject {

	/**
	 * Gets the value of the variable as of when it was read from the server.
	 *
	 * @return the value, such as <code>FY26</code>
	 */
	String getValue();

	/**
	 * Gets the scope the variable is defined at, which says which narrower type it is.
	 *
	 * @return the scope
	 */
	PbcsSubstitutionVariableScope getScope();

	/**
	 * Gets the client for the server the variable belongs to.
	 *
	 * @return the client
	 */
	PbcsPlanningClient getClient();

	/**
	 * Makes a copy of this variable, at the same scope, with a different value. Nothing changes on the server until
	 * the copy is passed to {@link PbcsApplication#updateVariables(java.util.Collection)}.
	 *
	 * @param value the new value
	 * @return the copy
	 */
	PbcsSubstitutionVariable withValue(String value);

	/**
	 * Gets the object type of every substitution variable.
	 *
	 * @return {@link PbcsObjectType#SUBSTITUTION_VARIABLE}
	 */
	@Override
	default PbcsObjectType getObjectType() {
		return PbcsObjectType.SUBSTITUTION_VARIABLE;
	}

}
