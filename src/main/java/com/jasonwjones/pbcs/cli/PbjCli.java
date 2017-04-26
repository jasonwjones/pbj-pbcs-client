package com.jasonwjones.pbcs.cli;

import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.jasonwjones.pbcs.cli.command.EncodePasswordCommand;

/**
 * Provides a simple CLI for interacting with the PBCS REST API
 * 
 * @author jasonwjones
 *
 */
public class PbjCli {

	public static void main(String[] args) {

		JCommander jc = new JCommander();
		EncodePasswordCommand encodePassword = new EncodePasswordCommand();

		jc.addCommand("encode-password", encodePassword);

		try {
			jc.parse(args);
			String command = jc.getParsedCommand();
			if (command != null && !command.isEmpty()) {
				if (command.equals("encode-password")) {
					encodePassword(encodePassword);
				}
			} else {
				jc.usage();
				System.exit(1);
			}
		} catch (ParameterException e) {
			jc.usage();
			System.exit(1);
		}
	}

	private static void encodePassword(EncodePasswordCommand encodePassword) {
		String encoded = Base64.encodeBase64String(encodePassword.getPasswordToEncode().getBytes(Charset.forName("UTF-8")));
		System.out.println("Encoded password: " + encoded);
	}

}
