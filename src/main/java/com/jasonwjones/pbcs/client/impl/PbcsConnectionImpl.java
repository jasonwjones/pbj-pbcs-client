package com.jasonwjones.pbcs.client.impl;

import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
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
		this(server, identityDomain, username);
		Assert.hasText(password, "Password may not be empty");
		this.password = password;
	}

	/**
	 * Builds a new PBCS Connection object. None of the fields may be null or
	 * otherwise empty, otherwise an IllegalArgumentException will be thrown.
	 *
	 * <p>
	 * This overload constructor is provided as a convenience and meant to be
	 * used immediately afterwith with the {@link #withBase64Password(String)}
	 * method to set the password.
	 *
	 * @param server the server, not including http and not containing anything
	 *            after the TLD
	 * @param identityDomain the identity domain for the user
	 * @param username the username to connect with
	 */
	public PbcsConnectionImpl(String server, String identityDomain, String username) {
		Assert.hasText(server, "Server name may not be empty");
		Assert.hasText(username, "Username may not be empty");
		this.server = server;
		this.identityDomain = identityDomain;
		this.username = username;
	}

	/**
	 * Returns this connection object updated with a new password that is
	 * provided as a string with Base64 encoding. This is not meant to provide
	 * robust security, but rather to allow for putting passwords into other
	 * scripts/code that might otherwise be exposed via a screenshot or during
	 * editing (like a Jython script in ODI/FDMEE).
	 *
	 * @param encodedPassword the Base64 (UTF-8) encoded password.
	 * @return an updated connection object with the password set to the decoded
	 *         value of the given string
	 */
	public PbcsConnectionImpl withBase64Password(String encodedPassword) {
		this.password = new String(Base64.decodeBase64(encodedPassword));
		return this;
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
	public boolean isToken() {
		return false;
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