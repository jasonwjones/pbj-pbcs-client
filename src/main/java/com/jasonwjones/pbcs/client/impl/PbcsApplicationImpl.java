package com.jasonwjones.pbcs.client.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpServerErrorException;

import com.jasonwjones.pbcs.api.v3.Application;
import com.jasonwjones.pbcs.api.v3.JobDefinition;
import com.jasonwjones.pbcs.api.v3.JobDefinitionsWrapper;
import com.jasonwjones.pbcs.api.v3.JobLaunchPayload;
import com.jasonwjones.pbcs.api.v3.JobLaunchResponse;
import com.jasonwjones.pbcs.api.v3.dataslices.DataSlice;
import com.jasonwjones.pbcs.api.v3.dataslices.ExportDataSlice;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsJobDefinition;
import com.jasonwjones.pbcs.client.PbcsJobLaunchResult;
import com.jasonwjones.pbcs.client.PbcsJobStatus;
import com.jasonwjones.pbcs.client.PbcsJobType;
import com.jasonwjones.pbcs.client.PbcsMemberProperties;
import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;
import com.jasonwjones.pbcs.client.impl.models.PbcsMemberPropertiesImpl;

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
		ResponseEntity<JobDefinitionsWrapper> output = this.context.getTemplate().getForEntity(url,
				JobDefinitionsWrapper.class, appMap);

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
			ResponseEntity<JobLaunchResponse> output = this.context.getTemplate().getForEntity(url,
					JobLaunchResponse.class, application.getName(), jobId);
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
		ResponseEntity<JobLaunchResponse> output = this.context.getTemplate().postForEntity(url, payload,
				JobLaunchResponse.class, appMap);
		return new PbcsJobLaunchResultImpl(output.getBody());
	}

	@Override
	public void launchRuleSet(String ruleSetName) {
		// TODO Auto-generated method stub

	}

	@Override
	public PbcsJobLaunchResult importMetadata(String metadataImportName) {
		return importMetadata(metadataImportName, null);
	}

	@Override
	public PbcsJobLaunchResult importMetadata(String metadataImportName, String dataFile) {
		logger.info("Launching metadata import data job: {}", metadataImportName);
		String url = this.context.getBaseUrl() + "applications/{application}/jobs";
		JobLaunchPayload payload = new JobLaunchPayload("IMPORT_METADATA", metadataImportName);

		// "parameters" var is optional if not specifying. If it's specified,
		// then it should
		// be a zip file
		if (dataFile != null) {
			Map<String, String> params = new HashMap<String, String>();
			params.put("importZipFileName", dataFile);
			payload.setParameters(params);
		}
		ResponseEntity<JobLaunchResponse> output = this.context.getTemplate().postForEntity(url, payload, JobLaunchResponse.class, appMap);
		return new PbcsJobLaunchResultImpl(output.getBody());
	}

	// example from CREST
	// public void integrationScenarioImportDataRunCalcCopyToAso() throws
	// Exception {
	// uploadFile("data.csv");
	// }
	// executeJob("IMPORT_DATA", "loadingq1data", "{importFileName:data.csv}");
	// executeJob("CUBE_REFRESH", null, null);
	// executeJob("PLAN_TYPE_MAP", "CampaignToReporting", "{clearData:false}");
	@Override
	public void launchDataImport(String dataImportName) {
		logger.info("Launcing import data job: {}", dataImportName);
		String url = this.context.getBaseUrl() + "applications/{application}/jobs";
		JobLaunchPayload payload = new JobLaunchPayload("IMPORT_DATA", dataImportName);
		// can add 'importFileName' to parameters on payload object if we want
		// (the name of a CSV, ZIP, or TXT file). In case of ZIP, the ZIP can
		// contain 1+ CSV files
		// such as data1-3, data2-3, data3-3.csv, etc.
		// ResponseEntity<JobLaunchResponse> output =
		// this.context.getTemplate().postForEntity(url, payload,
		// JobLaunchResponse.class, appMap);
		ResponseEntity<String> output = this.context.getTemplate().postForEntity(url, payload, String.class, appMap);
		System.out.println("import resp: " + output.getBody());
		// TODO Auto-generated method stub

	}

	@Override
	public PbcsJobLaunchResult exportData(String exportName) {
		String url = this.context.getBaseUrl() + "applications/{application}/jobs";
		JobLaunchPayload payload = new JobLaunchPayload("EXPORT_DATA", exportName);
		ResponseEntity<JobLaunchResponse> output = this.context.getTemplate().postForEntity(url, payload,
				JobLaunchResponse.class, appMap);
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
		ResponseEntity<JobLaunchResponse> output = this.context.getTemplate().postForEntity(url, payload,
				JobLaunchResponse.class, appMap);
		logger.info("Cube refresh launched");
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

	/**
	 * CREST doc: Adds a new member to the application outline in the specified
	 * dimension and plan type and under the specified parent member.
	 * Prerequisite: The parent member must be enabled for dynamic children and
	 * a cube refresh must have happened after the parent was enabled.
	 */

	// TODO: currently getting a BAD request 400 possibly because it's not
	// enable dynamic chidlren.
	@Override
	public PbcsMemberProperties addMember(String dimensionName, String memberName, String parentName) {
		String url = this.context.getBaseUrl() + "applications/{application}/dimensions/{dimName}/members";

		MemberAdd ma = new MemberAdd(memberName, parentName);
		// Map<String, String> request = new HashMap<String, String>();
		// request.put("memberName", memberName);
		// request.put("parentName", parentName);

		// {"status":400,"detail":
		// "Error occurred when adding member. Cannot add member
		// <North America> because its parent <Enterprise Global>
		// is not enabled for dynamic children.",
		// "message":"com.hyperion.planning.HspRuntimeException: Error occurred
		// when adding member. Cannot add member <North America> because its
		// parent <Enterprise Global> is not enabled for dynamic
		// children.","localizedMessage":"com.hyperion.planning.HspRuntimeException:
		// Error occurred when adding member. Cannot add member <North America>
		// because its parent <Enterprise Global> is not enabled for dynamic
		// children."}

		this.context.getTemplate().setErrorHandler(new MyResponseErrorHandler());
		ResponseEntity<String> resp = this.context.getTemplate().postForEntity(url, ma, String.class,
				application.getName(), dimensionName);

		System.out.println("Add response: " + resp.getBody());
		// ResponseEntity<PbcsMemberPropertiesImpl> memberResponse =
		// this.context.getTemplate().getForEntity(url,
		// PbcsMemberPropertiesImpl.class, application.getName(), dimensionName,
		// memberName);
		// return memberResponse.getBody();

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PbcsMemberProperties getMember(String dimensionName, String memberName) {
		Assert.hasText(dimensionName, "Must specify a dimension name");
		Assert.hasText(memberName, "Must specify a member name");
	
		logger.info("Fetching member properties for {} from dimension {}", memberName, dimensionName);
		String url = this.context.getBaseUrl() + "applications/{application}/dimensions/{dimName}/members/{member}";
		logger.debug("Body: {}", this.context.getTemplate().getForEntity(url, String.class, application.getName(), dimensionName, memberName).getBody());
		ResponseEntity<PbcsMemberPropertiesImpl> memberResponse = this.context.getTemplate().getForEntity(url, PbcsMemberPropertiesImpl.class, application.getName(), dimensionName, memberName);
		logger.debug("Headers: " + memberResponse.getHeaders());
		return memberResponse.getBody();
	}

	public void getUserPreferences() {
		logger.info("Fetching user preferences for current user");
		String url = this.context.getBaseUrl() + "applications/{application}/userpreferences";
		ResponseEntity<String> userPrefs = this.context.getTemplate().getForEntity(url, String.class,
				application.getName());
		System.out.println("User prefs: " + userPrefs.getBody());
	}

	@Override
	public void exportMetadata(String jobName, String exportFileName) {
		logger.info("Launching export metadata job: {}", jobName);
		String url = this.context.getBaseUrl() + "applications/{application}/jobs";
		JobLaunchPayload payload = new JobLaunchPayload("EXPORT_METADATA", jobName);

		Map<String, String> params = new HashMap<String, String>();
		params.put("exportZipFileName", "test.zip");
		// can add 'importFileName' to parameters on payload object if we want
		// (the name of a CSV, ZIP, or TXT file). In case of ZIP, the ZIP can
		// contain 1+ CSV files
		// such as data1-3, data2-3, data3-3.csv, etc.
		// ResponseEntity<JobLaunchResponse> output =
		// this.context.getTemplate().postForEntity(url, payload,
		// JobLaunchResponse.class, appMap);
		ResponseEntity<String> output = this.context.getTemplate().postForEntity(url, payload, String.class, appMap);
		System.out.println("export resp: " + output.getBody());
	}

	public void getTest() {
		logger.info("Fetching test");
		String url = this.context.getBaseUrl() + "applications/{application}/userpreferences";
		ResponseEntity<String> userPrefs = this.context.getTemplate().getForEntity(url, String.class,
				application.getName());
		System.out.println("User prefs: " + userPrefs.getBody());
	}

	public DataSlice exportDataSlice(String planType, ExportDataSlice dataSlice) {
		logger.info("Exporting data slice from plan type {}", planType);
		String url = this.context.getBaseUrl() + "applications/{application}/plantypes/{planType}/exportdataslice";
		ResponseEntity<DataSlice> slice = this.context.getTemplate().postForEntity(url, dataSlice, DataSlice.class, application.getName(), planType);
		logger.info("Slice: {}", slice);
		return slice.getBody();
	}

	private static class MemberAdd {

		private String memberName;

		private String parentName;

		public MemberAdd(String memberName, String parentName) {
			super();
			this.memberName = memberName;
			this.parentName = parentName;
		}

		public String getMemberName() {
			return memberName;
		}

		public void setMemberName(String memberName) {
			this.memberName = memberName;
		}

		public String getParentName() {
			return parentName;
		}

		public void setParentName(String parentName) {
			this.parentName = parentName;
		}

	}

}
