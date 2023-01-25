package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsDimension;
import com.jasonwjones.pbcs.client.PbcsMemberProperties;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.impl.PbcsPlanTypeImpl;

import java.util.Arrays;
import java.util.List;

public class TestGetMemberWithAlias extends AbstractIntegrationTest {

	public static void main(String[] args) {
		PbcsClient client = new PbcsClientFactory().createClient(connection);
		PbcsApplication app = client.getApplication("Vision");

		List<String> dims = Arrays.asList("Account", "Currency", "Entity", "Period", "Product", "Scenario", "Version", "Year");
		PbcsPlanType cube = app.getPlanType("Plan1", true, dims);

		PbcsMemberProperties mgmtRollup = cube.getMemberOrAlias("Management Rollup");
		PbcsMemberProperties hardware = cube.getMemberOrAlias("4110: Hardware");

		System.out.println("Dim: " + mgmtRollup + " dim: " + mgmtRollup.getDimensionName());
		System.out.println("Dim: " + hardware + " dim: " + hardware.getDimensionName());
	}

	private static void printMember(PbcsMemberProperties member, int level) {
		System.out.print(repeat("    ", level));
		System.out.printf("%s (%s) lev %d/%d%n", member.getName(), member.getDataStorage(), member.getGeneration(), member.getLevel());

		for (PbcsMemberProperties child : member.getChildren()) {
			printMember(child, level + 1);
		}
	}

}