package com.jasonwjones.di.api.v1;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Maps a single job entry as returned by the unofficial data management (DM/AIF) jobs endpoint.
 */
public class JobDefinition {

    private String details;

    private Integer jobId;

    // e.g SUCCESS, WARNING, FAILED
    @JsonProperty("jobStatus")
    private String jobStatus;

    // /u03/inbox/outbox/logs/Vision_271.log
    @JsonProperty("logFileName")
    private String logFilename;

    @JsonProperty("outputFilename")
    private String outputFilename;

    // e.g. COMM_LOAD_BALANCES
    private String processType;

    private String executedBy;

    /**
     * Constructs an empty instance for deserialization.
     */
    public JobDefinition() {
    }

    /**
     * Gets the job's details.
     *
     * @return the details
     */
    public String getDetails() {
        return details;
    }

    /**
     * Sets the job's details.
     *
     * @param details the details
     */
    public void setDetails(String details) {
        this.details = details;
    }

    /**
     * Gets the job's ID.
     *
     * @return the job ID
     */
    public Integer getJobId() {
        return jobId;
    }

    /**
     * Sets the job's ID.
     *
     * @param jobId the job ID
     */
    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    /**
     * Gets the job's status, e.g. {@code SUCCESS}, {@code WARNING}, or {@code FAILED}.
     *
     * @return the job status
     */
    public String getJobStatus() {
        return jobStatus;
    }

    /**
     * Sets the job's status.
     *
     * @param jobStatus the job status
     */
    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    /**
     * Gets the log filename for this job, e.g. {@code /u03/inbox/outbox/logs/Vision_271.log}.
     *
     * @return the log filename
     */
    public String getLogFilename() {
        return logFilename;
    }

    /**
     * Sets the log filename for this job.
     *
     * @param logFilename the log filename
     */
    public void setLogFilename(String logFilename) {
        this.logFilename = logFilename;
    }

    /**
     * Gets the output filename for this job.
     *
     * @return the output filename
     */
    public String getOutputFilename() {
        return outputFilename;
    }

    /**
     * Sets the output filename for this job.
     *
     * @param outputFilename the output filename
     */
    public void setOutputFilename(String outputFilename) {
        this.outputFilename = outputFilename;
    }

    /**
     * Gets the process type for this job, e.g. {@code COMM_LOAD_BALANCES}.
     *
     * @return the process type
     */
    public String getProcessType() {
        return processType;
    }

    /**
     * Sets the process type for this job.
     *
     * @param processType the process type
     */
    public void setProcessType(String processType) {
        this.processType = processType;
    }

    /**
     * Gets the user who executed this job.
     *
     * @return the executing user
     */
    public String getExecutedBy() {
        return executedBy;
    }

    /**
     * Sets the user who executed this job.
     *
     * @param executedBy the executing user
     */
    public void setExecutedBy(String executedBy) {
        this.executedBy = executedBy;
    }

}
