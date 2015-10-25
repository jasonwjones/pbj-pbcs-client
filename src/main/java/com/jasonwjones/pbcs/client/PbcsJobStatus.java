package com.jasonwjones.pbcs.client;

public interface PbcsJobStatus {

	/**
	 * Returns the numeric status of the job:
	 * 
	 * -1 = in progress
	 *  0 = success
	 *  1 = error
	 *  2 = cancel pending
	 *  3 = cancelled
	 *  4 = invalid parameter
	 *  Integer.MAX_VALUE = unknown
	 *  
	 * @return
	 */
	public Integer getStatus();
	
	public PbcsJobStatusCode getJobStatusCode();
	
	/** 
	 * Returns textual description of the status code, such as Completed or Error
	 * 
	 * @return status text
	 */
	public String getDescriptiveStatus();
	
	public String getDetails();
	
	/**
	 * The ID of the
	 * @return
	 */
	public Integer getJobId();
	
	public String getJobName();
	
}
