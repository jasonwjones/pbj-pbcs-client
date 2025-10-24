package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsPlanningClient;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.ssl.SSLContextBuilder;

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
                // TODO: rework; no longer exists in httpclient5
				//.setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build())
				//.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
				.build();

		try {
			PbcsPlanningClient client = new PbcsClientFactory(httpClient).createClient(connection);
			System.out.println("API: " + client.getApi());
			System.out.println("Num apps: " + client.getApplications().size());
		} catch (Exception e) {
			System.out.println("Error connecting to PBCS: " + e.getMessage());
		}
	}

}