package com.jasonwjones.pbcs.interop;

import java.io.File;

/**
 * Get versions:
 * 		%s/interop/rest
 * Get specific version:
 * 		%s/interop/rest/{version}
 * 
 * @author jasonwjones
 *
 */
public interface InteropClient {

	// URL url = new URL(String.format("%s/interop/rest/%s/applicationsnapshots/%s/contents", serverUrl, apiVersion, fileName));
	public File downloadFile(String filename);
	
}
