package com.jasonwjones.pbcs.client;

// appears to be inside of the items collection 
public interface PbcsJobDetails {

	// items --> collection for each dimensino?
	
	public Integer getRecordsRead();
	
	public Integer getRecordsRejected();
	
	public Integer getRecordsProcessed();
	
	public String getDimensionName();
	
	public String getLoadType();
	
}
