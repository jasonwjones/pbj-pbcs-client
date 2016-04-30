package com.jasonwjones.pbcs;

import com.jasonwjones.pbcs.client.PbcsConnection;
import com.jasonwjones.pbcs.client.PbcsServiceConfiguration;
import com.jasonwjones.pbcs.client.impl.PbcsConnectionImpl;
import com.jasonwjones.pbcs.client.impl.PbcsServiceConfigurationImpl;

public class PbcsClientFactory {

	/**
	 * Creates a new PbcsClient instances using the supplied conncetion details.
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
		return new PbcsClientImpl(connection, serviceConfiguration);
	}

	/**
	 * Convenience method for passing individual connection parameters instead
	 * of having to construct a {@link PbcsConnection} object. Interally just
	 * creates a connection object and calls the other createClient() method.
	 * 
	 * @param server the PBCS server name (just a server, not a scheme, port, or
	 *            path)
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
	public PbcsServiceConfiguration createDefaultServiceConfiguration() {
		PbcsServiceConfigurationImpl sc = new PbcsServiceConfigurationImpl();
		sc.setScheme("https");
		sc.setPort(443);
		sc.setPlanningApiVersion("v3");
		sc.setPlanningRestApiPath("/HyperionPlanning/rest/");
		sc.setInteropApiVersion("11.1.2.3.600");
		sc.setInteropRestApiPath("/interop/rest/");
		return sc;
	}

}
