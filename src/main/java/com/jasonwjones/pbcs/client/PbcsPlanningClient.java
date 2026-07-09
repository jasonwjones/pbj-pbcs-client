package com.jasonwjones.pbcs.client;

import java.util.List;

import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;

/**
 * Main interface for interacting with the PBCS service.
 *
 * @author jasonwjones
 *
 */
public interface PbcsPlanningClient extends PbcsObject {

	/**
	 * Returns an API object with information about the current endpoint.
	 *
	 * @return API information
	 */
	PbcsApi getApi();

	/**
	 * The name of the server this client is connected to
	 *
	 * @return the server connection name
	 */
	String getServer();

	/**
	 * The username used to connect to the REST API. Will be the signed on username if using native authentication,
	 * otherwise will be the subject (JWT <code>sub</code> attribute) from the authentication token.
	 *
	 * @return the username for this connection
	 */
	String getUserName();

	/**
	 * Returns a list of applications visible to the user connected with the
	 * API.
	 *
	 * @return the list of available applications
	 */
	List<PbcsApplication> getApplications();

	/**
	 * Returns an application with the specific name. Note that internally this
	 * method just pulls the whole list of applications and just filters for the
	 * one you want. It is for convenience purposes.
	 *
	 * @param applicationName the name of the application to return,
	 *            case-sensitive
	 * @return an application object for the application
	 * @throws PbcsClientException if the application is not found
	 */
	PbcsApplication getApplication(String applicationName) throws PbcsClientException;

}