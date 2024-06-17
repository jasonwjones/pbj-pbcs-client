package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.client.PbcsPlanningClient;

public class TestAddMember extends AbstractIntegrationTest {

	public static void main(String[] args) {
		PbcsPlanningClient client = new PbcsClientFactory().createClient(connection);
		PbcsMember member = client.getApplication(appName).addMember("Entity", "North America", "Enterprise Global");
		printMember(member, 0);
	}

	private static void printMember(PbcsMember member, int level) {
		System.out.print(repeat("    ", level));
		System.out.println(member.getName());
		for (PbcsMember child : member.getChildren()) {
			printMember(child, level + 1);
		}
	}

}