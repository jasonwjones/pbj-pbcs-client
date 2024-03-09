package com.jasonwjones.pbcs.client;

/**
 * This is a shell interface in order to maintain compatibility for clients using
 * {@link PbcsMemberProperties}. Historically, the REST POJO and the member object were
 * the same object, leading to some weirdness with behavior and properties.
 */
public interface PbcsMember extends PbcsMemberProperties {

    /**
     * Get the application associated with this member. The presence of this method in this
     * interface isn't "special", it simply postdates the PbcsMember/PbcsMemberProperties
     * bifurcation.
     *
     * @return the application for this member
     */
    PbcsApplication getApplication();

}