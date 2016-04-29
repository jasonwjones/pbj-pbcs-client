package com.jasonwjones.pbcs.test;

import java.io.FileReader;
import java.util.Properties;

import com.jasonwjones.pbcs.client.PbcsConnection;
import com.jasonwjones.pbcs.client.impl.PbcsConnectionImpl;

public abstract class AbstractIntegrationTest {

	private static final String PROPS = System.getProperty("user.home") +"/pbcs-client.properties";
	
	protected static final String server;
	protected static final String identityDomain;
	protected static final String username;
	protected static final String password;
	protected static final String appName;
	
	protected static PbcsConnection connection;
	
	static {
		Properties properties = new Properties();
		try {
			properties.load(new FileReader(PROPS));
		} catch (Exception e) {
			System.out.println("Couldn't load properties...");
			System.out.println("Looking for a file at " + PROPS + " containing server/domain/user/pw");
		}
		server = properties.getProperty("server");
		identityDomain = properties.getProperty("identityDomain");
		username = properties.getProperty("username");
		password = properties.getProperty("password");
		appName = properties.getProperty("appName");
		connection = PbcsConnectionImpl.fromProperties(properties);
	}
			
	public static String repeat(String text, int times) {
		StringBuilder sb = new StringBuilder(times * text.length());
		for (int i = 0; i < times; i++) {
			sb.append(text);
		}
		return sb.toString();
	}
	
}
