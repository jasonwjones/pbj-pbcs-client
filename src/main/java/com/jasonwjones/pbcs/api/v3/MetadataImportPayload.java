package com.jasonwjones.pbcs.api.v3;

/**
 * If you need parameters in json directly like a map use @{@link JobLaunchPayload} instead
 */
public class MetadataImportPayload extends Payload {

	public MetadataImportPayload(String jobType, String jobName) {
		super(jobType, jobName);
	}

}
