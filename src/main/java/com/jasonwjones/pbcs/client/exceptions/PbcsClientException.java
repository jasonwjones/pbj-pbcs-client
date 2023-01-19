package com.jasonwjones.pbcs.client.exceptions;

import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.http.client.ClientHttpResponse;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Base class from which all PBCS Client exception should be derived from.
 *
 * @author jasonwjones
 *
 */
public class PbcsClientException extends RuntimeException {

	public PbcsClientException(String message) {
		super(message);
	}

	public PbcsClientException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Might get HTTP body like this:
	 *
	 * <pre>
	 * {@code
	 * {
	 *    "detail":"The dimension Time is invalid.",
	 *    "status":400,"message":"com.hyperion.planning.InvalidDimensionException: The dimension Time is invalid.",
	 *    "localizedMessage":"com.hyperion.planning.InvalidDimensionException: The dimension Time is invalid."
	 * }
	 * }
	 *
	 * </pre>
	 *
	 * Or like this (from importMetadata):
	 * <pre>
	 * {@code
	 * {
	 *     "descriptiveStatus":"Error",
	 *     "jobId":-1,
	 *     "status":1,
	 *     "details":null,
	 *     "jobName":null,
	 *     "links":null}
	 * }
	 * }
	 * </pre>
	 *
	 * With headers:
	 *
	 * Headers: {Date=[Wed, 04 May 2016 17:42:26 GMT], Server=[Oracle-Application-Server-11g], X-EPM_ACTION=[Member Retrieve], X-EPM_FUNCTION=[Planning], X-EPM_OBJECT=[], X-Powered-By=[Servlet/2.5 JSP/2.1], Vary=[Accept-Encoding,User-Agent], Connection=[close], Transfer-Encoding=[chunked], Content-Type=[application/json; charset=UTF-8], Content-Language=[en]}
	 * @param response the response object
	 * @param responseBody the textual response body
	 * @return a new exception
	 */
	public static PbcsClientException createException(ClientHttpResponse response, String responseBody) {
		// TODO: static
		ObjectMapper mapper = new ObjectMapper();
		// added because some exceptions seem to have 'detail' property, and JsonAlias isn't available yet (need Jackson 2.9+)
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			PbcsErrorResponse errorResponse = mapper.readValue(responseBody, PbcsErrorResponse.class);
			return new PbcsGeneralException(errorResponse);
		} catch (IOException e) {
			return new PbcsClientException("PBJ General Error", e);
		}
	}

	public static class PbcsErrorResponse {

		// some errors that come back seem to erroneously have a field named 'detail'. If/when we upgrade to dependencies
		// that include a version of Jackson that is 2.9+, we might consider using JsonAlias to help with this
		private String details;

		private int status;
		private String message;
		private String localizedMessage;
		private String descriptiveStatus;
		private String jobId;
		private String jobName;
		private String links;

		public String getDetails() {
			return details;
		}

		public void setDetails(String details) {
			this.details = details;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getLocalizedMessage() {
			return localizedMessage;
		}

		public void setLocalizedMessage(String localizedMessage) {
			this.localizedMessage = localizedMessage;
		}

		public String getDescriptiveStatus() {
			return descriptiveStatus;
		}

		public void setDescriptiveStatus(String descriptiveStatus) {
			this.descriptiveStatus = descriptiveStatus;
		}

		public String getJobId() {
			return jobId;
		}

		public void setJobId(String jobId) {
			this.jobId = jobId;
		}

		public String getJobName() {
			return jobName;
		}

		public void setJobName(String jobName) {
			this.jobName = jobName;
		}

		public String getLinks() {
			return links;
		}

		public void setLinks(String links) {
			this.links = links;
		}
	}
}