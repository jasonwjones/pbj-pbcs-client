package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsMember;

public class TestAddMember extends AbstractIntegrationTest {

	public static void main(String[] args) {
		PbcsClient client = new PbcsClientFactory().createClient(connection);
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