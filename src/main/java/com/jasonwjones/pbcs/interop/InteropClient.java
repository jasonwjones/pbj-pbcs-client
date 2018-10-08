package com.jasonwjones.pbcs.interop;

import java.io.File;
import java.util.List;

import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;
import com.jasonwjones.pbcs.interop.impl.ApplicationSnapshot;
import com.jasonwjones.pbcs.interop.impl.ApplicationSnapshotInfo;

/**
 * Main interface for LCM operations.
 * 
 * @author Jason Jones
 *
 */
public interface InteropClient {

	public static final String LCM = "LCM";

	public static final String EXTERNAL = "EXTERNAL";

	/**
	 * Download a file that has been previously uploaded or otherwise exists
	 * from an export. Note that it's possible for a file to show on the
	 * application snapshot listing (such as provided by {@link #listFiles()})
	 * but not be able to download that file. This seems to be a quirk in the
	 * REST API such that folders themselves are considered files of type LCM.
	 * 
	 * @param filename the name of the file, such as "export.txt"
	 * @return a File object with a handle to the downloaded file
	 * @throws PbcsClientException if there is an error retrieving the file
	 *             (network, file doesn't exist)
	 */
	public File downloadFile(String filename) throws PbcsClientException;

	public File downloadFile(String filename, String localFilename);
	/**
	 * This is usefull for downloading large files - whole content is streamed to disk
	 * @param filename remote filename in cloud
	 * @return
	 * @throws PbcsClientException
	 */
	public File downloadFileViaStream(String filename) throws PbcsClientException;
	/**
	 * This is usefull for downloading large files - whole content is streamed to disk
	 * @param filename remote filename in cloud
	 * @param localFilename filename on disk
	 * @return
	 */
	public File downloadFileViaStream(String filename, String localFilename);

	/**
	 * Uploads a file to PBCS so that it can be imported. By default this uploads to inbox
	 * 
	 * @param filename the local name of the file to upload
	 */
	public void uploadFile(String filename);

	/**
	 * Uploads a file to PBCS so that it can be imported. This uploads to direcotry that is chosen
	 *
	 * @param filename the local name of the file to upload
	 * @param directory the dir to which this uploads to
	 */
	public void uploadFile(String filename, String directory);

	/**
	 * Deletes the file (snapshot) with the given name.
	 * 
	 * @param filename the name of the file to delete
	 */
	public void deleteFile(String filename);

	/**
	 * Return a list of files available on the remote system. Note that this
	 * includes both LCM snapshot files as well as regular files that exist on
	 * the system.
	 * 
	 * @return a list of available files
	 */
	public List<ApplicationSnapshot> listFiles();

	public ApplicationSnapshotInfo getSnapshotDetails(String name); 
	
	//public MaintenanceWindow getMaintenanceWindow();
	
	/**
	 * TODO: Implement LCM Export functionality.
	 */
	public void LcmExport();

	public void LcmImport();

}
