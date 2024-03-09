package com.jasonwjones.di;

import com.jasonwjones.di.api.v1.JobDefinition;

import java.util.List;

public interface DataManagementClient {

    void getVersions();

    List<DataManagementJob> getJobs();

}