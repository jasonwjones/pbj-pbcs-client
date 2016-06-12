package com.jasonwjones.pbcs.test;

import java.util.Properties;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.impl.PbcsConnectionImpl;

public class TestLoginBadCredentials extends AbstractIntegrationTest {

	public static void main(String[] args) {
		try {
			Properties properties = loadLoginProperties();
			properties.setProperty("username", "example@example.com");
			connection = PbcsConnectionImpl.fromProperties(properties); 
			PbcsClient client = new PbcsClientFactory().createClient(connection);
			System.out.println("API: " + client.getApi());
		} catch (Exception e) {
			System.out.println("Error connecting to PBCS: " + e.getMessage());
		}
	}
	
}
