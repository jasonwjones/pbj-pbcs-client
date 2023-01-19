package com.jasonwjones.pbcs.client;

import java.util.Set;

/**
 * An "app dimension" extends from a normal {@link PbcsDimension} object and is slightly specialized with additional
 * methods such as which plans (cubes) it is valid for.
 */
public interface PbcsAppDimension extends PbcsDimension {

    /**
     * Gets the set of plan/cube names that this dimension is valid in
     *
     * @return the set of valid plans
     */
    Set<String> getValidPlans();

    /**
     * Checks if the given plan is valid for this dimension. Should be semantically equivalent to
     * <code>getValidPlans().contains(plan)</code>.
     *
     * @param plan the plan name
     * @return true if this dimension is valid for the plan
     */
    boolean isValidForPlan(String plan);

}