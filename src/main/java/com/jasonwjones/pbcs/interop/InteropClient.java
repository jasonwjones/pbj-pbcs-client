package com.jasonwjones.pbcs.interop;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.util.MultiValueMap;

import com.jasonwjones.pbcs.api.v3.JobLaunchResponse;
import com.jasonwjones.pbcs.api.v3.RestoreBackupResponse;
import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;
import com.jasonwjones.pbcs.interop.impl.ApplicationSnapshot;
import com.jasonwjones.pbcs.interop.impl.ApplicationSnapshotInfo;
import com.jasonwjones.pbcs.interop.impl.InteropClientImpl;

/**
 * Main interface for LCM operations.
 * 
 * @author Jason Jones
 *
 */
public interface InteropClient {

	String LCM = "LCM";

	String EXTERNAL = "EXTERNAL";

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
	File downloadFile(String filename) throws PbcsClientException;

	File downloadFile(String filename, String localFilename);

	/**
	 * This is useful for downloading large files - whole content is streamed to
	 * disk
	 *
	 * @param filename remote filename in cloud
	 * @return an object representing the downloaded file
	 * @throws PbcsClientException if an error occurs
	 */

	File downloadFileViaStream(String filename) throws PbcsClientException;
	
	/**
	 * This is useful for downloading large files - whole content is streamed to
	 * disk
	 * 
	 * @param filename remote filename in cloud
	 * @param localFilename filename on disk
	 * @return an object representing the downloaded file
	 */
	File downloadFileViaStream(String filename, String localFilename);

	/**
	 * Uploads a file to PBCS so that it can be imported.
	 * 
	 * @param filename the local name of the file to upload
	 * @param remoteDir Remote extDir on the remote filesystem. Leave empty not to use
	 * @return response
	 */
	String uploadFile(String filename, Optional<String> remoteDir);

	String uploadFile(String filename);

	/**
	 * Deletes the file (snapshot) with the given name.
	 * 
	 * @param filename the name of the file to delete
	 *                 if you want to delete a filename from a folder just pass folderName/filename as filename param
	 * @return response
	 */
	String deleteFile(String filename);

	/**
	 * Run the role assignment report
	 * @param map - parameters for the call. filename and api_version
	 * @return response as a String
	 */
	JobLaunchResponse runRoleAssignmentReport(MultiValueMap<String, String> map);

	/**
	 * Return a list of files available on the remote system. Note that this
	 * includes both LCM snapshot files as well as regular files that exist on
	 * the system.
	 *
	 * @return a list of available files
	 */
	List<ApplicationSnapshot> listFiles();

	ApplicationSnapshotInfo getSnapshotDetails(String name);

	//public MaintenanceWindow getMaintenanceWindow();

	/**
	 * TODO: Implement LCM Export functionality.
	 */
	void LcmExport();

	/**
	 * TODO: Implement LCM Import functionality.
	 */
	void LcmImport();

	/**
	 * List available backup snapshots archived by Oracle in the Oracle Object storage Cloud
	 * @return list of available backup snapshots
	 */
	List<String> backupsList();

	/**
	 *
	 * @param backupName The name of the backup snapshot, as listed in the response for @{@link InteropClientImpl#backupsList()}
	 * @param parameters Parameters for restore backup. For example targetName:
	 * {
	 * 	"backupName": "2022-02-16T21:00:02/Artifact_Snapshot_2021-12-16T21:00:02",
	 * 	"parameters": {
	 * 		"targetName": "Backup_16Dec"
	 *        }
	 * }
	 * @return job result
	 */
	RestoreBackupResponse launchRestoreBackup(String backupName, Map<String, String> parameters);

}
