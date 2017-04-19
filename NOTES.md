# Get full(er) logging 

Run with VM args:

-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.SimpleLog
-Dorg.apache.commons.logging.simplelog.showdatetime=true
-Dorg.apache.commons.logging.simplelog.log.org.apache.http=DEBUG
-Dorg.apache.commons.logging.simplelog.log.org.apache.http.wire=ERROR

https://plantastic-test-keyperformanceideas.pbcs.us2.oraclecloud.com/HyperionPlanning/rest

authentiation: identitydomain dot username

TODO:

Separate URL structure for LCM stuff

Trim trailing newlines off of descriptive statuses

Curl Calls:
	$1 might be "POST" $url $param
	 
	statusCode=`curl -X $1 -s -w "%{http_code}" -u "$USERNAME:$PASSWORD" -o "response.txt" -D "respHeader.txt" -H "Content-Type: $4" -d $3 $2`

	-X specifies a custom request (following)
	-s silent mode
	-w write output -- goes with %{http_code} and writes out code?
	-u user and password
	-o write output to file given
	-D dump headers to given file
	-H extra header to include -- Content-Type: 
	-d sends specified data in POST request 
	
Curl downloading file (Interop)

statusCode=`curl -X GET -s -w "%{http_code}" -u "$USERNAME:$PASSWORD" -o $filepath -H "Content-Type:
application/x-www-form-urlencoded" -D respHeader.txt $url`
funcExecuteRequest "GET" $url

List files:
	LCM Version: "11.1.2.3.600"
	url=$SERVER_URL/interop/rest/$API_VERSION/applicationsnapshots
	funcExecuteRequest "GET" $url
	
	"application/x-www-form-urlencoded"
	
	URL: https://plantastic-test-keyperformanceideas.pbcs.us2.oraclecloud.com/interop/rest/11.1.2.3.600/applicationsnapshots
	Note: Curl calls where the PW has a $ in it need to escape it!
	curl -X GET -s -w "%{http_code}" -u "keyperformanceideas.jjones@keyperformanceideas.com:removed" -o test.txt -H "Content-Type: application/x-www-form-urlencoded" -D respHeader.txt https://plantastic-test-keyperformanceideas.pbcs.us2.oraclecloud.com/interop/rest/11.1.2.3.600/applicationsnapshots

Issues:
	GET on a non-existent job status ID throws 500 (would expect 404)
	Details on EXPORT_DATA job is "Outline load finished successfully"
	
	
	
{

"exportPlanningData" : false,

"gridDefinition" : {

"suppressMissingBlocks" : true,

"pov" : {

"dimensions" : [ "HSP_View", "Year", "Scenario", "Version", "Entity", "Product" ],

"members" : [ [ "BaseData" ], [ "FY15" ], [ "Plan" ], [ "Working" ], [ "410" ], [ "P_160" ] ]

},

"columns" : [ {

"dimensions" : [ "Period" ],

"members" : [ [ "IDescendants(Q1)" ] ]

}, {

"dimensions" : [ "Period" ],

"members" : [ [ "IDescendants(Q2)" ] ]

} ],

"rows" : [ {

"dimensions" : [ "Account" ],

"members" : [ [ "Project Number", "Request Date", "Project Type", "Project Investment" ] ]

} ]

}

}

	