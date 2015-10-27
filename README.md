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

## About

The PBCS Java Client is written by Jason Jones and is licensed under the generous Apache Software
License version 2.0. You are welcome to use the code for any purposes. It is recommended that if you 
make fixes or enhancements to contribute those back to the project.

## License

Licensed under the Apache Software License version 2.0.