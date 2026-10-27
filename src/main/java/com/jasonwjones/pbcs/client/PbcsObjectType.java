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
    MEMBER,

    /**
     * Represents a job that can be run, such as a refresh cube operation.
     */
    JOB,

    /**
     * Represents the top-level client used to connect to PBCS.
     */
    CLIENT,

    /**
     * Represents a substitution variable in an application or one of its plans.
     *
     * @since 3.1.0
     */
    SUBSTITUTION_VARIABLE

}