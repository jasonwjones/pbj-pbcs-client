package com.jasonwjones.pbcs.client.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import com.jasonwjones.pbcs.api.v3.*;
import com.jasonwjones.pbcs.client.*;
import com.jasonwjones.pbcs.client.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpServerErrorException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasonwjones.pbcs.aif.AifApplication;
import com.jasonwjones.pbcs.aif.AifDimension;
import com.jasonwjones.pbcs.api.v3.dataslices.DataSlice;
import com.jasonwjones.pbcs.api.v3.dataslices.ExportDataSlice;
import com.jasonwjones.pbcs.api.v3.PbcsMemberPropertiesImpl;
import com.jasonwjones.pbcs.interop.impl.SimpleFilenameUtils;

public class PbcsApplicationImpl extends AbstractPbcsObject implements PbcsApplication {

	private static final Logger logger = LoggerFactory.getLogger(PbcsApplicationImpl.class);

	private static final String JOBS_ENDPOINT = "applications/{application}/jobs";

	private final PbcsPlanningClient client;

	private final Application application;

	public PbcsApplicationImpl(RestContext context, PbcsPlanningClient client, Application application) {
		super(context);
		this.client = client;
		this.application = application;
	}

	@Override
	public List<PbcsJobDefinition> getJobDefinitions() {
		logger.info("Getting job definitions for {}", getName());
		JobDefinitionsWrapper jobDefinitions = get("applications/{application}/jobdefinitions", JobDefinitionsWrapper.class, getName());

		List<PbcsJobDefinition> pbcsJobDefs = new ArrayList<>();
		for (JobDefinition jobDefinition : jobDefinitions.getItems()) {
			PbcsJobDefinition pbcsJobDef = new PbcsJobDefinitionImpl(context, jobDefinition);
			pbcsJobDefs.add(pbcsJobDef);
		}
		return pbcsJobDefs;
	}

	@Override
	public List<PbcsJobDefinition> getJobDefinitions(PbcsJobType jobType) {
		// someone messed up how the payload for this call should look like, but we craft this fake 'variable' to stick
		// on to the query string and make it work
		String jobTypeFragment = "{'jobType':'" + jobType.name() + "'}";
		JobDefinitionsWrapper jobDefinitions = get("applications/{application}/jobdefinitions?q={jobTypeFragment}", JobDefinitionsWrapper.class, getName(), jobTypeFragment);
		List<PbcsJobDefinition> pbcsJobDefs = new ArrayList<>();
		for (JobDefinition jobDefinition : jobDefinitions.getItems()) {
			PbcsJobDefinition pbcsJobDef = new PbcsJobDefinitionImpl(context, jobDefinition);
			pbcsJobDefs.add(pbcsJobDef);
		}
		return pbcsJobDefs;
	}

	@Override
	public PbcsJobStatus getJobStatus(Integer jobId) {
		try {
			JobLaunchResponse jobLaunchResponse = get("applications/{application}/jobs/{jobId}", JobLaunchResponse.class, getName(), jobId);
			return new PbcsJobLaunchResultImpl(this, jobLaunchResponse);
		} catch (HttpServerErrorException e) {
			throw new PbcsClientException("Error fetching job with status ID " + jobId + ". Perhaps it doesn't exist?");
		}
	}

	@Override
	public PbcsJobStatus launchBusinessRule(String ruleName) {
		return launchBusinessRule(ruleName, new HashMap<>());
	}

	@Override
	public PbcsJobStatus launchBusinessRule(String ruleName, Map<String, String> parameters) {
		String url = this.context.getBaseUrl() + JOBS_ENDPOINT;
		MetadataImportPayload payload = new MetadataImportPayload("RULES", ruleName);
		payload.setParameters(parameters);
		try {
			logger.info("Launching {} business rule {} with parameters {}", getName(), ruleName, parameters);
			ResponseEntity<JobLaunchResponse> output = this.context.getTemplate().postForEntity(url, getRequestEntityWithHeaders(payload),
					JobLaunchResponse.class, application.getName());
			logger.info("Launched business rule, job ID is {}", output.getBody().getJobId());
			return new PbcsJobLaunchResultImpl(this, output.getBody());
		} catch (Exception e) {
			logger.error("Exception launching business rule {}", ruleName);
			throw new PbcsJobLaunchException(ruleName, e);
		}
	}

	@Override
	public PbcsJobStatus launchRuleSet(String ruleSetName) {
		return launchRuleSet(ruleSetName, Collections.emptyMap());
	}

	@Override
	public PbcsJobStatus launchRuleSet(String ruleSetName, Map<String, String> parameters) {
		String url = context.getBaseUrl() + JOBS_ENDPOINT;
		JobLaunchPayload payload = new JobLaunchPayload("RULESET", ruleSetName);
		payload.setParameters(parameters);
		HttpEntity<?> requestEntity = getRequestEntityWithHeaders(payload);
		ResponseEntity<JobLaunchResponse> output = context.getTemplate().postForEntity(url, requestEntity, JobLaunchResponse.class, getName());
		return new PbcsJobLaunchResultImpl(this, output.getBody());
	}

	@Override
	public PbcsJobStatus launchDataRule(String dataRuleName, Map<String, String> parameters) {
		String url = context.getAifUrl("/jobs");
		JobLaunchPayload payload = new JobLaunchPayload("DATARULE", dataRuleName);
		payload.setParameters(parameters);
		HttpEntity<?> requestEntity = getRequestEntityWithHeaders(payload);
		ResponseEntity<JobLaunchResponse> output = context.getTemplate().postForEntity(url, requestEntity, JobLaunchResponse.class, getName());
		return new PbcsJobLaunchResultImpl(this, output.getBody());
	}

	@Override
	public PbcsJobStatus launchIntegration(String integrationName, Map<String, String> parameters) {
		String url = context.getAifUrl("/jobs");
		JobLaunchPayload payload = new JobLaunchPayload("INTEGRATION", integrationName);
		payload.setParameters(parameters);
		HttpEntity<?> requestEntity = getRequestEntityWithHeaders(payload);
		ResponseEntity<JobLaunchResponse> output = context.getTemplate()
														  .postForEntity(url, requestEntity, JobLaunchResponse.class, getName());
		return new PbcsJobLaunchResultImpl(this, output.getBody());
	}

	private HttpEntity<?> getRequestEntityWithHeaders(Payload payload) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			return new HttpEntity<>(new ObjectMapper().writer()
															.withDefaultPrettyPrinter()
															.writeValueAsString(payload), headers);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Cannot map object to json", e);
		}
	}

	@Override
	public PbcsJobStatus importMetadata(String metadataImportName) {
		return importMetadata(metadataImportName, null);
	}

	@Override
	public PbcsJobStatus importMetadata(String metadataImportName, String dataFile) {
		logger.info("Launching metadata import data job: {}", metadataImportName);
		String url = this.context.getBaseUrl() + JOBS_ENDPOINT;
		MetadataImportPayload payload = new MetadataImportPayload("IMPORT_METADATA", metadataImportName);

		// "parameters" var is optional if not specifying. If it's specified,
		// then it should
		// be a zip file
		if (dataFile != null) {
			Map<String, String> params = new HashMap<>();
			if (SimpleFilenameUtils.getExtension(dataFile) != null && SimpleFilenameUtils.getExtension(dataFile).equalsIgnoreCase("zip")) {
				params.put("importZipFileName", dataFile);
			}
			else {
				params.put("importFileName", dataFile);
			}
			payload.setParameters(params);
		}
		ResponseEntity<JobLaunchResponse> output = this.context.getTemplate().postForEntity(url, getRequestEntityWithHeaders(payload), JobLaunchResponse.class, getName());
		return new PbcsJobLaunchResultImpl(this, output.getBody());
	}

	@Override
	public PbcsJobStatus launchDataImport(String dataImportName) {
		return this.launchDataImport(dataImportName, Optional.empty());
	}
	@Override
	public PbcsJobStatus launchDataImport(String dataImportName, Optional<String> importFileName) {
		logger.info("Launching import data job: {}", dataImportName);
		String url = this.context.getBaseUrl() + JOBS_ENDPOINT;
		JobLaunchPayload payload = new JobLaunchPayload("IMPORT_DATA", dataImportName);
		if (importFileName.isPresent()){
			if (SimpleFilenameUtils.getExtension(importFileName.get()) != null && SimpleFilenameUtils.getExtension(importFileName.get()).equals("zip")){
				payload.setParameters(Collections.singletonMap("importZipFileName", importFileName.get()));
			}
			else {
				payload.setParameters(Collections.singletonMap("importFileName", importFileName.get()));
			}
		}
		// can add 'importFileName' to parameters on payload object if we want
		// (the name of a CSV, ZIP, or TXT file). In case of ZIP, the ZIP can
		// contain 1+ CSV files
		// such as data1-3, data2-3, data3-3.csv, etc.
		RequestEntity<JobLaunchPayload> body = RequestEntity.post(context.getTemplate()
																		 .getUriTemplateHandler()
																		 .expand(url, getName()))
															.contentType(MediaType.APPLICATION_JSON)
															.body(payload);
		ResponseEntity<JobLaunchResponse> output = context.getTemplate()
															.exchange(body, JobLaunchResponse.class);

		return new PbcsJobLaunchResultImpl(this, output.getBody());
	}

	@Override
	public PbcsJobStatus exportData(String exportName) {
		String url = this.context.getBaseUrl() + JOBS_ENDPOINT;
		JobLaunchPayload payload = new JobLaunchPayload("EXPORT_DATA", exportName);
		ResponseEntity<JobLaunchResponse> output = this.context.getTemplate().postForEntity(url, payload,
				JobLaunchResponse.class, getName());
		logger.info("Export data HTTP code: {}", output.getStatusCode().value());
		return new PbcsJobLaunchResultImpl(this, output.getBody());
	}

	@Override
	public PbcsJobStatus refreshCube() {
		return refreshCube("CubeRefresh");
	}

	@Override
	public PbcsJobStatus refreshCube(String cubeRefreshName) {
		String url = this.context.getBaseUrl() + JOBS_ENDPOINT;
		MetadataImportPayload payload = new MetadataImportPayload("CUBE_REFRESH", cubeRefreshName);
		ResponseEntity<JobLaunchResponse> output = this.context.getTemplate().postForEntity(url, getRequestEntityWithHeaders(payload),
				JobLaunchResponse.class, getName());
		logger.info("Cube refresh launched");
		return new PbcsJobLaunchResultImpl(this, output.getBody());
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
	public PbcsObjectType getObjectType() {
		return PbcsObjectType.APPLICATION;
	}

	@Override
	public String getType() {
		return application.getType();
	}

	@Override
	public PbcsAppType getAppType() {
		for (PbcsAppType appType : PbcsAppType.values()) {
			if (appType.getCode().equals(application.getAppType())) {
				return appType;
			}
		}
		logger.warn("Unknown app type: {}", application.getAppType());
		return null;
	}

	@Override
	public Set<SubstitutionVariable> getSubstitutionVariables() {
		logger.info("Getting substitution variables for {}", application.getName());
		String url = this.context.getBaseUrl() + "applications/{application}/substitutionvariables";
		ResponseEntity<SubstitutionVariablesWrapper> response = this.context.getTemplate().getForEntity(url, SubstitutionVariablesWrapper.class, getName());
		return new HashSet<>(response.getBody().getItems());
	}

	@Override
	public SubstitutionVariable getSubstitutionVariable(String variableName) {
		logger.info("Retrieving value of substitution variable");
		for (SubstitutionVariable subVar : getSubstitutionVariables()) {
			if (subVar.getName().equals(variableName)) {
				return subVar;
			}
		}
		throw new PbcsNoSuchVariableException(application.getName(), variableName);
	}

	@Override
	public void updateSubstitutionVariables(Collection<SubstitutionVariable> variables) {
		SubstitutionVariableUpdateWrapper subs = new SubstitutionVariableUpdateWrapper();
		subs.setItems(new ArrayList<>(variables));
		post("applications/{application}/substitutionariables", subs, String.class, getName());
	}

	@Override
	public void updateSubstitutionVariable(String name, String value) {
		Collection<SubstitutionVariable> subVar = Collections.singletonList(new SubstitutionVariable(name, value));
		updateSubstitutionVariables(subVar);
	}

	/**
	 * CREST doc: Adds a new member to the application outline in the specified
	 * dimension and plan type and under the specified parent member.
	 * Prerequisite: The parent member must be enabled for dynamic children and
	 * a cube refresh must have happened after the parent was enabled.
	 */

	// TODO: currently getting a BAD request 400 possibly because it's not enable dynamic children.
	@Override
	public PbcsMemberProperties addMember(String dimensionName, String memberName, String parentName) {
		String url = this.context.getBaseUrl() + "applications/{application}/dimensions/{dimName}/members";

		MemberAdd ma = new MemberAdd(memberName, parentName);

		this.context.getTemplate().setErrorHandler(new MyResponseErrorHandler());
		ResponseEntity<String> resp = this.context.getTemplate().postForEntity(url, ma, String.class,
				application.getName(), dimensionName);

		// ResponseEntity<PbcsMemberPropertiesImpl> memberResponse =
		// this.context.getTemplate().getForEntity(url,
		// PbcsMemberPropertiesImpl.class, application.getName(), dimensionName,
		// memberName);
		// return memberResponse.getBody();

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PbcsMember getMember(String dimensionName, String memberName) {
		Assert.hasText(dimensionName, "Must specify a dimension name");
		Assert.hasText(memberName, "Must specify a member name");

		logger.debug("Fetching member properties for {} from dimension {}", memberName, dimensionName);
		try {
			PbcsMemberPropertiesImpl properties = get("applications/{application}/dimensions/{dimName}/members/{member}", PbcsMemberPropertiesImpl.class, getName(), dimensionName, memberName);
			return new PbcsMemberImpl(this, properties);
		} catch (PbcsGeneralException e) {
			// catch a general exception that is intercepted by the error handler, and confirm that it's about no such
			// member. If so, let's throw a better error that the object doesn't exist. If not, we'll just rethrow it.
			// I don't usually like to alter behavior based on the contents of an error message (which could conceivably
			// change, but we're only getting a lame 400 error instead of a 404, and I'm not comfortable just arbitrarily
			// wrapping the exception no matter what, as we could end up with a NoSuchObjectFound exception occluding
			// something more fundamental going on
			if (e.getMessage() != null && e.getMessage().toLowerCase().startsWith("the member")) {
				throw new PbcsInvalidMemberException(memberName);
			} else if (e.getMessage() != null && e.getMessage().toLowerCase().startsWith("the dimension")) {
				throw new PbcsInvalidDimensionException(memberName);
			} else {
				throw e;
			}
		}
	}

	public UserPreferences getUserPreferences() {
		return get("applications/{application}/userpreferences", UserPreferences.class, getName());
	}

	@Override
	public void exportMetadata(String jobName, String exportFileName) {
		logger.info("Launching export metadata job: {}", jobName);
		String url = this.context.getBaseUrl() + JOBS_ENDPOINT;
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
		ResponseEntity<String> output = this.context.getTemplate().postForEntity(url, payload, String.class, getName());
		System.out.println("export resp: " + output.getBody());
	}

	@Override
	public DataSlice exportDataSlice(String planType, ExportDataSlice dataSlice) {
		logger.info("Exporting data slice from plan {}", planType);
		return post("applications/{application}/plantypes/{planType}/exportdataslice", dataSlice, DataSlice.class, getName(), planType);
	}

	@Override
	public String toString() {
		return "PbcsApplicationImpl[application=" + application + "]";
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
		throw new PbcsInvalidDimensionException(dimensionName);
	}

	@Override
	public List<PbcsDimension> getDimensions(String planType) {
		throw new UnsupportedOperationException();
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
		return validatePlanType(planTypeName);
	}

	public PbcsPlanType getPlanType(String planTypeName, boolean skipCheck) {
		return getPlanType(planTypeName, skipCheck, Collections.emptyList());
	}

	@Deprecated
	public PbcsPlanType getPlanType(String planTypeName, boolean skipCheck, List<String> dimensions) {
		PlanTypeConfigurationImpl configuration = new PlanTypeConfigurationImpl();
		configuration.setName(planTypeName);
		configuration.setSkipCheck(skipCheck);
		configuration.setExplicitDimensions(dimensions);
		return getPlanType(configuration);
	}

	@Override
	public PbcsPlanType getPlanType(PlanTypeConfiguration configuration) {
		if (!configuration.isSkipCheck()) {
			validatePlanType(configuration.getName());
		}
		if (configuration.getExplicitDimensions() != null && !configuration.getExplicitDimensions().isEmpty()) {
			return new PbcsExplicitDimensionsPlanTypeImpl(context, this, configuration);
		} else {
			return new PbcsPlanTypeImpl(context, this, configuration.getName(), configuration.getMemberDimensionCache());
		}
	}

	/**
	 * Gets the list of plans for this application and ensures that the given plan type name is actually in it.
	 *
	 * @param planTypeName the name of the plan to validate
	 */
	private PbcsPlanType validatePlanType(String planTypeName) {
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
		throw new PbcsNoSuchObjectException(planTypeName, PbcsObjectType.PLAN);
	}

}