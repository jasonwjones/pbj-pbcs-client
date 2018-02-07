package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.interop.impl.ApplicationSnapshotInfo;

public class TestListAppSnapshotDetails extends AbstractIntegrationTest {

	public static void main(String[] args) {
		PbcsClient client = new PbcsClientFactory().createClient(connection);
		//ApplicationSnapshotInfo info = client.getSnapshotDetails("MDP_Demo_3.24.17");
		ApplicationSnapshotInfo info = client.getSnapshotDetails("TEST.md");
		//client.uploadFile("TEST.md");
	}
		
}
