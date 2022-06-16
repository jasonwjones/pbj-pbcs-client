package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsConnection;

public class TestBadCredentials extends AbstractIntegrationTest {

	public static void main(String[] args) {
		PbcsConnection facade = new ConnectionFacade(connection);
		PbcsClient client = new PbcsClientFactory().createClient(facade);

		System.out.println("Apps:");
		client.getApplications();
	}

	public static class ConnectionFacade implements PbcsConnection {

		private final PbcsConnection connection;

		public ConnectionFacade(PbcsConnection connection) {
			this.connection = connection;
		}

		@Override
		public String getServer() {
			return connection.getServer();
		}

		@Override
		public String getIdentityDomain() {
			return connection.getIdentityDomain();
		}

		@Override
		public String getUsername() {
			return connection.getUsername() + "XX";
		}

		@Override
		public String getPassword() {
			return connection.getPassword() + "XX";
		}

	}

}