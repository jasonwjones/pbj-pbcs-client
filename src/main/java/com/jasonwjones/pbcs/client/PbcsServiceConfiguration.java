package com.jasonwjones.pbcs.client;

public interface PbcsServiceConfiguration {

	public String getScheme();
	
	public Integer getPort();
	
	public String getPlanningApiVersion();
	
	public String getPlanningRestApiPath();
	
	public String getInteropApiVersion();
	
	public String getInteropRestApiPath();
	
}
