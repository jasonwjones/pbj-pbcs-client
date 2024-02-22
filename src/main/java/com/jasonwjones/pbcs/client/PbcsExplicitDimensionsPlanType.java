package com.jasonwjones.pbcs.client;

/**
 * A specialization of a normal plan type that has been created with explicitly defined dimensions. There are still a
 * handful of methods in the parent interface that need to be 'moved' over to this one since they are unsupported in a
 * plan type without explicit dimensions.
 */
public interface PbcsExplicitDimensionsPlanType extends PbcsPlanType {

    /**
     * Validate the explicitly defined dimensions. Dimensions are checked by calling {@link PbcsPlanType#getMember(String, String)}
     * using the dimension name for both parameters.
     *
     * @throws com.jasonwjones.pbcs.client.exceptions.PbcsInvalidDimensionException if there is an invalid dimension.
     */
    void validateDimensions();

}