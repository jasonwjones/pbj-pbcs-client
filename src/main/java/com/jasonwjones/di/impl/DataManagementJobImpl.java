package com.jasonwjones.di.impl;

import com.jasonwjones.di.DataManagementJob;
import com.jasonwjones.di.api.v1.JobDefinition;

import java.util.StringJoiner;

/**
 * Default {@link DataManagementJob} implementation, delegating to the underlying data management API's
 * {@link JobDefinition}.
 */
public class DataManagementJobImpl implements DataManagementJob {

    private final JobDefinition jobDefinition;

    /**
     * Constructs an instance wrapping the given job definition.
     *
     * @param jobDefinition the job definition to delegate to
     */
    public DataManagementJobImpl(JobDefinition jobDefinition) {
        this.jobDefinition = jobDefinition;
    }

    @Override
    public Integer getId() {
        return jobDefinition.getJobId();
    }

    @Override
    public String getStatusText() {
        return jobDefinition.getJobStatus();
    }

    @Override
    public String getExecutedBy() {
        return jobDefinition.getExecutedBy();
    }

    @Override
    public String toString() {
        return DataManagementJobImpl.class.getSimpleName() + " [id=" + getId() + ", statusText=" + getStatusText() + ", executedBy=" + getExecutedBy() + "]";
    }

}