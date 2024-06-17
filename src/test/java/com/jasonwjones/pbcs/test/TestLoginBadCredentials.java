package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsPlanningClient;
import com.jasonwjones.pbcs.client.impl.PbcsConnectionImpl;

import java.util.Properties;

public class TestLoginBadCredentials extends AbstractIntegrationTest {

	public static void main(String[] args) {
		try {
			Properties properties = loadLoginProperties();
			properties.setProperty("username", "example@example.com");
			connection = PbcsConnectionImpl.fromProperties(properties);
			PbcsPlanningClient client = new PbcsClientFactory().createClient(connection);
			System.out.println("API: " + client.getApi());
		} catch (Exception e) {
			System.out.println("Error class: " + e.getClass().getCanonicalName());
			System.out.println("Error connecting to PBCS: " + e.getMessage());
		}
	}

}