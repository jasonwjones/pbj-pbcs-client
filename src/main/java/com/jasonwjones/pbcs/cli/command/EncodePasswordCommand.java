package com.jasonwjones.pbcs.cli.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(separators = "=", commandDescription = "Encode a password for use in a connection")
public class EncodePasswordCommand {

	@Parameter(names = "--plain-password", required = true)
	private String passwordToEncode;

	public String getPasswordToEncode() {
		return passwordToEncode;
	}

	public void setPasswordToEncode(String passwordToEncode) {
		this.passwordToEncode = passwordToEncode;
	}
	
}
