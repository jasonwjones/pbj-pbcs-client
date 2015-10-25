package com.jasonwjones.pbcs.client.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;

import com.jasonwjones.pbcs.api.v3.Application;
import com.jasonwjones.pbcs.api.v3.JobDefinition;
import com.jasonwjones.pbcs.api.v3.JobDefinitionsWrapper;
import com.jasonwjones.pbcs.api.v3.JobLaunchPayload;
import com.jasonwjones.pbcs.api.v3.JobLaunchResponse;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsJobDefinition;
import com.jasonwjones.pbcs.client.PbcsJobLaunchResult;
import com.jasonwjones.pbcs.client.PbcsJobStatus;
import com.jasonwjones.pbcs.client.PbcsJobType;
import com.jasonwjones.pbcs.client.PbcsMemberProperties;
import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;

public class PbcsApplicationImpl implements PbcsApplication {
	
	private static final Logger logger = LoggerFactory.getLogger(PbcsApplicationImpl.class);
	
	private RestContext context;
	
	private Application application;
	
	private Map<String, String> appMap;
	
	public PbcsApplicationImpl(RestContext context, Application application) {
		this.context = context;
		this.application = application;
		
		this.appMap = new HashMap<String, String>();
		this.appMap.put("application", application.getName());
	}
	
	@Override
	public List<PbcsJobDefinition> getJobDefinitions() {
		logger.info("Getting job definitions for {}", application.getName());
		String url = this.context.getBaseUrl() + "applications/{application}/jobdefinitions";
		ResponseEntity<JobDefinitionsWrapper> output = this.context.getTemplate().getForEntity(url, JobDefinitionsWrapper.class, appMap);
		
		JobDefinitionsWrapper jobDefinitions = output.getBody();
		List<PbcsJobDefinition> pbcsJobDefs = new ArrayList<PbcsJobDefinition>();
		for (JobDefinition jobDefinition : jobDefinitions.getItems()) {
			PbcsJobDefinition pbcsJobDef = new PbcsJobDefinitionImpl(context, jobDefinition);
			pbcsJobDefs.add(pbcsJobDef);
		}
		return pbcsJobDefs;
	}

	@Override
	public List<PbcsJobDefinition> getJobDefinitions(PbcsJobType jobType) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public PbcsJobStatus getJobStatus(Integer jobId) {
		String url = this.context.getBaseUrl() + "applications/{application}/jobs/{jobId}";
		try {
			ResponseEntity<JobLaunchResponse> output = this.context.getTemplate().getForEntity(url, JobLaunchResponse.class, application.getName(), jobId);
			return new PbcsJobStatusImpl(output.getBody());
		} catch (HttpServerErrorException e) {
			throw new PbcsClientException("Error fetching job with status ID " + jobId + ". Perhaps it doesn't exist?");
		}
	}
	
	@Override
	public PbcsJobLaunchResult launchBusinessRule(String ruleName) {
		return launchBusinessRule(ruleName, null);

	}

	@Override
	public PbcsJobLaunchResult launchBusinessRule(String ruleName, Map<String, String> parameters) {
		String url = this.context.getBaseUrl() + "applications/{application}/jobs";
		JobLaunchPayload payload = new JobLaunchPayload("RULES", ruleName);
		payload.setParameters(parameters);
		ResponseEntity<JobLaunchResponse> output = this.context.getTemplate().postForEntity(url, payload, JobLaunchResponse.class, appMap);
		return new PbcsJobLaunchResultImpl(output.getBody());		
	}

	@Override
	public void launchRuleSet(String ruleSetName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void launchDataImport(String dataImportName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PbcsJobLaunchResult exportData(String exportName) {
		String url = this.context.getBaseUrl() + "applications/{application}/jobs";
		JobLaunchPayload payload = new JobLaunchPayload("EXPORT_DATA", exportName);
		ResponseEntity<JobLaunchResponse> output = this.context.getTemplate().postForEntity(url, payload, JobLaunchResponse.class, appMap);
		logger.info("Export data HTTP code: {}", output.getStatusCode().value());
		return new PbcsJobLaunchResultImpl(output.getBody());			
	}

	@Override
	public PbcsJobLaunchResult refreshCube() {
		return refreshCube("CubeRefresh");
	}

	@Override
	public PbcsJobLaunchResult refreshCube(String cubeRefreshName) {
		String url = this.context.getBaseUrl() + "applications/{application}/jobs";
		JobLaunchPayload payload = new JobLaunchPayload("CUBE_REFRESH", cubeRefreshName);
		ResponseEntity<JobLaunchResponse> output = this.context.getTemplate().postForEntity(url, payload, JobLaunchResponse.class, appMap);
		return new PbcsJobLaunchResultImpl(output.getBody());	
	}

	@Override
	public boolean isDpEnabled() {
		return application.isDpEnabled();
	}

	@Override
	public String getName() {
		return application.getName();
	}

	@Override
	public String getType() {
		return application.getType();
	}

	@Override
	public PbcsMemberProperties addMember(String dimensionName, String memberName, String parentName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PbcsMemberProperties getMemberProperties(String dimensionName, String memberName) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
