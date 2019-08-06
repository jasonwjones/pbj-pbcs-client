package com.jasonwjones.pbcs.client;

import java.util.List;

// TODO:
// basically it's syntactic sugar for the export data slice method and the get dimennsions method that takes a plan type

public interface PbcsPlanType {

	public String getName();
	
	public List<PbcsDimension> getDimensions();
	
	public PbcsApplication getApplication();
	
}
