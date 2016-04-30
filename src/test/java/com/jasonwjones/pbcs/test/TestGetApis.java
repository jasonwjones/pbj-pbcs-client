package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;

public class TestGetApis extends AbstractIntegrationTest {

	public static void main(String[] args) {
		try {
			PbcsClient client = new PbcsClientFactory().createClient(connection);
			System.out.println("API: " + client.getApi());
		} catch (Exception e) {
			System.out.println("Error connecting to PBCS: " + e.getMessage());
		}
	}
	
}
