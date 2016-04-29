package com.jasonwjones.pbcs.interop;

import java.io.File;
import java.util.List;

import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;
import com.jasonwjones.pbcs.interop.impl.ApplicationSnapshot;

/**
 * Main interface for LCM operations.
 * 
 * @author Jason Jones
 *
 */
public interface InteropClient {

	/**
	 * Download a file that has been previously uploaded or otherwise exists
	 * from an export.
	 * 
	 * @param filename the name of the file, such as "export.txt"
	 * @return a File object with a handle to the downloaded file
	 * @throws PbcsClientException if there is an error retrieving the file
	 *             (network, file doesn't exist)
	 */
	public File downloadFile(String filename) throws PbcsClientException;

	/**
	 * Uploads a file to PBCS so that it can be imported.
	 * 
	 * @param filename the local name of the file to upload
	 */
	public void uploadFile(String filename);

	/**
	 * Deletes the file (snapshot) with the given name.
	 * 
	 * @param filename the name of the file to delete
	 */
	public void deleteFile(String filename);

	/**
	 * Return a list of files available on the remote system.
	 * 
	 * @return a list of available files
	 */
	public List<ApplicationSnapshot> listFiles();

	/**
	 * TODO: Implement LCM Export functionality.
	 */
	public void LcmExport();

	/**
	 * TODO: Impleent LCM Import functionality.
	 */
	public void LcmImport();

}
