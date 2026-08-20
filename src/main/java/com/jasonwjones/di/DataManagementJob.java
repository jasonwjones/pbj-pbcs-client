package com.jasonwjones.di;

/**
 * Represents a job launched through the data management (DM/AIF) API.
 */
public interface DataManagementJob {

    /**
     * Gets the job's ID.
     *
     * @return the job ID
     */
    Integer getId();

    /**
     * Gets the job's status text.
     *
     * @return the status text
     */
    String getStatusText();

    /**
     * Gets the user who executed the job.
     *
     * @return the executing user
     */
    String getExecutedBy();

}
