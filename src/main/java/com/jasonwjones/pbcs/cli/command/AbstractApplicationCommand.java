package com.jasonwjones.pbcs.cli.command;

import com.beust.jcommander.Parameter;
import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.client.PbcsApplication;

// no command annotation since subclasses will implement
public abstract class AbstractApplicationCommand implements PbjCliCommand {

	@Parameter(names = "--application", required = true)
	private String application;
	
	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	@Override
	public boolean requiresConnection() {
		return true;
	}

	@Override
	public void execute(PbcsClient client) {
		executeForApp(client.getApplication(application));
	}
	
	public abstract void executeForApp(PbcsApplication application);
		
}
