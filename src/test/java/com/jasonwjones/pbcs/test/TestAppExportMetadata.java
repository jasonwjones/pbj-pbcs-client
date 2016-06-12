package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;

public class TestAppExportMetadata extends AbstractIntegrationTest {

	public static void main(String[] args) {
		PbcsClient client = new PbcsClientFactory().createClient(connection); 
		client.getApplication(appName).exportMetadata("ExportProduct", "test.zip");		
	}
		
}
