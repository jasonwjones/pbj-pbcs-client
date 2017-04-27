package com.jasonwjones.pbcs.client.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class PbcsConnectionImplTest {

	private PbcsConnectionImpl conn;

	private String decodedPassword = "mypassword";

	private String encodedPassword = "bXlwYXNzd29yZA==";

	@Before
	public void setUp() throws Exception {
		conn = new PbcsConnectionImpl("serveR", "identitydomain", "admin", "somepass");
	}

	/**
	 * Ensure that setting password with a Base64 password is properly encoded
	 */
	@Test
	public void testWithBase64Password() {
		PbcsConnectionImpl encodedPasswordConncetion = conn.withBase64Password(encodedPassword);
		assertEquals(encodedPasswordConncetion.getPassword(), decodedPassword);
	}

}
