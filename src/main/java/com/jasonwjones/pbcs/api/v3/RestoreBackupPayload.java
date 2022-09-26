package com.jasonwjones.pbcs.api.v3;

import java.util.HashMap;
import java.util.Map;

public class RestoreBackupPayload {

	private String backupName;
	Map<String, String> parameters = new HashMap<>();

	public RestoreBackupPayload(String backupName) {
		this.backupName = backupName;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public String getBackupName() {
		return backupName;
	}

	public void setBackupName(String backupName) {
		this.backupName = backupName;
	}
}
