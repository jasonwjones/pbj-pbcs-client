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

## Getting Started

The PBCS Java Client is packaged as a Maven project. And is best to include in your own Java projects
as a Maven dependency. If you absolutely have to include this project manually in your own project,
you will need to gather up the proper dependencies and add those to your project. 

To get started, first clone the the PBJ repository to your local machine:

```
git clone https://github.com/jasonwjones/pbj-pbcs-client
```

Then `cd` into the folder and run a Maven install using the included Maven
Wrapper:

```
./mvnw install -Dgpg.skip=true
```

The wrapper downloads the project's pinned Maven version on first use, then
builds the project and installs it to your local repository. On Windows, use
`mvnw.cmd` instead of `./mvnw`.


### Notes on Samples in Test Folder

Routine tests are isolated from live EPM Cloud environments:

```
./mvnw test
```

Surefire runs only `*Test` classes. Tests named `*IT` are opt-in and are selected
by a JUnit category:

```
# Reads from the configured environment, but does not intentionally change it
./mvnw verify -Pintegration-read-only -Dgpg.skip=true

# May change data, metadata, jobs, variables, or other live state
./mvnw verify -Pintegration-destructive -DallowDestructiveEpmTests=true -Dgpg.skip=true
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

Releases are prepared with the Maven Release Plugin and published by GitHub
Actions. Before starting, check out the branch to release, pull the latest
changes, and ensure the Git working tree is clean.

For an interactive release:

```
./mvnw release:clean release:prepare
```

The plugin verifies the project, changes the POM from the development version
to the release version, commits and tags that release, changes the branch to the
next `-SNAPSHOT` version, commits it, and pushes the commits and tag. Tags use
the format `v<project.version>`.

For example, the following command releases `3.0.0` and starts development on
`3.0.1-SNAPSHOT` without prompting:

```
./mvnw -B release:clean release:prepare \
  -DreleaseVersion=3.0.0 \
  -DdevelopmentVersion=3.0.1-SNAPSHOT \
  -Dtag=v3.0.0
```

The pushed release tag starts `.github/workflows/publish.yml`. That workflow
checks that the tag matches the non-SNAPSHOT POM version, runs the unit tests,
publishes to Maven Central, deploys the generated Javadoc, and creates the
GitHub release.

Do not run `./mvnw release:perform`: deployment is owned by the tag-triggered
GitHub Actions workflow, and running `release:perform` would attempt a second
deployment.

The following GitHub Actions secrets are required:

- `MAVEN_GPG_PRIVATE_KEY`: ASCII-armored private key used to sign artifacts
- `MAVEN_GPG_PASSPHRASE`: passphrase for that private key
- `OSSRH_USERNAME`: Maven Central Portal user-token username
- `OSSRH_TOKEN`: Maven Central Portal user-token password

The `OSSRH_*` names are retained for compatibility, but their values must be a
current Central Portal user-token pair. `MAVEN_GPG_PASSPHRASE` must match the
exported private key.

The signing key currently available in the local GPG keyring has fingerprint
`BDBDB8A29B1564A1C40F7C5E5DB9AED4EF3C5FAA`. Create or replace the repository
secret without writing the private key into this checkout:

```
gpg --armor --export-secret-keys BDBDB8A29B1564A1C40F7C5E5DB9AED4EF3C5FAA \
  | gh secret set MAVEN_GPG_PRIVATE_KEY
```

GitHub does not allow existing secret values to be retrieved. To rotate the
signing key, generate and publish a new GPG key first, then replace both
`MAVEN_GPG_PRIVATE_KEY` and `MAVEN_GPG_PASSPHRASE`.

## License

Licensed under the Apache Software License version 2.0.