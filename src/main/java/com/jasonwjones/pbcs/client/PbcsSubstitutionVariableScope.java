package com.jasonwjones.pbcs.client;

/**
 * Where a substitution variable is defined, and so which plan types can use it.
 *
 * @since 3.1.0
 */
public enum PbcsSubstitutionVariableScope {

	/**
	 * Defined for the whole application, so every plan type can use it. The REST API marks these with the plan type
	 * <code>ALL</code>. Variables at this scope are {@link PbcsApplicationSubstitutionVariable}s.
	 */
	APPLICATION,

	/**
	 * Defined for one plan type, which alone can use it. Variables at this scope are
	 * {@link PbcsPlanTypeSubstitutionVariable}s.
	 */
	PLAN_TYPE

}
