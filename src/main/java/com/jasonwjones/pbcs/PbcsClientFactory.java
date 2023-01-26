package com.jasonwjones.pbcs;

import com.jasonwjones.pbcs.client.PbcsConnection;
import com.jasonwjones.pbcs.client.PbcsPlanningClient;
import com.jasonwjones.pbcs.client.PbcsServiceConfiguration;
import com.jasonwjones.pbcs.client.impl.*;
import com.jasonwjones.pbcs.client.impl.interceptors.RefreshableTokenInterceptor;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class PbcsClientFactory {

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
	 */
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
		sc.setPort(443);
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
		String baseUrl = serviceConfiguration.getScheme() + "://" + connection.getServer() + serviceConfiguration.getPlanningRestApiPath() + serviceConfiguration.getPlanningApiVersion() + "/";

		RestTemplate restTemplate = !connection.isToken() ? new RestTemplate(serviceConfiguration.createRequestFactory(connection)) : new RestTemplate();
		restTemplate.setErrorHandler(new MyResponseErrorHandler());

		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();

		if (connection.isToken()) {
			RefreshableTokenInterceptor refreshableTokenInterceptor = new RefreshableTokenInterceptor(connection);
			interceptors.add(refreshableTokenInterceptor);
		}

		restTemplate.setInterceptors(interceptors);
		return new RestContext(restTemplate, baseUrl);
	}

}