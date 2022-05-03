package com.jasonwjones.pbcs.client.impl;

import java.util.*;

import com.jasonwjones.pbcs.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpServerErrorException;

import com.jasonwjones.pbcs.aif.AifApplication;
import com.jasonwjones.pbcs.aif.AifDimension;
import com.jasonwjones.pbcs.api.v3.Application;
import com.jasonwjones.pbcs.api.v3.JobDefinition;
import com.jasonwjones.pbcs.api.v3.JobDefinitionsWrapper;
import com.jasonwjones.pbcs.api.v3.JobLaunchPayload;
import com.jasonwjones.pbcs.api.v3.JobLaunchResponse;
import com.jasonwjones.pbcs.api.v3.SubstitutionVariable;
import com.jasonwjones.pbcs.api.v3.SubstitutionVariablesWrapper;
import com.jasonwjones.pbcs.api.v3.SubstiutionVariableUpdateWrapper;
import com.jasonwjones.pbcs.api.v3.dataslices.DataSlice;
import com.jasonwjones.pbcs.api.v3.dataslices.ExportDataSlice;
import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;
import com.jasonwjones.pbcs.client.exceptions.PbcsNoSuchObjectException;
import com.jasonwjones.pbcs.client.exceptions.PbcsNoSuchVariableException;
import com.jasonwjones.pbcs.client.impl.models.PbcsMemberPropertiesImpl;

public class PbcsApplicationImpl implements PbcsApplication {

	private static final Logger logger = LoggerFactory.getLogger(PbcsApplicationImpl.class);

	private final RestContext context;

	private final PbcsPlanningClient client;

	private final Application application;

	private final Map<String, String> appMap;

	public PbcsApplicationImpl(RestContext context, PbcsPlanningClient client, Application application) {
		this.context = context;
		this.client = client;
		this.application = application;
		this.appMap = new HashMap<>();
		this.appMap.put("application", application.getName());
	}

	@Override
	public List<PbcsJobDefinition> getJobDefinitions() {
		logger.info("Getting job definitions for {}", application.getName());
		String url = this.context.getBaseUrl() + "applications/{application}/jobdefinitions";
		ResponseEntity<JobDefinitionsWrapper> output = this.context.getTemplate().getForEntity(url,
				JobDefinitionsWrapper.class, appMap);

		JobDefinitionsWrapper jobDefinitions = output.getBody();
		List<PbcsJobDefinition> pbcsJobDefs = new ArrayList<>();
		for (JobDefinition jobDefinition : jobDefinitions.getItems()) {
			PbcsJobDefinition pbcsJobDef = new PbcsJobDefinitionImpl(context, jobDefinition);
			pbcsJobDefs.add(pbcsJobDef);
		}
		return pbcsJobDefs;
	}

	@Override
	public List<PbcsJobDefinition> getJobDefinitions(PbcsJobType jobType) {
		List<PbcsJobDefinition> filteredJobDefinitinos = new ArrayList<>();
		for (PbcsJobDefinition currentDef : getJobDefinitions()) {
			if (currentDef.getJobType().equals(jobType.name())) {
				filteredJobDefinitinos.add(currentDef);
			}
		}
		return filteredJobDefinitinos;
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
			Map<String, String> params = new HashMap<>();
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
	public PbcsPlanningClient getClient() {
		return client;
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
	public Set<SubstitutionVariable> getSubstitutionVariables() {
		logger.info("Getting substitution variables for {}", application.getName());
		String url = this.context.getBaseUrl() + "applications/{application}/substitutionvariables";
		ResponseEntity<SubstitutionVariablesWrapper> response = this.context.getTemplate().getForEntity(url, SubstitutionVariablesWrapper.class, appMap);
		return new HashSet<>(response.getBody().getItems());
	}

	@Override
	public SubstitutionVariable getSubstitutionVariable(String variableName) {
		logger.info("Retrieving value of substitution variable");
		for (SubstitutionVariable var : getSubstitutionVariables()) {
			if (var.getName().equals(variableName)) {
				return var;
			}
		}
		throw new PbcsNoSuchVariableException(application.getName(), variableName);
	}

	@Override
	public void updateSubstitutionVariables(Collection<SubstitutionVariable> variables) {
		String url = this.context.getBaseUrl() + "applications/{application}/substitutionvariables";
		SubstiutionVariableUpdateWrapper subs = new SubstiutionVariableUpdateWrapper();
		subs.setItems(new ArrayList<>(variables));
		ResponseEntity<String> resp = this.context.getTemplate().postForEntity(url, subs, String.class, appMap);
		//logger.info("Response: {}", resp.getHeaders());
		//System.out.println(resp.getBody());
	}

	@Override
	public void updateSubstitutionVariable(String name, String value) {
		Collection<SubstitutionVariable> var = Collections.singletonList(new SubstitutionVariable(name, value));
		updateSubstitutionVariables(var);
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

		logger.debug("Fetching member properties for {} from dimension {}", memberName, dimensionName);
		String url = this.context.getBaseUrl() + "applications/{application}/dimensions/{dimName}/members/{member}";
		// left over from trying to debug why some responses (that were too big?) were incomplete
		//String body = this.context.getTemplate().getForEntity(url, String.class, application.getName(), dimensionName, memberName).getBody();
		//logger.debug("Body: {} / {}", body.length(), body);
		ResponseEntity<PbcsMemberPropertiesImpl> memberResponse = this.context.getTemplate().getForEntity(url, PbcsMemberPropertiesImpl.class, application.getName(), dimensionName, memberName);
		//logger.debug("Headers: " + memberResponse.getHeaders());
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

		Map<String, String> params = new HashMap<>();
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

	@Override
	public String toString() {
		return "PbcsApplicationImpl [application=" + application + "]";
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

	@Override
	public List<PbcsAppDimension> getDimensions() {
		ResponseEntity<AifApplication> result = this.context.getTemplate().getForEntity(this.context.getAifUrl("/applications/" + this.application.getName()), AifApplication.class);

		AifApplication application = result.getBody();
		List<String> plans = Arrays.asList(application.getPlan1Name(), application.getPlan2Name(), application.getPlan3Name(), application.getPlan4Name(), application.getPlan5Name(), application.getPlan6Name());

		List<PbcsAppDimension> dimensions = new ArrayList<>();
		int dimIndex = 0;
		for (AifDimension aifDimension : result.getBody().getItems()) {
			List<Integer> validForPlans = Arrays.asList(aifDimension.getValidForPlan1(), aifDimension.getValidForPlan2(), aifDimension.getValidForPlan3(), aifDimension.getValidForPlan4(), aifDimension.getValidForPlan5(), aifDimension.getValidForPlan6());
			Set<String> plansForDim = new TreeSet<>();
			for (int validIndex = 0; validIndex < 6; validIndex++) {
				if (validForPlans.get(validIndex) == 1) {
					plansForDim.add(plans.get(validIndex));
				}
			}
			PbcsDimensionImpl dim = new PbcsDimensionImpl(plansForDim, this, aifDimension, dimIndex++);
			dimensions.add(dim);
		}
		return dimensions;
	}

	@Override
	public PbcsDimension getDimension(String dimensionName) {
		for (PbcsDimension dimension : getDimensions()) {
			if (dimension.getName().equals(dimensionName)) {
				return dimension;
			}
		}
		throw new PbcsNoSuchObjectException(dimensionName, "dimension");
	}

	// TODO: check that the plan type is actually valid
	@Override
	@Deprecated
	public List<PbcsDimension> getDimensions(String planType) {
		List<PbcsDimension> dimensions = new ArrayList<>();
//		for (PbcsDimension dimension : getDimensions()) {
//			if (dimension.isValidForPlan(planType)) {
//				dimensions.add(dimension);
//			}
//		}
		return dimensions;
	}

	@Override
	public List<PbcsPlanType> getPlanTypes() {
		ResponseEntity<AifApplication> result = this.context.getTemplate().getForEntity(this.context.getAifUrl("/applications/" + this.application.getName()), AifApplication.class);
		AifApplication application = result.getBody();
		List<PbcsPlanType> planTypes = new ArrayList<>();
		for (String plan : application.getAllPlans()) {
			PbcsPlanTypeImpl planTypeImpl = new PbcsPlanTypeImpl(context, this, plan);
			planTypes.add(planTypeImpl);
		}
		return planTypes;
	}

	@Override
	public PbcsPlanType getPlanType(String planTypeName) {
		List<PbcsPlanType> planTypes = getPlanTypes();
		for (PbcsPlanType planType : planTypes) {
			if (planType.getName().equals(planTypeName)) {
				return planType;
			}
		}
		List<String> availablePlanTypeNames = new ArrayList<>();
		for (PbcsPlanType planType : planTypes) {
			availablePlanTypeNames.add(planType.getName());
		}
		logger.warn("PBCS application {} does not contain plan type {}; available plan type names are {}", application.getName(), planTypeName, availablePlanTypeNames);
		throw new PbcsNoSuchObjectException(planTypeName, "plan type");
	}

	public PbcsPlanType getPlanType(String planTypeName, boolean skipCheck) {
		return getPlanType(planTypeName, skipCheck, Collections.emptyList());
	}

	public PbcsPlanType getPlanType(String planTypeName, boolean skipCheck, List<String> dimensions) {
		return new PbcsPlanTypeImpl(context, this, planTypeName, dimensions);
	}

}