package com.jasonwjones.pbcs;

import com.jasonwjones.pbcs.client.PbcsConnection;
import com.jasonwjones.pbcs.client.PbcsPlanningClient;
import com.jasonwjones.pbcs.client.PbcsServiceConfiguration;
import com.jasonwjones.pbcs.client.impl.*;
import com.jasonwjones.pbcs.client.impl.interceptors.BasicCredentialsInterceptor;
import com.jasonwjones.pbcs.client.impl.interceptors.RefreshableTokenInterceptor;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Builds a {@link PbcsClient} object to interact with the PBCS/EPM Cloud REST API.
 */
public class PbcsClientFactory {

	private final ClientHttpRequestFactory clientHttpRequestFactory;

	/**
	 * Builds a PBCS client factory with a default HTTP Client. For more control over the client (such as to use a
	 * proxy, see {@link PbcsClientFactory#PbcsClientFactory(HttpClient)}). By default, the HTTP client that is
	 * constructed will use the system settings, such as <code>https.proxyHost</code> and <code>https.proxyPort</code>.
	 *
	 * <p>You can therefore use these settings to globally configure the HTTP client to use a system proxy. Note that if
	 * you get PKIX exceptions, it may indicate that your Java <code>cacerts</code> file needs to have the certificate
	 * of the proxy server installed.
	 */
	public PbcsClientFactory() {
		this(HttpClients.createSystem());
	}

	/**
	 * Builds a PBCS client factory using the given HTTP client.
	 *
	 * <p>If's possible, if inadvisable, to construct an HTTP client using this method that will disregard potential
	 * certificate validation steps. For example, a custom client could be constructed as follows:
	 *
	 * <pre>{@code
	 *
	 * 		HttpHost proxy = new HttpHost("localhost", 8080);
	 *
	 * 		RequestConfig requestConfig = RequestConfig.custom()
	 * 				.setProxy(proxy)
	 * 				.build();
	 *
	 * 		HttpClient httpClient = HttpClients.custom()
	 * 				.setDefaultRequestConfig(requestConfig)
	 * 				.setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build())
	 * 				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE) // not strictly needed given the above
	 * 				.build();
	 *
 	 * 		PbcsClient client = new PbcsClientFactory(httpClient).createClient(connection);
	 * }</pre>
	 *
	 * @param httpClient the HTTP client to use
	 */
	public PbcsClientFactory(HttpClient httpClient) {
		HttpComponentsClientHttpRequestFactory componentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		this.clientHttpRequestFactory = new BufferingClientHttpRequestFactory(componentsClientHttpRequestFactory);
	}

	/**
	 * Creates a new PbcsClient instances using the supplied connection details.
	 * A default service configuration will be created (default HTTP protocol,
	 * REST endpoints, etc.). If customization is needed (for example, to
	 * specify a particular API version), then the other createClient method
	 * should be used.
	 *
	 * @param connection a connection details object
	 * @return a new PBCS client instance
	 */
	public PbcsClient createClient(PbcsConnection connection) {
		PbcsServiceConfiguration config = createDefaultServiceConfiguration();
		return createClient(connection, config);
	}

	/**
	 * Creates a new client instance with supplied connection and service
	 * configuration.
	 *
	 * @param connection the connection details
	 * @param serviceConfiguration the configuration details
	 * @return a new PBCS client instance
	 */
	public PbcsClient createClient(PbcsConnection connection, PbcsServiceConfiguration serviceConfiguration) {
		RestContext restContext = createRestContext(serviceConfiguration, connection);
		return new PbcsClientImpl(restContext, connection, serviceConfiguration);
	}

	@Deprecated
	public PbcsPlanningClient createPlanningClient(PbcsConnection connection) {
		return new PbcsPlanningClientImpl(createRestContext(createDefaultServiceConfiguration(), connection), connection.getServer(), false);
	}

	/**
	 * Convenience method for passing individual connection parameters instead
	 * of having to construct a {@link PbcsConnection} object. Internally just
	 * creates a connection object and calls the other createClient() method.
	 *
	 * @param server the PBCS server name (just a server, not a scheme, port, or path)
	 * @param identityDomain the identity domain
	 * @param username the username
	 * @param password the password
	 * @return a PbcsClient constructed with the given parameters
	 * @deprecated use {@link #createClient(PbcsConnection)} or {@link #createClient(PbcsConnection, PbcsServiceConfiguration)} instead
	 */
	@Deprecated
	public PbcsClient createClient(String server, String identityDomain, String username, String password) {
		return createClient(new PbcsConnectionImpl(server, identityDomain, username, password));
	}

	/**
	 * Builds and returns a default service configuration, namely with the
	 * following are true: the scheme is https, the port is 443 (default HTTPS
	 * port), the API version for Planning is "v3", and the endpoint path for
	 * the API is /HyperionPlanning/rest/. These values should almost always be
	 * valid, but there may come a point where one of them needs to change, most
	 * likely the Planning API version, if/when it gets bumped to v4.
	 *
	 * <p>
	 * Users needing advanced control over the configuration of their service
	 * configuration will want to instantiate their own, such as using the
	 * {@link PbcsServiceConfigurationImpl} class or rolling their own.
	 *
	 * @return a default service configuration
	 */
	public PbcsServiceConfigurationImpl createDefaultServiceConfiguration() {
		PbcsServiceConfigurationImpl sc = new PbcsServiceConfigurationImpl();
		sc.setScheme("https");
		sc.setPlanningApiVersion("v3");
		sc.setPlanningRestApiPath("/HyperionPlanning/rest/");
		sc.setInteropApiVersion("11.1.2.3.600");
		sc.setInteropRestApiPath("/interop/rest/");

		// Might be something like https://example.pbcs.us2.oraclecloud.com/aif/rest/V1/applications/{APPNAME}
		sc.setAifRestApiPath("/aif/rest/");
		sc.setAifRestApiVersion("V1");
		return sc;
	}

	protected RestContext createRestContext(PbcsServiceConfiguration serviceConfiguration, PbcsConnection connection) {
		Objects.requireNonNull(connection.getServer(), "server");
		if (connection.getServer().contains("/")) {
			throw new IllegalArgumentException("Server should not contain protocol, path, or any forward slashes");
		}
		String baseUrl = serviceConfiguration.getScheme() + "://" + connection.getServer() + serviceConfiguration.getPlanningRestApiPath() + serviceConfiguration.getPlanningApiVersion() + "/";

		RestTemplate restTemplate = getRestTemplate(connection);
		String aifBaseUrl = serviceConfiguration.getScheme() + "://" + connection.getServer() + serviceConfiguration.getAifRestApiPath() + serviceConfiguration.getAifRestApiVersion();
		return new RestContext(restTemplate, baseUrl, aifBaseUrl);
	}

	private RestTemplate getRestTemplate(PbcsConnection connection) {
		RestTemplate restTemplate = !connection.isToken() ? new RestTemplate(clientHttpRequestFactory) : new RestTemplate();
		restTemplate.setErrorHandler(new MyResponseErrorHandler());

		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();

		if (connection.isToken()) {
			RefreshableTokenInterceptor refreshableTokenInterceptor = new RefreshableTokenInterceptor(connection);
			interceptors.add(refreshableTokenInterceptor);
		} else {
			BasicCredentialsInterceptor basicCredentialsInterceptor = new BasicCredentialsInterceptor(connection);
			interceptors.add(basicCredentialsInterceptor);
		}

		restTemplate.setInterceptors(interceptors);
		return restTemplate;
	}

}