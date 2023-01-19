package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.api.v3.SubstitutionVariable;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.impl.PbcsApplicationImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class TestGetSubstitutionVariablesPlanType extends AbstractIntegrationTest {

	private static final Logger logger = LoggerFactory.getLogger(TestGetSubstitutionVariablesPlanType.class);

	public static void main(String[] args) {
		PbcsClient client = new PbcsClientFactory().createClient(connection);
		PbcsApplication app = client.getApplication("Vision");

		for (PbcsPlanType planType : app.getPlanTypes()) {
			logger.info("Sub vars in {}", planType);
			for (SubstitutionVariable variable : planType.getSubstitutionVariables()) {
				logger.info(" Var: {}", variable);
			}
		}

	}

}