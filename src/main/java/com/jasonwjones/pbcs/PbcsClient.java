package com.jasonwjones.pbcs;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/*
 * epmautomate login serviceAdmin P@ssw0rd https://test-cloud-pln.pbcs.us1.oraclecloud.com
myIdentityDomain
epmautomate uploadfile accounts.zip
epmautomate importmetadata accountMetadata accounts.zip
epmautomate refreshcube
epmautomate logout
 */

public class PbcsClient {
	/*
	public static void main(String[] args) {
		RestTemplate restTemplate = new RestTemplate();
		String endpoint = "foo";
		String version = "11.1.2.3.600";
		String server = "https://plantastic-test-keyperformanceideas.pbcs.us2.oraclecloud.com/workspace";
		
		//restTemplate.getForObject(endpoint + "￼￼￼￼￼￼￼￼￼￼￼￼￼￼￼￼￼￼￼￼￼￼￼￼/HyperionPlanning/rest/{version}/applications/{appName}/jobs", Object.class);
		HttpClient httpClient = HttpClientBuilder.create().build();
		
		HttpHost httpHost = new HttpHost("plantastic-test-keyperformanceideas.pbcs.us2.oraclecloud.com/workspace", 443, "https");
		//new HttpHost()
		final AuthHttpComponentsClientHttpRequestFactory requestFactory =
			    new AuthHttpComponentsClientHttpRequestFactory(
			                httpClient, httpHost, "username", "password");
		
		// INITIAL POST
		String testPost = "https://plantastic-test-keyperformanceideas.pbcs.us2.oraclecloud.com/HyperionPlanning";
		ResponseEntity<String> result = restTemplate.postForEntity(testPost, null, String.class);
		//restTemplate.postF
		System.out.println("Status code: " + result.getStatusCode());
		System.out.println("Headers: " + result.getHeaders());
		System.out.println("Cookie: " + result.getHeaders().getFirst(HttpHeaders.SET_COOKIE));
		HttpHeaders headers = result.getHeaders();
		System.out.println("Location: " + headers.getLocation());
		
		// GET TO OAM FROM POST
		URI oamUrl = headers.getLocation();
		ResponseEntity<String> oam = restTemplate.getForEntity(oamUrl, String.class);

		System.out.println("OAM Status code: " + oam.getStatusCode());
		System.out.println("OAM Headers: " + oam.getHeaders());
		List<String> cookieTest = oam.getHeaders().get(HttpHeaders.SET_COOKIE);
		for (String cookie : oam.getHeaders().get(HttpHeaders.SET_COOKIE)) {
			System.out.println("Cookie: " + cookie);
		}
		//System.out.println("OAM Body: " + oam.getBody());

		String body = oam.getBody();
		int reqIdOffset = body.indexOf("name=\"request_id\"") + 25;
		System.out.println("Offset: " + reqIdOffset);
		int reqIdEndOffset = body.indexOf("\"", reqIdOffset);
		String reqId = body.substring(reqIdOffset, reqIdEndOffset);
		System.out.println("Red ID1: " + reqId);
		reqId = reqId.replace("#45;", "-");
		
		System.out.println("Request ID: " + reqId);
		
		System.out.println("---------");
		
		
		
		//System.exit(0);
		
		// NOW POST BACK TO OAM
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
		map.add("username", "jjones@keyperformanceideas.com");
		map.add("password", "Ja$on100");
		map.add("tenantName", "keyperformanceideas");
		map.add("request_id", reqId);
		
		HttpClient client = new HttpClient();
		PostMethod loginForm = new PostMethod("https://login.us2.oraclecloud.com:443/oam/server/auth_cred_submit");

		loginForm.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
		for (String cookie : cookieTest) {
			System.out.println("Adding cooking: " + cookie);
			loginForm.addRequestHeader("Cookie", "$Version=0; " + cookie);
		}

		//loginForm.addRequestHeader("Cookie", "$Version=0; OAM_REQ_0=VERSION_4~lGShz7zJInA38e3uhnZPlZmKh");
		//loginForm.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
		//PostMethod loginForm = new PostMethod(oamUrl.toString());
		loginForm.addParameter("username", "jjones@keyperformanceideas.com");
		loginForm.addParameter("password", "Ja$on100");
		loginForm.addParameter("request_id", reqId);
		loginForm.addParameter("tenantName", "keyperformanceideas");
		loginForm.addRequestHeader("Expect", "100-continue");
		loginForm.addRequestHeader("Connection", "Close");
		loginForm.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");


		try {
			int status = client.executeMethod(loginForm);
			System.out.println("Status: " + status);
			
			Header[] heads = loginForm.getResponseHeaders();
			for (Header head : heads) {
				System.out.println(head.getName() + " ---> " + head.getValue());
			}
			
		} catch (HttpException e) {
			System.out.println("Exception: " + e);
		} catch (IOException e) {
			System.out.println("Exception: " + e);
		}
		
		System.out.println();
		System.out.println("------- sso attempt -------");
		System.out.println();
		
		//
		// NOW TRY TO GET THE SSO TOKEN
		
		String interop = "https://plantastic-test-keyperformanceideas.pbcs.us2.oraclecloud.com/interop/logon";
		
		loginForm = new PostMethod(interop);
		//loginForm.setFollowRedirects(true);
		System.out.println("Follow redirects: " + loginForm.getFollowRedirects());

		loginForm.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
		for (String cookie : cookieTest) {
			System.out.println("Adding cooking: " + cookie);
			loginForm.addRequestHeader("Cookie", "$Version=0; " + cookie);
		}

		//loginForm.addRequestHeader("Cookie", "$Version=0; OAM_REQ_0=VERSION_4~lGShz7zJInA38e3uhnZPlZmKh");
		//loginForm.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
		//PostMethod loginForm = new PostMethod(oamUrl.toString());
		loginForm.addParameter("sso_username", "jjones@keyperformanceideas.com");
		loginForm.addParameter("sso_password", "Ja$on100");
		loginForm.addParameter("request_id", reqId);
		loginForm.addParameter("tenantName", "keyperformanceideas");
		loginForm.addRequestHeader("Expect", "100-continue");
		loginForm.addRequestHeader("Connection", "Close");
		loginForm.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");

		try {
			int status = client.executeMethod(loginForm);
			System.out.println("Status: " + status);
			
			Header[] heads = loginForm.getResponseHeaders();
			for (Header head : heads) {
				System.out.println(head.getName() + " ---> " + head.getValue());
			}
			System.out.println("Body: " + loginForm.getResponseBodyAsString());
		} catch (HttpException e) {
			System.out.println("Exception: " + e);
		} catch (IOException e) {
			System.out.println("Exception: " + e);
		}
		
		//////////////
		
		String newLoc = loginForm.getResponseHeader("Location").getValue();
		System.out.println("Redirect location: " + newLoc);
		
		
		
		loginForm = new PostMethod(newLoc);
		//loginForm.setFollowRedirects(true);
		System.out.println("Follow redirects: " + loginForm.getFollowRedirects());

		loginForm.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
		for (String cookie : cookieTest) {
			System.out.println("Adding cooking: " + cookie);
			loginForm.addRequestHeader("Cookie", "$Version=0; " + cookie);
		}

		//loginForm.addRequestHeader("Cookie", "$Version=0; OAM_REQ_0=VERSION_4~lGShz7zJInA38e3uhnZPlZmKh");
		//loginForm.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
		//PostMethod loginForm = new PostMethod(oamUrl.toString());
		loginForm.addParameter("sso_username", "jjones@keyperformanceideas.com");
		loginForm.addParameter("sso_password", "Ja$on100");
		loginForm.addParameter("request_id", reqId);
		loginForm.addParameter("tenantName", "keyperformanceideas");
		loginForm.addRequestHeader("Expect", "100-continue");
		loginForm.addRequestHeader("Connection", "Close");
		loginForm.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");

		try {
			int status = client.executeMethod(loginForm);
			System.out.println("Status: " + status);
			
			Header[] heads = loginForm.getResponseHeaders();
			for (Header head : heads) {
				System.out.println(head.getName() + " ---> " + head.getValue());
			}
			System.out.println("Body: " + loginForm.getResponseBodyAsString());
		} catch (HttpException e) {
			System.out.println("Exception: " + e);
		} catch (IOException e) {
			System.out.println("Exception: " + e);
		}
		
		
		
		System.exit(0);
		
		//HttpClient client = HttpClients.createDefault();
		//client.e
		
		//
//		String loc = "https://login.us2.oraclecloud.com:443/oam/server/auth_cred_submit";
//		ResponseEntity<String> oamResult = restTemplate.postForEntity(loc, map, String.class);
//		System.out.println("OAM Result Status code: " + oamResult.getStatusCode());
//		System.out.println("OAM Result Headers: " + oamResult.getHeaders());
//		System.out.println("Cookie: " + oamResult.getHeaders().getFirst(HttpHeaders.SET_COOKIE));
//		//System.out.println("OAM Result Body: " + oamResult.getBody());
		
		System.exit(0);
		
//		String plainCreds = "willie:p@ssword";
//		byte[] plainCredsBytes = plainCreds.getBytes();
//		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
//		String base64Creds = new String(base64CredsBytes);
//		HttpHeaders authHeaders = new HttpHeaders();
//		authHeaders.add("Authorization", "Basic " + base64Creds);
//		HttpEntity<String> authRequest = new HttpEntity<String>(authHeaders);
		
//		//String jobsUrl = "https://plantastic-test-keyperformanceideas.pbcs.us2.oraclecloud.com/HyperionPlanning/rest/11.1.2.3.600/applications/FOO/jobs";
//		String jobsUrl = "https://plantastic-test-keyperformanceideas.pbcs.us2.oraclecloud.com/HyperionPlanning/rest/11.1.2.3.600/applicationSnapshotsAA";
//		ResponseEntity<String> jobs = restTemplate.exchange(jobsUrl, HttpMethod.GET, authRequest, String.class);
//		//ResponseEntity<String> jobs = restTemplate.getForEntity(jobsUrl, );
//		System.out.println("Jobs status: " + jobs.getStatusCode());
//		System.out.println("Jobs headers: " + jobs.getHeaders());
//		System.out.println("Jobs output: " + jobs.getBody());
//		
//		// https://login.us2.oraclecloud.com:443/oam/server/obrareq.cgi?encquery%3DCV%2BqtqVy57fT02gv9XpB%2F%2FxUUmlCtUnv%2Fg8kFUL5MwXpNDSYYwD%2FPMi7i3Y4dRdYZzUU1qgUDgHSmkA2Tug9sU8v7876orpad4%2F0%2Frn7DHqvS1i5CDiGMRRybwmSc7Ys%2BK%2Bwo91KLEhZte7RZ6j%2F2qsBybYKa5BqS28XIca%2Bxf81Bt%2FvrjkhgowWEQdPtT6Q%2F0ZZzsFggkhYslsS%2FoIekZYMf3l6QSGWlp4x2p%2Fr0nHSEnuwK%2F82RSkIAtJ6PfeDd6Yz4wVUg%2Fc%2FnBhDks%2FHKRk9HQYu69BxKN1%2BA8wV41I%2BLBcVdlF%2F9fkn7%2Fz35DctHlWfisfunAQpEDxakN80kBZrHnldfOM7gk69PH5epBc%3D%20agentid%3DPlanning_WG%20ver%3D1%20crmethod%3D2
//		//System.out.println("Result: " + result.getBody());
		
	}
	*/
}
