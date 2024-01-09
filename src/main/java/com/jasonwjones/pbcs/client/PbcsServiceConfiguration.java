package com.jasonwjones.pbcs.client;

public interface PbcsServiceConfiguration {

	String getScheme();

	String getPlanningApiVersion();

	String getPlanningRestApiPath();

	String getInteropApiVersion();

	String getInteropRestApiPath();

	String getAifRestApiVersion();

	String getAifRestApiPath();

	boolean isSkipApiCheck();

}