package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.api.v3.UserPreferences;
import com.jasonwjones.pbcs.client.impl.PbcsApplicationImpl;

public class TestGetUserPreferences extends AbstractIntegrationTest {

	public static void main(String[] args) {
		PbcsClient client = new PbcsClientFactory().createClient(connection);
		PbcsApplicationImpl app = (PbcsApplicationImpl) client.getApplication("Vision");

		UserPreferences prefs = app.getUserPreferences();
		System.out.println(prefs);
	}

}