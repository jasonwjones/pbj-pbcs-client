package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsPlanningClient;
import org.springframework.http.client.JdkClientHttpRequestFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


public class TestGetApisWithProxy extends AbstractIntegrationTest {

	public static void main(String[] args) throws NoSuchAlgorithmException, KeyManagementException {

        // //https://stackoverflow.com/questions/52988677/allow-insecure-https-connection-for-java-jdk-11-httpclient
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new SecureRandom());

        HttpClient httpClient = HttpClient.newBuilder()
                // default mitm proxy
                .proxy(ProxySelector.of(new InetSocketAddress("localhost", 8080)))
                .sslContext(sslContext)
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);

		try {
			PbcsPlanningClient client = new PbcsClientFactory(requestFactory).createClient(connection);
			System.out.println("API: " + client.getApi());
			System.out.println("Num apps: " + client.getApplications().size());
		} catch (Exception e) {
			System.out.println("Error connecting to PBCS: " + e.getMessage());
		}
	}

    private static final TrustManager[] trustAllCerts = new TrustManager[] {

            new X509TrustManager() {

                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    // do nothing
                }
                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    // do nothing
                }

            }
    };

}