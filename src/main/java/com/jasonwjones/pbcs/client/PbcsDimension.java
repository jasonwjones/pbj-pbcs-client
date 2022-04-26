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

	String getBalanceColumnName();

	String getDimensionClass();

	String getDimensionClassOrg();

	String getName();

	String getNameOrg();

	Set<String> getValidPlans();

	boolean isValidForPlan(String plan);

}