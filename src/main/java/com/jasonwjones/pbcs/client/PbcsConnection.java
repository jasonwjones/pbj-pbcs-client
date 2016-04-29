package com.jasonwjones.pbcs.client;

/**
 * Basic details for a connection to the PBCS API.
 * 
 * @author Jason Jones
 *
 */
public interface PbcsConnection {

	/**
	 * Returns the server name, which does not contain the scheme or anything
	 * after the TLD suffix. For example:
	 * our-company-pbcs-server.pbcs.us2.oraclecloud.com
	 * 
	 * @return the PBCS server name
	 */
	public String getServer();

	/**
	 * The identity domain associated with the given user
	 * 
	 * @return the user's identity domain
	 */
	public String getIdentityDomain();

	/**
	 * The user name used to connect to the PBCS instance
	 * 
	 * @return the PBCS user name
	 */
	public String getUsername();

	/**
	 * The password for the user
	 * 
	 * @return the password for the user
	 */
	public String getPassword();

}
