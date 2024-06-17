package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.api.v3.UserPreferences;
import com.jasonwjones.pbcs.client.PbcsPlanningClient;
import com.jasonwjones.pbcs.client.impl.PbcsApplicationImpl;

public class TestGetUserPreferences extends AbstractIntegrationTest {

	public static void main(String[] args) {
		PbcsPlanningClient client = new PbcsClientFactory().createClient(connection);
		PbcsApplicationImpl app = (PbcsApplicationImpl) client.getApplication("Vision");

		UserPreferences prefs = app.getUserPreferences();
		System.out.println(prefs);
	}

}