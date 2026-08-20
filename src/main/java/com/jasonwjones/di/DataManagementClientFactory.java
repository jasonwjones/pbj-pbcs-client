package com.jasonwjones.di;

import java.util.List;

/**
 * Factory for creating {@link DataManagementClient} instances for a given API version.
 */
public class DataManagementClientFactory {

    /**
     * The default data management API version.
     */
    public static final String DEFAULT_VERSION = "V1";

    /**
     * Constructs an instance of this factory.
     */
    public DataManagementClientFactory() {
    }

    /**
     * Creates a data management client for the given API version.
     *
     * @param version the API version to use
     * @return a data management client for that version
     */
    public DataManagementClient createClient(String version) {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the API versions supported by this factory.
     *
     * @return the supported versions
     */
    public List<String> getVersions() {
        throw new UnsupportedOperationException();
    }

}
