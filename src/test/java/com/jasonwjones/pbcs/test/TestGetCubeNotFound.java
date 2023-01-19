package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestGetCubeNotFound extends AbstractIntegrationTest {

	private static final Logger logger = LoggerFactory.getLogger(TestGetCubeNotFound.class);

	public static void main(String[] args) {

		PbcsClient client = new PbcsClientFactory().createClient(connection);

		PbcsApplication vision = client.getApplication("Vision");
		PbcsPlanType plan1 = vision.getPlanType("No such");
		System.out.println(plan1.getName());
	}

}