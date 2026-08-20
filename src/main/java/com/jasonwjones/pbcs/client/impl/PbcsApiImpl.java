package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.api.v3.Api;
import com.jasonwjones.pbcs.client.PbcsApi;

/**
 * Default {@link PbcsApi} implementation, delegating to a deserialized {@link Api} payload.
 */
public class PbcsApiImpl implements PbcsApi {

	private final Api api;

	/**
	 * Constructs an instance wrapping the given API version details.
	 *
	 * @param api the API version details to delegate to
	 */
	public PbcsApiImpl(Api api) {
		this.api = api;
	}

	@Override
	public String getVersion() {
		return api.getVersion();
	}

	@Override
	public String getLifecycle() {
		return api.getLifecycle();
	}

	@Override
	public boolean isLatest() {
		return api.isLatest();
	}

	@Override
	public String toString() {
		return api.toString();
	}

}