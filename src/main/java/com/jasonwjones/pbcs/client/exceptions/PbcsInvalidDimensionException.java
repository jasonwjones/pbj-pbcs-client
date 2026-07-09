package com.jasonwjones.pbcs.client.exceptions;

import com.jasonwjones.pbcs.client.PbcsObjectType;

/**
 * A specialization of the "no such object" exception that is specific for when a dimension is invalid. This may occur
 * when requesting a dimension of a certain name from a {@link com.jasonwjones.pbcs.client.PbcsPlanType} or when
 * validating the dimensions of a newly instantiated cube, such as when enabling the {@link com.jasonwjones.pbcs.client.PbcsApplication.PlanTypeConfiguration}
 * option to do so.
 */
public class PbcsInvalidDimensionException extends PbcsNoSuchObjectException {

    /**
     * Instantiate this exception with the given invalid dimension name.
     *
     * @param dimensionName the name of the invalid dimension
     */
    public PbcsInvalidDimensionException(String dimensionName) {
        super(dimensionName, PbcsObjectType.DIMENSION);
    }

}