package com.jasonwjones.pbcs.client;

import java.util.Set;

public interface PbcsDimension {

	public String getBalanceColumnName();
	
	public String getDimensionClass();
	
	public String getDimensionClassOrg();
	
	public String getName();
	
	public String getNameOrg();
	
	public Set<String> getValidPlans();
	
	public boolean isValidForPlan(String plan);
	
}
