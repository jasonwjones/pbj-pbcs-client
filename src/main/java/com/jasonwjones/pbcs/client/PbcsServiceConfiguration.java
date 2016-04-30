package com.jasonwjones.pbcs.client;

import org.springframework.http.client.ClientHttpRequestFactory;

public interface PbcsServiceConfiguration {

	public String getScheme();
	
	public Integer getPort();
	
	public String getPlanningApiVersion();
	
	public String getPlanningRestApiPath();
	
	public String getInteropApiVersion();
	
	public String getInteropRestApiPath();
	
	public Boolean isSkipApiCheck();
	
	public ClientHttpRequestFactory createRequestFactory(PbcsConnection connection);
	
}
