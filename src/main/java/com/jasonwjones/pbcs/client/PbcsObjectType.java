package com.jasonwjones.pbcs.client;

/**
 * Enumeration for various objects that exist in PBCS system. Mostly used for typing in
 * {@link com.jasonwjones.pbcs.client.exceptions.PbcsNoSuchObjectException}.
 */
public enum PbcsObjectType {

    /**
     * Object type represents an entire PBCS application, such as the Vision application
     */
    APPLICATION,

    /**
     * Object type represents a plan/cube in an application.
     */
    PLAN,

    /**
     * Represents a dimension in the application or a plan/cube.
     */
    DIMENSION,

    /**
     * Represents a member in the application.
     */
    MEMBER

}