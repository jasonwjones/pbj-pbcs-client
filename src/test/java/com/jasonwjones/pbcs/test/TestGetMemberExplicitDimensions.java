package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsDimension;
import com.jasonwjones.pbcs.client.PbcsMemberProperties;
import com.jasonwjones.pbcs.client.PbcsPlanType;

import java.util.Arrays;
import java.util.List;

public class TestGetMemberExplicitDimensions extends AbstractIntegrationTest {

	public static void main(String[] args) {
		PbcsClient client = new PbcsClientFactory().createClient(connection);
		PbcsApplication app = client.getApplication("Vision");

		List<String> dims = Arrays.asList("Account", "Currency", "Entity", "Period", "Scenario", "Version", "Years");
		PbcsPlanType cube = app.getPlanType("Plan1", true, dims);

		for (PbcsDimension dimension : cube.getDimensions()) {
			System.out.println("Dim: " + dimension.getName());
			PbcsMemberProperties memberProperties = dimension.getRoot();
			printMember(memberProperties, 0);
		}
		System.out.println();
	}

	private static void printMember(PbcsMemberProperties member, int level) {
		System.out.print(repeat("    ", level));
		System.out.printf("%s (%s) lev %d/%d%n", member.getName(), member.getDataStorage(), member.getGeneration(), member.getLevel());

		for (PbcsMemberProperties child : member.getChildren()) {
			printMember(child, level + 1);
		}
	}

}