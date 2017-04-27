package com.jasonwjones.pbcs.cli.command;

import com.beust.jcommander.Parameters;
import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.client.PbcsApplication;

/**
 * List available applications
 * 
 * @author jasonwjones
 *
 */
@Parameters(separators = "=", commandDescription = "List available applications")
public class ListApplicationsCommand implements PbjCliCommand {

	public void execute(PbcsClient client) {
		for (PbcsApplication application : client.getApplications()) {
			System.out.println(application.getName());
		}		
	}

	public boolean requiresConnection() {
		return true;
	}

}
