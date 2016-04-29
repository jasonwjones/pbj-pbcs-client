package com.jasonwjones.pbcs.client;

import java.util.List;

public interface PbcsMemberProperties {

	public String getName();
	
	public List<? extends PbcsMemberProperties> getChildren();
	
	public String getDescription();
	
	public String getParentName();
	
	public String getDataType();
	
	// such as 33
	public Integer getObjectType();
	
	// such as STOREDATA
	public String getDataStorage();
	
	// mapped from dimName
	public String getDimensionName();
	
	// from twoPass
	public boolean isTwoPass();
	
}
