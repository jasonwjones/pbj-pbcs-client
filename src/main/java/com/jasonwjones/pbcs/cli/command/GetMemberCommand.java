package com.jasonwjones.pbcs.cli.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.client.PbcsMemberProperties;

/**
 * Fetches information on a particular member, given the app/dimension/member
 * name
 * 
 * @author jasonwjones
 *
 */
@Parameters(separators = "=", commandDescription = "Get information on a member in an application")
public class GetMemberCommand implements PbjCliCommand {

	@Parameter(names = "--application", required = true)
	private String application;

	@Parameter(names = "--dimension", required = true)
	private String dimension;

	@Parameter(names = "--member", required = true)
	private String member;

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getDimension() {
		return dimension;
	}

	public void setDimension(String dimension) {
		this.dimension = dimension;
	}

	public String getMember() {
		return member;
	}

	public void setMember(String member) {
		this.member = member;
	}

	public void execute(PbcsClient client) {
		PbcsMemberProperties mp = client.getApplication(application).getMember(dimension, member);
		String template = "%20s: %s%n";
		System.out.printf(template, "Parent Name", mp.getParentName());
		System.out.printf(template, "Data Storage", mp.getDataStorage());
	}

	public boolean requiresConnection() {
		return true;
	}

}
