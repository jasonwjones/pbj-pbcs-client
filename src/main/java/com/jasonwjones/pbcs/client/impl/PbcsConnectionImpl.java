package com.jasonwjones.pbcs.client.impl;

import java.util.Properties;

import org.springframework.util.Assert;

import com.jasonwjones.pbcs.client.PbcsConnection;

public class PbcsConnectionImpl implements PbcsConnection {

	private String server;

	private String identityDomain;

	private String username;

	private String password;

	/**
	 * Builds a new PBCS Connection object. None of the fields may be null or
	 * otherwise empty, otherwise an IllegalArgumentException will be thrown.
	 * 
	 * @param server the server, not including http and not containing anything
	 *            after the TLD
	 * @param identityDomain the identity domain for the user
	 * @param username the username to connect with
	 * @param password the password for the user
	 */
	public PbcsConnectionImpl(String server, String identityDomain, String username, String password) {
		Assert.hasText(server, "Server name may not be empty");
		Assert.hasText(identityDomain, "Identity domain may not be empty");
		Assert.hasText(username, "Username may not be empty");
		Assert.hasText(password, "Password may not be empty");
		this.server = server;
		this.identityDomain = identityDomain;
		this.username = username;
		this.password = password;
	}

	@Override
	public String getServer() {
		return server;
	}

	@Override
	public String getIdentityDomain() {
		return identityDomain;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String toString() {
		return "PbcsConnectionImpl [server=" + server + ", identityDomain=" + identityDomain + ", username=" + username
				+ "]";
	}

	/**
	 * Convenience method that attempts to construct a new user connection
	 * details object from a Properties object that contains the following keys:
	 * server, identityDomain, username, password.
	 * 
	 * <p>
	 * If any of the fields are missing, an exception is thrown
	 * 
	 * @param properties a Properties object that shouold contain the user
	 *            details
	 * @return a new user connection details object based on the keys from the
	 *         given properties
	 */
	public static PbcsConnectionImpl fromProperties(Properties properties) {
		String server = properties.getProperty("server");
		String identityDomain = properties.getProperty("identityDomain");
		String username = properties.getProperty("username");
		String password = properties.getProperty("password");
		return new PbcsConnectionImpl(server, identityDomain, username, password);
	}

}
