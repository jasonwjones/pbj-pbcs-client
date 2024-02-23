package com.jasonwjones.pbcs.client;

import java.util.concurrent.TimeUnit;

/**
 * Model for a job that is executed on a PBCS server. Jobs include running business rules (calcs), data imports, and more.
 */
public interface PbcsJobStatus {

	long DEFAULT_CHECK_INTERVAL_MILLIS = 3000;

	/**
	 * Returns the numeric status of the job:
	 *
	 * <p>
	 * -1 = in progress 0 = success 1 = error 2 = cancel pending 3 = cancelled 4
	 * = invalid parameter Integer.MAX_VALUE = unknown
	 *
	 * @return the numeric job status
	 */
	Integer getStatus();

	/**
	 * Returns the job status enumeration.
	 *
	 * @return the job status enumeration value
	 */
	PbcsJobStatusCode getJobStatusType();

	/**
	 * Check if this job has finished in one way or another. Simply returns the value of {@link PbcsJobStatusCode#isFinished()}.
	 *
	 * @return true if the job has finished, false otherwise
	 */
	boolean isFinished();

	/**
	 * Check if the job finished successfully. Simply returns the value of  {@link PbcsJobStatusCode#isSuccessful()},
	 * and is essentially syntactic sugar for checking if the status type is {@link PbcsJobStatusCode#SUCCESS}.
	 *
	 * @return true if job ran successfully, false otherwise
	 */
	boolean isSuccessful();

	/**
	 * Check for updates on this job and return a new object with the updated values.
	 *
	 * @return a new job status object
	 */
	PbcsJobStatus refresh();

	/**
	 * Returns textual description of the status code, such as Completed or Error.
	 *
	 * @return status text
	 */
	String getDescriptiveStatus();

	/**
	 * Get details about the job (often blank).
	 *
	 * @return job details
	 */
	String getDetails();

	/**
	 * The ID of the job
	 *
	 * @return the job ID
	 */
	Integer getJobId();

	/**
	 * The name of the job. May vary by type. In the case of a business rule, the job name is the business rule name.
	 *
	 * @return the name of the job
	 */
	String getJobName();

	/**
	 * Convenience method to wait (and poll) until the job is finished, one way or the other. This method calls {@link #waitUntilFinished(long, TimeUnit)}
	 * using the {@link PbcsJobStatus#DEFAULT_CHECK_INTERVAL_MILLIS} value.
	 *
	 * @return returns the most recent refresh of this job (could/should be different from original instance if it wasn't already complete)
	 * @throws InterruptedException if an interruption occurs
	 */
	PbcsJobStatus waitUntilFinished() throws InterruptedException;

	/**
	 * Wait until job is finished (success or failure). This mechanism serves mostly a quick convenience that just loops
	 * until the job is finished. If you are concerned that the task might be waiting for a long time, you may be beter
	 * served refreshing the status yourself with the {@link #refresh()} method used inside an Executor, in order to
	 * avoid busy waiting the thread.
	 *
	 * @param checkInterval how often to poll for a job update
	 * @param unit the time unit for the check interval
	 * @return returns the most recent refresh of this job (could/should be different from original instance if it wasn't already complete)
	 * @throws InterruptedException if an interruption occurs
	 */
	PbcsJobStatus waitUntilFinished(long checkInterval, TimeUnit unit) throws InterruptedException;

}