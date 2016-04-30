# PBCS Java Client (PBJ)

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

The samples all derive from a common base class `AbstractIntegrationTest` that loads in details for a PBCS server from a local file on your computer. By default, the location of this file is here:

```
System.getProperty("user.home") +"/pbcs-client.properties";
```

The contents of this file should look something like this:

```
server=example-pbcs.pbcs.us2.oraclecloud.com
identityDomain=examplecorp
username=jason@example.com
password=yourpass
appName=appname
```

Note that all of the above values are fictitious. You will need to put in your own information to connect to your PBCS isntance. Your username will typically if not always be an email address. Specifying the appName is optional but it's used in a few examples.

## Planning API vs LCM API

There are effectively two REST APIs that can be interacted with for PBCS: One is a Hyperion Planning
specific API (at version 'v3' as of this writing) and the other is an API for LCM. At the time of
this writing the LCM API version is 11.1.2.3.600. To the extent possible, the PBJ tries to hide the
underlying details of these APIs from the user. Sometimes its inevitable that these APIs must be
worked with separately. For example, a full use-case of exporting and downloading a file  

## Functionality Overview

### LCM (Interop)

* Get LCM API versions
* Upload and download files
* List files
* Delete files
* Get information about services
* Recreate a service

## Development Status

### Working

- Getting an application
- Getting the API info
- Launching business rule
- Refreshing a cube
- Getting job definitions
- Getting a job status
- Exporting a data file (but not subsequently downloading it, yet)

### Next to be developed

- Downloading exported file from LCM service
- Rule sets
- Plan type maps
- Other things you launch

### Not implemented (not currently part of plans)

- Managing Planning Units (PUs, history, filters, status)

## Helping out

REST documentation: https://docs.oracle.com/cloud/latest/pbcs_common/CREST.pdf

Other links:

http://john-goodwin.blogspot.com/2015/09/planning-rest-api.html

## To Do

- Specify alternate REST API version if needed for Planning and LCM clients
- Publish to Maven Central
- Enhance upload behavior with file already exists
- Add default service config params to factory as public final statics
- Synchronous job execution
- Move HTTP client request factory method to service config object so interop and p client can both use

## Examples



## About

The PBCS Java Client is written by Jason Jones and is licensed under the generous Apache Software
License version 2.0. You are welcome to use the code for any purposes. It is recommended that if you 
make fixes or enhancements to contribute those back to the project.

## License

Licensed under the Apache Software License version 2.0.