package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;

public class TestGetApis extends AbstractIntegrationTest {

	public static void main(String[] args) {

		String host = "localhost";
		String port = "8080";
//
//		System.setProperty("http.proxyHost", host);
//		System.setProperty("http.proxyPort", port);

		System.setProperty("https.proxyHost", host);
		System.setProperty("https.proxyPort", port);
//		System.setProperty("com.sun.net.ssl.checkRevocation", "false");

		try {
			PbcsClient client = new PbcsClientFactory().createClient(connection);
			System.out.println("API: " + client.getApi());
		} catch (Exception e) {
			System.out.println("Error connecting to PBCS: " + e.getMessage());
			e.printStackTrace();
		}
	}

}