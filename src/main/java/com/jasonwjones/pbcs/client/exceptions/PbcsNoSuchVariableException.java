package com.jasonwjones.pbcs.client.exceptions;

@SuppressWarnings("serial")
public class PbcsNoSuchVariableException extends PbcsClientException {

	public PbcsNoSuchVariableException(String application, String variable) { 
		super("Application " + application + " does not have variable named " + variable);
	}

}
