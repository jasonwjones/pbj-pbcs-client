package com.jasonwjones.pbcs.client.exceptions;

import java.io.IOException;
import java.io.Serializable;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.http.client.ClientHttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Base class from which all PBCS Client exception should be derived from.
 *
 * @author jasonwjones
 *
 */
public class PbcsClientException extends RuntimeException {

	/**
	 * Constructs an instance with the given message.
	 *
	 * @param message the detail message
	 */
	public PbcsClientException(String message) {
		super(message);
	}

	/**
	 * Constructs an instance with the given message and cause.
	 *
	 * @param message the detail message
	 * @param cause the cause
	 */
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

	/**
	 * Maps the various shapes of error payload the PBCS REST API can return, used by {@link #createException}
	 * to build a {@link PbcsGeneralException}.
	 */
	public static class PbcsErrorResponse implements Serializable {

		// some errors that come back seem to erroneously have a field named 'detail'. If/when we upgrade to dependencies
		// that include a version of Jackson that is 2.9+, we might consider using JsonAlias to help with this
		/**
		 * The error details.
		 */
		private String details;

		/**
		 * The numeric status code of the error.
		 */
		private int status;
		/**
		 * The error message.
		 */
		private String message;
		/**
		 * The localized error message.
		 */
		private String localizedMessage;
		/**
		 * A human-readable status description.
		 */
		private String descriptiveStatus;
		/**
		 * The ID of the job associated with this error, if any.
		 */
		private String jobId;
		/**
		 * The name of the job associated with this error, if any.
		 */
		private String jobName;
		/**
		 * The raw links value associated with this error, if any.
		 */
		private String links;

		/**
		 * Constructs an empty instance for deserialization.
		 */
		public PbcsErrorResponse() {
		}

		/**
		 * Gets the error details.
		 *
		 * @return the details
		 */
		public String getDetails() {
			return details;
		}

		/**
		 * Sets the error details.
		 *
		 * @param details the details
		 */
		public void setDetails(String details) {
			this.details = details;
		}

		/**
		 * Gets the numeric status code of the error.
		 *
		 * @return the status code
		 */
		public int getStatus() {
			return status;
		}

		/**
		 * Sets the numeric status code of the error.
		 *
		 * @param status the status code
		 */
		public void setStatus(int status) {
			this.status = status;
		}

		/**
		 * Gets the error message.
		 *
		 * @return the message
		 */
		public String getMessage() {
			return message;
		}

		/**
		 * Sets the error message.
		 *
		 * @param message the message
		 */
		public void setMessage(String message) {
			this.message = message;
		}

		/**
		 * Gets the localized error message.
		 *
		 * @return the localized message
		 */
		public String getLocalizedMessage() {
			return localizedMessage;
		}

		/**
		 * Sets the localized error message.
		 *
		 * @param localizedMessage the localized message
		 */
		public void setLocalizedMessage(String localizedMessage) {
			this.localizedMessage = localizedMessage;
		}

		/**
		 * Gets a human-readable status description.
		 *
		 * @return the descriptive status
		 */
		public String getDescriptiveStatus() {
			return descriptiveStatus;
		}

		/**
		 * Sets the human-readable status description.
		 *
		 * @param descriptiveStatus the descriptive status
		 */
		public void setDescriptiveStatus(String descriptiveStatus) {
			this.descriptiveStatus = descriptiveStatus;
		}

		/**
		 * Gets the ID of the job associated with this error, if any.
		 *
		 * @return the job ID, may be null
		 */
		public String getJobId() {
			return jobId;
		}

		/**
		 * Sets the ID of the job associated with this error.
		 *
		 * @param jobId the job ID
		 */
		public void setJobId(String jobId) {
			this.jobId = jobId;
		}

		/**
		 * Gets the name of the job associated with this error, if any.
		 *
		 * @return the job name, may be null
		 */
		public String getJobName() {
			return jobName;
		}

		/**
		 * Sets the name of the job associated with this error.
		 *
		 * @param jobName the job name
		 */
		public void setJobName(String jobName) {
			this.jobName = jobName;
		}

		/**
		 * Gets the raw links value associated with this error, if any.
		 *
		 * @return the links, may be null
		 */
		public String getLinks() {
			return links;
		}

		/**
		 * Sets the raw links value associated with this error.
		 *
		 * @param links the links
		 */
		public void setLinks(String links) {
			this.links = links;
		}

	}

}