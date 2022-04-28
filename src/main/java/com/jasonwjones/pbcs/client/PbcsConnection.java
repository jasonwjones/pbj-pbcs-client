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
	 * after the TLD suffix, such as <code>our-company-pbcs-server.pbcs.us2.oraclecloud.com</code>.
	 *
	 * @return the PBCS server name
	 */
	String getServer();

	/**
	 * The identity domain associated with the given user
	 *
	 * @return the user's identity domain
	 */
	String getIdentityDomain();

	/**
	 * The username used to connect to the PBCS instance
	 *
	 * @return the PBCS username
	 */
	String getUsername();

	/**
	 * The password for the user
	 *
	 * @return the password for the user
	 */
	String getPassword();

}