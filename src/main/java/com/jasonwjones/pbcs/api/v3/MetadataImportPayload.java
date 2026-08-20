package com.jasonwjones.pbcs.api.v3;

/**
 * If you need parameters in json directly like a map use @{@link JobLaunchPayload} instead
 */
public class MetadataImportPayload extends Payload {

	/**
	 * Constructs an instance with the given job type and name.
	 *
	 * @param jobType the job type
	 * @param jobName the job name
	 */
	public MetadataImportPayload(String jobType, String jobName) {
		super(jobType, jobName);
	}

}
