package com.jasonwjones.pbcs.test;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.api.v3.SubstitutionVariable;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.impl.PbcsApplicationImpl;

public class TestGetSubstitutionVariables extends AbstractIntegrationTest {

	private static final Logger logger = LoggerFactory.getLogger(TestGetSubstitutionVariables.class);

	public static void main(String[] args) {
		PbcsClient client = new PbcsClientFactory().createClient(connection);
		PbcsApplication app = client.getApplication("Vision");

		PbcsApplicationImpl app2 = (PbcsApplicationImpl) app;
		Set<SubstitutionVariable> vars = app2.getSubstitutionVariables();

		System.out.println("Count of variables: " + vars.size());
		for (SubstitutionVariable var : vars) {
			logger.info("Variable: {}", var);
		}

	}

}