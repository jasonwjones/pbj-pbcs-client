package com.jasonwjones.pbcs.client.exceptions;

import java.io.IOException;

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
@SuppressWarnings("serial")
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
		try {
			PbcsErrorResponse errorResponse = mapper.readValue(responseBody, PbcsErrorResponse.class);
			return new PbcsGeneralException(errorResponse);
		} catch (JsonParseException e) {
			return new PbcsClientException("PBJ General Error", e);
		} catch (JsonMappingException e) {
			return new PbcsClientException("PBJ General Error", e);
		} catch (IOException e) {
			return new PbcsClientException("PBJ General Error", e);
		}
	
	}

	public static class PbcsErrorResponse {

		private String detail;
		private int status;
		private String message;
		private String localizedMessage;

		public String getDetail() {
			return detail;
		}

		public void setDetail(String detail) {
			this.detail = detail;
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
		
	}

}
