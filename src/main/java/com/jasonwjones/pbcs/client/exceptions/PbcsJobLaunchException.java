package com.jasonwjones.pbcs.client.exceptions;

/**
 * Thrown when an unexpected error occurs while launching a job.
 */
public class PbcsJobLaunchException extends PbcsClientException {

    /**
     * The name of the job that failed to launch.
     */
    private final String jobName;

    /**
     * Constructs an instance for the given job and cause.
     *
     * @param jobName the name of the job that failed to launch
     * @param cause the underlying cause
     */
    public PbcsJobLaunchException(String jobName, Throwable cause) {
        super("Exception running job " + jobName + ": " + cause.getMessage(), cause);
        this.jobName = jobName;
    }

    /**
     * Gets the name of the job that failed to launch.
     *
     * @return the job name
     */
    public String getJobName() {
        return jobName;
    }

}
