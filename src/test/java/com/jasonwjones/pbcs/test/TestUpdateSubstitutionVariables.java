package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.api.v3.SubstitutionVariable;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsPlanningClient;

import java.util.Arrays;
import java.util.List;

public class TestUpdateSubstitutionVariables extends AbstractIntegrationTest {

	public static void main(String[] args) {
		PbcsPlanningClient client = new PbcsClientFactory().createClient(connection);
		PbcsApplication app = client.getApplication("MDP_Demo");

		SubstitutionVariable var = new SubstitutionVariable();
		var.setName("TestPd");
		var.setValue("FY15");
		var.setPlanType("Foo");

		List<SubstitutionVariable> vars = Arrays.asList(var);

		app.updateSubstitutionVariables(vars);

//		PbcsApplicationImpl app2 = (PbcsApplicationImpl) app;
//		List<SubstitutionVariable> vars = app2.getSubstitutionVariables();
//
//		System.out.println("Count of variables: " + vars.size());
//		for (SubstitutionVariable var : vars) {
//			System.out.println("Var: " + var.getName());
//		}

	}

}