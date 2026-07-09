package com.jasonwjones.di.api.v1;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getLogFilename() {
        return logFilename;
    }

    public void setLogFilename(String logFilename) {
        this.logFilename = logFilename;
    }

    public String getOutputFilename() {
        return outputFilename;
    }

    public void setOutputFilename(String outputFilename) {
        this.outputFilename = outputFilename;
    }

    public String getProcessType() {
        return processType;
    }

    public void setProcessType(String processType) {
        this.processType = processType;
    }

    public String getExecutedBy() {
        return executedBy;
    }

    public void setExecutedBy(String executedBy) {
        this.executedBy = executedBy;
    }

}