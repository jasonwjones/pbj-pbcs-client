package com.jasonwjones.di;

import com.jasonwjones.pbcs.aif.AifDimension;
import com.jasonwjones.pbcs.client.PbcsApi;

import java.util.List;

/**
 * A client for the unofficial data management (DM/AIF) REST API, used for jobs and application/dimension
 * metadata not exposed by the main PBCS REST API.
 */
public interface DataManagementClient {

    /**
     * Gets the data management API version in use.
     *
     * @return the API version
     */
    PbcsApi getVersion();

    /**
     * Gets the jobs known to the data management service.
     *
     * @return the jobs
     */
    List<DataManagementJob> getJobs();

    /**
     * Gets the dimensions for the given application.
     *
     * @param applicationName the application name
     * @return the dimensions for that application
     */
    List<AifDimension> getDimensions(String applicationName);

}
