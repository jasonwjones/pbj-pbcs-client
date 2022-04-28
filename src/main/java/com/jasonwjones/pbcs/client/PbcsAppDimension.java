package com.jasonwjones.pbcs.client;

import java.util.Set;

public interface PbcsAppDimension extends PbcsDimension {

    Set<String> getValidPlans();

    boolean isValidForPlan(String plan);

}