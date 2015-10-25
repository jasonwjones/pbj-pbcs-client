package com.jasonwjones.pbcs.client;

public interface PbcsJobLaunchResult {

	public Integer getStatus();
	
	public String getDescriptiveStatus();
	
	public Integer getJobId();
	
	public String getJobName();
	
}
