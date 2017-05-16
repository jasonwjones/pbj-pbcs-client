package com.jasonwjones.pbcs.cli.command;

import com.beust.jcommander.Parameters;
import com.jasonwjones.pbcs.client.PbcsApplication;

@Parameters(separators = "=", commandDescription = "Refresh an application")
public class RefreshApplicationCommand extends AbstractApplicationCommand {

	@Override
	public void executeForApp(PbcsApplication application) {
		System.out.println("TODO Running app command");
	}

}
