package com.jasonwjones.pbcs.api.v3;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the request payload for launching a restore of a backup snapshot.
 */
public class RestoreBackupPayload {

	private String backupName;
	Map<String, String> parameters = new HashMap<>();

	/**
	 * Constructs an instance for the given backup name.
	 *
	 * @param backupName the name of the backup snapshot to restore
	 */
	public RestoreBackupPayload(String backupName) {
		this.backupName = backupName;
	}

	/**
	 * Gets the restore parameters, such as {@code targetName}.
	 *
	 * @return the parameters
	 */
	public Map<String, String> getParameters() {
		return parameters;
	}

	/**
	 * Sets the restore parameters.
	 *
	 * @param parameters the parameters
	 */
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	/**
	 * Gets the name of the backup snapshot to restore.
	 *
	 * @return the backup name
	 */
	public String getBackupName() {
		return backupName;
	}

	/**
	 * Sets the name of the backup snapshot to restore.
	 *
	 * @param backupName the backup name
	 */
	public void setBackupName(String backupName) {
		this.backupName = backupName;
	}
}
