package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.PbcsPlanningClient;
import com.jasonwjones.pbcs.client.impl.PlanTypeConfigurationImpl;

import java.util.Arrays;
import java.util.List;

public class TestGetMemberWithAlias extends AbstractIntegrationTest {

	public static void main(String[] args) {
		PbcsPlanningClient client = new PbcsClientFactory().createClient(connection);
		PbcsApplication app = client.getApplication("Vision");

		List<String> dims = Arrays.asList("Account", "Currency", "Entity", "Period", "Product", "Scenario", "Version", "Year");
		PbcsApplication.PlanTypeConfiguration configuration = new PlanTypeConfigurationImpl.Builder("Plan1")
				.skipCheck()
				.dimensions(dims)
				.build();
		PbcsPlanType cube = app.getPlanType(configuration);

		PbcsMember mgmtRollup = cube.getMemberOrAlias("Management Rollup");
		PbcsMember hardware = cube.getMemberOrAlias("4110: Hardware");

		System.out.println("Dim: " + mgmtRollup + " dim: " + mgmtRollup.getDimensionName());
		System.out.println("Dim: " + hardware + " dim: " + hardware.getDimensionName());
	}

	private static void printMember(PbcsMember member, int level) {
		System.out.print(repeat("    ", level));
		System.out.printf("%s (%s) lev %d/%d%n", member.getName(), member.getDataStorage(), member.getGeneration(), member.getLevel());

		for (PbcsMember child : member.getChildren()) {
			printMember(child, level + 1);
		}
	}

}