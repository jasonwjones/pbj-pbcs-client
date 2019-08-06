package com.jasonwjones.pbcs.client;

import java.util.Set;

/**
 * Represents a dimension in terms of a Data Management dimension (as opposed to a particular
 * instance of a dimension in an application/plan type).
 * 
 * @author jasonwjones
 *
 */
public interface PbcsDimension {

	public String getBalanceColumnName();

	public String getDimensionClass();

	public String getDimensionClassOrg();

	public String getName();

	public String getNameOrg();

	public Set<String> getValidPlans();

	public boolean isValidForPlan(String plan);

}
