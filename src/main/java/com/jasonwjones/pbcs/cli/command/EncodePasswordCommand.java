package com.jasonwjones.pbcs.cli.command;

import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.jasonwjones.pbcs.PbcsClient;

@Parameters(separators = "=", commandDescription = "Encode a password for use in a connection")
public class EncodePasswordCommand implements PbjCliCommand {

	@Parameter(names = "--plain-password", required = true)
	private String passwordToEncode;

	public String getPasswordToEncode() {
		return passwordToEncode;
	}

	public void setPasswordToEncode(String passwordToEncode) {
		this.passwordToEncode = passwordToEncode;
	}

	@Override
	public boolean requiresConnection() {
		return false;
	}

	@Override
	public void execute(PbcsClient client) {
		String encoded = Base64.encodeBase64String(getPasswordToEncode().getBytes(Charset.forName("UTF-8")));
		System.out.println("Encoded password: " + encoded); 
	}
	
}
