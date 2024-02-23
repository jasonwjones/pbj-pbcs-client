package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.api.v3.JobLaunchResponse;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsJobStatus;
import com.jasonwjones.pbcs.client.PbcsJobStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class PbcsJobLaunchResultImpl implements PbcsJobStatus {

	private static final Logger logger = LoggerFactory.getLogger(PbcsJobLaunchResultImpl.class);

	private final PbcsApplication application;

	private final JobLaunchResponse jobLaunchResponse;

	private final PbcsJobStatusCode status;

	PbcsJobLaunchResultImpl(PbcsApplication application, JobLaunchResponse jobLaunchResponse) {
		this.application = application;
		this.jobLaunchResponse = jobLaunchResponse;
		this.status = PbcsJobStatusCode.valueOf(jobLaunchResponse.getStatus());
	}

	@Override
	public Integer getStatus() {
		return jobLaunchResponse.getStatus();
	}

	@Override
	public PbcsJobStatusCode getJobStatusType() {
		return status;
	}

	@Override
	public boolean isFinished() {
		return status.isFinished();
	}

	@Override
	public boolean isSuccessful() {
		return status.isSuccessful();
	}

	@Override
	public String getDescriptiveStatus() {
		return jobLaunchResponse.getDescriptiveStatus();
	}

	@Override
	public String getDetails() {
		return jobLaunchResponse.getDetails();
	}

	@Override
	public Integer getJobId() {
		return jobLaunchResponse.getJobId();
	}

	@Override
	public String getJobName() {
		return jobLaunchResponse.getJobName();
	}

	@Override
	public PbcsJobStatus refresh() {
		logger.info("Refreshing job {}", this);
		return application.getJobStatus(getJobId());
	}

	@Override
	public String toString() {
		return jobLaunchResponse.toString();
	}

	@Override
	public PbcsJobStatus waitUntilFinished() throws InterruptedException {
		return waitUntilFinished(DEFAULT_CHECK_INTERVAL_MILLIS, TimeUnit.MILLISECONDS);
	}

	@Override
	public PbcsJobStatus waitUntilFinished(long checkInterval, TimeUnit unit) throws InterruptedException {
		PbcsJobStatus status = this;
		long startTime = System.currentTimeMillis();
		while (!status.isFinished()) {
			Thread.sleep(unit.toMillis(checkInterval));
			status = status.refresh();
		}
		long duration = System.currentTimeMillis() - startTime;
		logger.info("Waited {}ms for {} to finish, result: {}", duration, status, status.getJobStatusType());
		return status;
	}

}