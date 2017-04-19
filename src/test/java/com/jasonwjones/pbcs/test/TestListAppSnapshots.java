package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.interop.impl.ApplicationSnapshot;

public class TestListAppSnapshots extends AbstractIntegrationTest {

	public static void main(String[] args) {
		PbcsClient client = new PbcsClientFactory().createClient(connection); 
		for (ApplicationSnapshot snapshot : client.listFiles()) {
			//if (snapshot.getType().equals(PbcsClient.LCM)) {
				System.out.println("Snapshot: " + snapshot);
			//}
		}
	}
		
}
