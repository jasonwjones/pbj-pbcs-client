package com.jasonwjones.pbcs.client.exceptions;

/**
 * Thrown when a requested substitution variable does not exist for the given application.
 */
@SuppressWarnings("serial")
public class PbcsNoSuchVariableException extends PbcsClientException {

	/**
	 * Constructs an instance for the given application and variable name.
	 *
	 * @param application the application name
	 * @param variable the substitution variable name
	 */
	public PbcsNoSuchVariableException(String application, String variable) {
		super("Application " + application + " does not have variable named " + variable);
	}

}
