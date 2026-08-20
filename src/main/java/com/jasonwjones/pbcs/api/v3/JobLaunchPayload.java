package com.jasonwjones.pbcs.api.v3;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonValue;

/***
 * Some of epm requests need to have json like:
 *{
 * 	"backupName": "2022-02-16T21:00:02/Artifact_Snapshot_2021-12-16T21:00:02",
 * 	"parameters": {
 * 		"targetName": "Backup_16Dec"
 *  }
 * }
 * Need have map of parameters directly in the body. For requests which do not need this use @{@link MetadataImportPayload}
 */
public class JobLaunchPayload extends Payload {

	/**
	 * Constructs an instance with the given job type and name.
	 *
	 * @param jobType the job type
	 * @param jobName the job name
	 */
	public JobLaunchPayload(String jobType, String jobName) {
		super(jobType, jobName);
	}

	/**
	 * Constructs an instance with the given job type, name, and parameters.
	 *
	 * @param jobType the job type
	 * @param jobName the job name
	 * @param parameters the job launch parameters
	 */
	public JobLaunchPayload(String jobType, String jobName, Map<String, String> parameters) {
		this(jobType, jobName);
		setParameters(parameters);
	}

//	@JsonValue
//	public Map<String, String> getParameters() {
//		return super.getParameters();
//	}

}