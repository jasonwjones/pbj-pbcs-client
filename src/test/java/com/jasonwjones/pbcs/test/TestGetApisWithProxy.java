package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class TestGetApisWithProxy extends AbstractIntegrationTest {

	public static void main(String[] args) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {

		// default mitm proxy
		HttpHost proxy = new HttpHost("localhost", 8080);

		RequestConfig requestConfig = RequestConfig.custom()
				.setProxy(proxy)
				.build();

		HttpClient httpClient = HttpClients.custom()
				.setDefaultRequestConfig(requestConfig)
				.setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build())
				//.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
				.build();

		try {
			PbcsClient client = new PbcsClientFactory(httpClient).createClient(connection);
			System.out.println("API: " + client.getApi());
			System.out.println("Num apps: " + client.getApplications().size());
		} catch (Exception e) {
			System.out.println("Error connecting to PBCS: " + e.getMessage());
		}
	}

}