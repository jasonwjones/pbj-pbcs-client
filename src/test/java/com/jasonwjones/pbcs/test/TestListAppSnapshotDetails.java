package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsPlanningClient;

public class TestListAppSnapshotDetails extends AbstractIntegrationTest {

	public static void main(String[] args) {
		PbcsPlanningClient client = new PbcsClientFactory().createClient(connection);
		//ApplicationSnapshotInfo info = client.getSnapshotDetails("MDP_Demo_3.24.17");
		// below stopped working after 2.0.0 refactor to remove interop
		//ApplicationSnapshotInfo info = client.getSnapshotDetails("TEST.md");
		//client.uploadFile("TEST.md");
	}

}