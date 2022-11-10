package com.jasonwjones.pbcs.client;

// appears to be inside the items collection
public interface PbcsJobDetails {

	// items --> collection for each dimension?

	Integer getRecordsRead();

	Integer getRecordsRejected();

	Integer getRecordsProcessed();

	String getDimensionName();

	String getLoadType();

}