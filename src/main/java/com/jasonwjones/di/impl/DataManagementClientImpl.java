package com.jasonwjones.di.impl;

import com.jasonwjones.common.SimpleRestTemplate;
import com.jasonwjones.di.DataManagementClient;
import com.jasonwjones.di.DataManagementJob;
import com.jasonwjones.di.api.v1.JobDefinition;
import com.jasonwjones.di.api.v1.JobDefinitionsWrapper;
import com.jasonwjones.pbcs.api.v3.Api;
import com.jasonwjones.pbcs.client.impl.RestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DataManagementClientImpl implements DataManagementClient {

    private static final Logger logger = LoggerFactory.getLogger(DataManagementClientImpl.class);

    public static final String DEFAULT_VERSION = "V1";

    private static final String AIF_BASE = "/aif/rest/";

    private final SimpleRestTemplate template;

    public DataManagementClientImpl(RestContext context) {
        template = new SimpleRestTemplate(context.getTemplate(), "https://" + context.getServer() + AIF_BASE + DEFAULT_VERSION);
    }

    @Override
    public void getVersions() {
        Api api = template.get("", Api.class);
        logger.info("Data management API version: {}", api.getVersion());
    }

    @Override
    public List<DataManagementJob> getJobs() {
        JobDefinitionsWrapper wrapper = template.get("/jobs", JobDefinitionsWrapper.class);
        List<DataManagementJob> jobs = new ArrayList<>();
        for (JobDefinition jobDefinition : wrapper.getItems()) {
            jobs.add(new DataManagementJobImpl(jobDefinition));
        }
        return jobs;
    }

}