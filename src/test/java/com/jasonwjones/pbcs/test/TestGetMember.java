package com.jasonwjones.pbcs.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsMemberProperties;

public class TestGetMember extends AbstractIntegrationTest {

	private static final Logger logger = LoggerFactory.getLogger(TestGetMember.class);

	public static void main(String[] args) {
		PbcsClient client = new PbcsClientFactory().createClient(connection);

		logger.info("Applications: {}", client.getApplications());

		PbcsMemberProperties member = client
				.getApplication("Vision")
				.getMember("Account", "Cash from Current Operations");
				//.getMember("Period", "Q1");
				//.getMember("Scenario", "Scenario");

		logger.info("Dimension: {}", member.getDimensionName());

		printMember(member, 0);
	}


	private static void printMember(PbcsMemberProperties member, int level) {
		System.out.print(repeat("    ", level));
		System.out.printf("%s (%s) %n", member.getName(), member.getDataStorage());

		for (PbcsMemberProperties child : member.getChildren()) {
			printMember(child, level + 1);
		}
	}

}