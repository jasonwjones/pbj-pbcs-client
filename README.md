# PBJ – PBCS Java Client

Full Documentation can be found [here](https://jasonwjones.github.io/pbj-pbcs-client/).

The PBCS Java Client (PBJ) project is an open source project to implement a high-quality, easy to 
use, and robust API for working with Oracle's Planning and Budgeting Cloud Service (PBCS) via its 
REST API.

As a very quick example, consider the following scenario where the server, identity domain, username
and password have all been set already and you want to launch a business rule in an application
named "Vision". The following code would accomplish this:
 
```
PbcsClient client = new PbcsClientImpl(server, identityDomain, username, password);
PbcsApplication app = client.getApplication("Vision");
app.launchBusinessRule("AggAll"); 
```

Behind the scenes, the PBCS Java Client takes care of the details of connecting, executing the
proper REST method, and returning the result (in the above example, the result is ignored, but it
could be returned in order to determine the status of the call and its ID).

The PBCS Java Client is written by [Jason Jones](https://www.jasonwjones.com) and is licensed under the generous Apache Software License version 2.0. You are welcome to use the code for any purposes. It is recommended that if you make fixes or enhancements to contribute those back to the project.


## Why this project?

There are many reasons for this project. The introduction of a REST API to manage data and metadata
for Hyperion Planning (and more!) is exciting for a number of reasons.

The REST API will allow a multitude of programming languages to easily interact with PBCS. 
Effectively this means that clients won't be limited to whatever official API (Java libraries) are
made available from Oracle themselves. 

Building a library on top of the REST API allows us to build a library with an fluent, easily 
comprehensible, well-crafted, and expressive syntax. For example, the amount of code needed to 
simply connect to PBCS and launch a business rule is many lines because we have to connect, find the
right endpoint, open an HTTP connection, submit a payload, parse the results, disconnect, and more.
It's verbose. An expressive API, however, allows us to very easily perform this action:

```
PbcsClient client = new PbcsClientImpl(server, identityDomain, username, password);
PbcsApplication app = client.getApplication("Vision");
app.launchBusinessRule("AggAll"); 
```

That said, Java is one of the "languages of the enterprise" owing to having good tooling support,
various language features, and broad set of libraries. One such feature in the Java ecosystem is the
Maven build system, for example. In short, Maven is a project management technology that allows for
easily specifying and managing dependencies, testing, compiling, and deploying software. Among other
things, Maven helps solve "JAR hell" in the Java ecosystem by making it trivial to specify a set
of libraries that a project depends on, and in turn the libraries that those projects depend on.

Unfortunately, common Oracle Java components like their JDBC driver for Oracle databases and their
Essbase API are not available in what is called Maven Central, which is akin to a global collection
of Java libraries. In practice this means that just a few extra steps have to be taken to 
incorporate this functionality into a project. It's not bad but it's less than ideal. 

Having a REST API (or more specifically, just a web API) obviates the need for relying on a JAR file
that might have some licensing restriction that prevents it from being used easily in a project. 
This means that we can publish a library to Maven Central or any other repository, and other people
and groups can simply include it in their own Java projects and easily start using it.  

The point of the PBJ (PBCS Java API) project can be summarized with the following core principals:

 * PBJ is available in Maven Central and can be easily utilized in any enterprise Java project
 * PBJ is open source under the Apache Software License 2.0 
 * PBJ provides a fluent, expressive, and clean API for interacting with the PBCS REST API
 * PBJ serves as a common, high-quality platform for Java/Groovy/Jython programmers to easily use 
   PBCS without reinventing the wheel.   


## Technical Highlights

 * Logging using SLF4J logging API
 * Mavenized project available in Maven Central
 * Extensible
 * Fluent domain-specific language
 * Error handling 
 * Can easily build uber JAR for less hassle with dependencies
 

## Getting Started

The PBCS Java Client is packaged as a Maven project. And is best to include in your own Java projects
as a Maven dependency. If you absolutely have to include this project manually in your own project,
you will need to gather up the proper dependencies and add those to your project. 

To get started, first clone the the PBJ repository to your local machine:

```
git clone https://github.com/jasonwjones/pbcs-client
```

Then `cd` into the folder and run a Maven install:

```
mvn install
```

This will build the project and install it to your local machine. You can then include it 
as just any other dependency in your own project. 


### Notes on Samples in Test Folder

Routine tests are isolated from live EPM Cloud environments:

```
mvn test
```

Surefire runs only `*Test` classes. Tests named `*IT` are opt-in and are selected
by a JUnit category:

```
# Reads from the configured environment, but does not intentionally change it
mvn verify -Pintegration-read-only -Dgpg.skip=true

# May change data, metadata, jobs, variables, or other live state
mvn verify -Pintegration-destructive -DallowDestructiveEpmTests=true -Dgpg.skip=true
```

The destructive profile fails during Maven's validation phase unless the
acknowledgement property has the exact value `true`. New `*IT` classes do not run
in either profile until they are explicitly categorized as
`ReadOnlyIntegrationTest` or `DestructiveIntegrationTest`.
The `gpg.skip` property avoids signing local development artifacts; omit it when
you intentionally want the existing release-signing behavior.

Live tests load connection details from a local file on your computer. By
default, the location of this file is:

```
System.getProperty("user.home") +"/pbcs-client.properties";
```

Use `-Dpbcs.test.credentials=/path/to/other.properties` to select a different
credentials file for an integration-test run.

The contents of this file should look something like this:

```
server=example-pbcs.pbcs.us2.oraclecloud.com
identityDomain=examplecorp
username=jason@example.com
password=yourpass
appName=appname
```

If the file or the required `server`, `username`, and `password` properties are
absent, live tests are skipped with a clear reason. All of the example values
above are fictitious. You will need to use your own information to connect to
your PBCS instance. The `identityDomain` property is optional for Gen2
environments. Specifying `appName` is optional, but it is used in a few examples.


### Release Process

Pushing a tag that exactly matches `v` followed by the non-SNAPSHOT Maven project
version runs the release workflow. For example, version `3.0.0` must be tagged
`v3.0.0`. The workflow runs the unit test suite before publishing, deploys the
generated Javadoc, and creates the GitHub release.

The following GitHub Actions secrets are required:

- `MAVEN_GPG_PRIVATE_KEY`: ASCII-armored private key used to sign artifacts
- `MAVEN_GPG_PASSPHRASE`: passphrase for that private key
- `OSSRH_USERNAME`: Maven Central Portal user-token username
- `OSSRH_TOKEN`: Maven Central Portal user-token password

The `OSSRH_*` names are retained for compatibility, but their values must be a
current Central Portal user-token pair.


### Packaging an Uber JAR

PBJ can be packaged into a single JAR file to make integrating with projects simpler. For example,
PBJ can be integrated with FDMEE, ODI, run in Jython, or fit other integration scenarios. PBJ can 
be packaged into a runnable JAR using the following Maven goal:

```
mvn clean package
```

Note that PBJ uses the Maven shade plugin to build the JAR file. Additionally, some of the package
names are renamed on the fly in order to prevent conflicts with some other Hyperion software. For 
example, the Apache `http-client` library is renamed and the Spring Framework packages under 
`org.springframework` are renamed (mostly to avoid a class loading issue with an older version of
`spring-core.jar` that is shipped with FDMEE/ODI.


## Development Status

### Working

- Downloading exported file from LCM service
- Getting an application
- Getting the API info
- Launching business rule
- Refreshing a cube
- Getting job definitions
- Getting a job status
- Exporting a data file (but not subsequently downloading it, yet)

### Not Implemented (yet)

- Rule sets
- Plan type maps
- Other things you launch
- Managing Planning Units (PUs, history, filters, status)  


## Reference

 * [Oracle REST documentation](https://docs.oracle.com/cloud/latest/pbcs_common/CREST.pdf)
 * [John Goodwin blog article on REST API](http://john-goodwin.blogspot.com/2015/09/planning-rest-api.html)
 * [Newer? Oracle EPM REST docs](https://docs.oracle.com/cloud/latest/epm-common/PREST/PREST.pdf)


## Old To-Do list (move to Issues)

- Specify alternate REST API version if needed for Planning and LCM clients
- Publish to Maven Central
- Enhance upload behavior with file already exists
- Add default service config params to factory as public final statics
- Synchronous job execution
- Move HTTP client request factory method to service config object so interop and p client can both use

## Logging

Get more detailed/wire logs:

Run with VM args:

-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.SimpleLog
-Dorg.apache.commons.logging.simplelog.showdatetime=true
-Dorg.apache.commons.logging.simplelog.log.org.apache.http=DEBUG
-Dorg.apache.commons.logging.simplelog.log.org.apache.http.wire=ERROR


## License

Licensed under the Apache Software License version 2.0.
