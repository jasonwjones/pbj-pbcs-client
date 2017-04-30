package com.jasonwjones.pbcs.cli.command;

import com.beust.jcommander.Parameters;
import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.interop.impl.ApplicationSnapshot;

@Parameters(separators = "=", commandDescription = "List files on the server")
public class ListFilesCommand implements PbjCliCommand {

	@Override
	public boolean requiresConnection() {
		return true;
	}

	@Override
	public void execute(PbcsClient client) {
		for (ApplicationSnapshot snapshot : client.listFiles()) {
			System.out.println(snapshot.getName());
		}
	}

}
