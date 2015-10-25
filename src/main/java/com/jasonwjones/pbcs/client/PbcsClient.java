package com.jasonwjones.pbcs.client;

import java.util.List;

import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;

/**
 * Main interface for interacting with the PBCS service.
 * 
 * @author jasonwjones
 *
 */
public interface PbcsClient {

	public PbcsApi getApi();
	
	/**
	 * Returns a list of applications visible to the user connected with the
	 * API.
	 * 
	 * @return the list of available applications
	 */
	public List<PbcsApplication> getApplications();

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
	public PbcsApplication getApplication(String applicationName) throws PbcsClientException;

}
