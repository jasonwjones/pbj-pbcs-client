package com.jasonwjones.pbcs.cli.command;

import com.beust.jcommander.Parameters;
import com.jasonwjones.pbcs.PbcsClient;

@Parameters(separators = "=", commandDescription = "Refresh an application")
public class RefreshApplicationCommand extends AbstractApplicationCommand {

	@Override
	public void execute(PbcsClient client) {
		client.getApplication(getApplication()).refreshCube();
	}

}
