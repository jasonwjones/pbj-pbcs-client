package com.jasonwjones.pbcs.client.exceptions;

public class PbcsJobLaunchException extends PbcsClientException {

    private final String jobName;

    public PbcsJobLaunchException(String jobName, Throwable cause) {
        super("Exception running job " + jobName, cause);
        this.jobName = jobName;
    }

    public String getJobName() {
        return jobName;
    }

}