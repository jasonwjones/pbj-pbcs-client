package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.*;
import com.jasonwjones.pbcs.client.impl.PlanTypeConfigurationImpl;

import java.util.Arrays;
import java.util.List;

public class TestGetMemberExplicitDimensions extends AbstractIntegrationTest {

	public static void main(String[] args) {
		PbcsClient client = new PbcsClientFactory().createClient(connection);
		PbcsApplication app = client.getApplication("Vision");

		List<String> dims = Arrays.asList("Account", "Currency", "Entity", "Period", "Product", "Scenario", "Version", "Year");
		PbcsApplication.PlanTypeConfiguration configuration = new PlanTypeConfigurationImpl.Builder("Plan1")
				.skipCheck()
				.dimensions(dims)
				.build();
		PbcsPlanType cube = app.getPlanType(configuration);

		for (PbcsDimension dimension : cube.getDimensions()) {
			System.out.println("Dim: " + dimension.getName());
			PbcsMember memberProperties = dimension.getRoot();
			printMember(memberProperties, 0);
		}
		System.out.println();
	}

	private static void printMember(PbcsMember member, int level) {
		System.out.print(repeat("    ", level));
		System.out.printf("%s (%s) lev %d/%d%n", member.getName(), member.getDataStorage(), member.getGeneration(), member.getLevel());

		for (PbcsMember child : member.getChildren()) {
			printMember(child, level + 1);
		}
	}

}