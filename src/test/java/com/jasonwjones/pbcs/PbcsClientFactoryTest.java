package com.jasonwjones.pbcs;

import com.jasonwjones.pbcs.client.PbcsConnection;
import com.jasonwjones.pbcs.client.PbcsPlanningClient;
import com.jasonwjones.pbcs.client.exceptions.PbcsInvalidCredentialsException;
import com.jasonwjones.pbcs.client.impl.PbcsConnectionImpl;
import com.jasonwjones.pbcs.util.ConnectionUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

public class PbcsClientFactoryTest {

    private PbcsClientFactory clientFactory;

    @Before
    public void setUp() {
        clientFactory = new PbcsClientFactory();
    }

    @Ignore
    @Test
    public void createClient() {
        PbcsConnection connection = new PbcsConnectionImpl("server", "", "jason", "password");
        PbcsPlanningClient client = clientFactory.createClient(connection);
        assertNotNull(client);
    }

    @Ignore
    @Test(expected = IllegalArgumentException.class)
    public void createClientWithBadServerName() {
        PbcsConnection connection = new PbcsConnectionImpl("https://server/foo", "", "jason", "password");
        clientFactory.createClient(connection);
    }

    @Test
    public void whenLoginWithBadCredentials() {
        PbcsConnection defaultConnection = ConnectionUtils.defaultConnection();
        PbcsConnection connection = new PbcsConnectionImpl(defaultConnection.getServer(), defaultConnection.getIdentityDomain(), "invalid_user@example.com", "invalid_password");
        PbcsClientFactory clientFactory = new PbcsClientFactory();
        assertThrows(PbcsInvalidCredentialsException.class, () -> clientFactory.createClient(connection));
    }

    @Test
    public void whenUseProxy() throws NoSuchAlgorithmException, KeyManagementException {
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
            PbcsConnection connection = ConnectionUtils.defaultConnection();
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