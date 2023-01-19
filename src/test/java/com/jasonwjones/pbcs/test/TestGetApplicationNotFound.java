package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsDimension;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TestGetApplicationNotFound extends AbstractIntegrationTest {

	private static final Logger logger = LoggerFactory.getLogger(TestGetApplicationNotFound.class);

	public static void main(String[] args) {

		PbcsClient client = new PbcsClientFactory().createClient(connection);

		PbcsApplication vision = client.getApplication("VisionX");
		System.out.println("Has " + vision.getPlanTypes().size() + " cubes");

	}

}