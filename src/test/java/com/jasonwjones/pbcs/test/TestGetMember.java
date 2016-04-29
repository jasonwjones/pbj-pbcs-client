package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsMemberProperties;

public class TestGetMember extends AbstractIntegrationTest {

	public static void main(String[] args) {
		PbcsClient client = new PbcsClientFactory().createClient(connection);
		PbcsMemberProperties member = client.getApplication(appName).getMember("Period", "YearTotal");
		
		printMember(member, 0);		
	}
	
	private static void printMember(PbcsMemberProperties member, int level) {
		System.out.print(repeat("    ", level));
		System.out.println(member.getName());
		for (PbcsMemberProperties child : member.getChildren()) {
			printMember(child, level + 1);
		}
	}
	
}
