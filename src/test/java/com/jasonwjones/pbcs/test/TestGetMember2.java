package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsMemberProperties;
import com.jasonwjones.pbcs.client.impl.PlanTypeConfigurationImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class TestGetMember2 extends AbstractIntegrationTest {

	private static final Logger logger = LoggerFactory.getLogger(TestGetMember2.class);

	public static void main(String[] args) {
		PbcsClient client = new PbcsClientFactory().createClient(connection);

		logger.info("Applications: {}", client.getApplications());

		PlanTypeConfigurationImpl planTypeConfiguration = new PlanTypeConfigurationImpl();
		planTypeConfiguration.setName("Plan1");
		planTypeConfiguration.setExplicitDimensions(Arrays.asList("Account", "Currency", "Entity", "Period", "Scenario", "Version", "Year"));

		PbcsMemberProperties member = client
				.getApplication("Vision")
				.getPlanType(planTypeConfiguration)
				// 4110: Hardware
				.getMember("Operating Expenses");

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