package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.client.PbcsMemberQueryType;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.impl.PlanTypeConfigurationImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class TestMemberQuery extends AbstractIntegrationTest {

	private static final Logger logger = LoggerFactory.getLogger(TestMemberQuery.class);

	public static void main(String[] args) {
		PbcsClient client = new PbcsClientFactory().createClient(connection);

		logger.info("Applications: {}", client.getApplications());

		PlanTypeConfigurationImpl planTypeConfiguration = new PlanTypeConfigurationImpl();
		planTypeConfiguration.setName("Plan1");
		planTypeConfiguration.setExplicitDimensions(Arrays.asList("Account", "Currency", "Entity", "Period", "Scenario", "Version", "Year"));

		PbcsPlanType plan1 = client
				.getApplication("Vision")
				.getPlanType(planTypeConfiguration);

		List<PbcsMember> descendants = plan1.queryMembers("YearTotal", PbcsMemberQueryType.IDESCENDANTS);
		logger.info("Received {} descendants", descendants.size());
		for (PbcsMember member : descendants) {
			logger.info("Mbr: {}", member);
		}

		System.out.println("---");

		List<PbcsMember> children = plan1.queryMembers("YearTotal", PbcsMemberQueryType.ICHILDREN);
		logger.info("Received {} children", descendants.size());
		for (PbcsMember member : children) {
			logger.info("Mbr: {}", member);
		}

		System.out.println("---");
		List<PbcsMember> ancestors = plan1.queryMembers("Jan", PbcsMemberQueryType.IANCESTORS);
		logger.info("Received {} ancestors", ancestors.size());
		for (PbcsMember member : ancestors) {
			logger.info("Ans: {}", member);
		}

	}


	private static void printMember(PbcsMember member, int level) {
		System.out.print(repeat("    ", level));
		System.out.printf("%s (%s) %n", member.getName(), member.getDataStorage());

		for (PbcsMember child : member.getChildren()) {
			printMember(child, level + 1);
		}
	}

}