package com.jasonwjones.pbcs.client;

public interface PbcsObject {

    /**
     * Gets the name of this object. For example, a {@link PbcsApplication} object would return
     * the application name, such as "Vision". A {@link PbcsJobDefinition} may return a name such
     * <code>RefreshCube</code> or <code>calcall</code>.
     *
     * @return the name of this object
     */
    String getName();

    /**
     * Returns the type of object this is. This construct is somewhat a blend between "official"
     * EPM objects and "synthetic" objects that are modeled in this library.
     *
     * @return the object type
     */
    PbcsObjectType getObjectType(); // look to refactor to just getType()

}