package com.jasonwjones.pbcs.client.exceptions;

import com.jasonwjones.pbcs.client.PbcsObjectType;

/**
 * Thrown when a requested substitution variable does not exist for the given application or plan type. Since 3.1.0
 * it's a {@link PbcsNoSuchObjectException}, whose {@link #getObjectName() object name} is the variable's name and
 * whose {@link #getObjectType() object type} is {@link PbcsObjectType#SUBSTITUTION_VARIABLE}.
 */
@SuppressWarnings("serial")
public class PbcsNoSuchVariableException extends PbcsNoSuchObjectException {

	/**
	 * Constructs an instance for the given application and variable name.
	 *
	 * @param application the application name
	 * @param variable the substitution variable name
	 */
	public PbcsNoSuchVariableException(String application, String variable) {
		super("Application " + application + " does not have variable named " + variable, variable, PbcsObjectType.SUBSTITUTION_VARIABLE);
	}

	/**
	 * Constructs an instance for a variable missing from a plan type.
	 *
	 * @param application the application name
	 * @param planType the plan type name
	 * @param variable the substitution variable name
	 * @since 3.1.0
	 */
	public PbcsNoSuchVariableException(String application, String planType, String variable) {
		super("Plan type " + planType + " in application " + application + " does not have variable named " + variable, variable, PbcsObjectType.SUBSTITUTION_VARIABLE);
	}

}
