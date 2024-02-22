package com.jasonwjones.pbcs.client.exceptions;

import com.jasonwjones.pbcs.client.PbcsObjectType;

public class PbcsInvalidDimensionException extends PbcsNoSuchObjectException {

    public PbcsInvalidDimensionException(String dimensionName) {
        super(dimensionName, PbcsObjectType.DIMENSION);
    }

}