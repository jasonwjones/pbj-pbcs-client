package com.jasonwjones.pbcs.cli.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.jasonwjones.pbcs.PbcsClient;

@Parameters(separators = "=", commandDescription = "Download a file")
public class DownloadFileCommand implements PbjCliCommand {

	@Parameter(names = "--filename", required = true)
	private String filename;
	
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	@Override
	public boolean requiresConnection() {
		return true;
	}

	@Override
	public void execute(PbcsClient client) {
		client.downloadFile(filename);
	}

}
