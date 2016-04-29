package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.client.PbcsPlanningClient;
import com.jasonwjones.pbcs.client.PbcsMemberProperties;
import com.jasonwjones.pbcs.client.impl.PbcsPlanningClientImpl;

public class TestAddMember extends AbstractIntegrationTest {

	public static void main(String[] args) {
		PbcsPlanningClient client = new PbcsPlanningClientImpl(server, identityDomain, username, password);
		//PbcsMemberProperties member = 
		client.getApplication(appName).addMember("Entity", "North America", "Enterprise Global");
		
		//printMember(member, 0);		
	}
	
	private static void printMember(PbcsMemberProperties member, int level) {
		System.out.print(repeat("    ", level));
		System.out.println(member.getName());
		for (PbcsMemberProperties child : member.getChildren()) {
			printMember(child, level + 1);
		}
	}
	
}
