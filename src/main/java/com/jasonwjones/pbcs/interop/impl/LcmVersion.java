package com.jasonwjones.pbcs.interop.impl;

import java.util.List;

import com.jasonwjones.pbcs.api.v3.HypermediaLink;

/**
 * Maps a single LCM API version entry as returned by the interop service.
 */
public class LcmVersion {

	private boolean latest;

	private List<HypermediaLink> links;

	private String version;

	private String lifecycle;

	/**
	 * Constructs an empty instance for deserialization.
	 */
	public LcmVersion() {
	}

}
