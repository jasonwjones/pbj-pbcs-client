package com.jasonwjones.pbcs.cli.command;

import com.beust.jcommander.Parameter;
import com.jasonwjones.pbcs.PbcsClient;

// no command annotation since subclasses will implement
public abstract class AbstractApplicationCommand implements PbjCliCommand {

	@Parameter(names = "--filename", required = true)
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
	public abstract void execute(PbcsClient client);

}
