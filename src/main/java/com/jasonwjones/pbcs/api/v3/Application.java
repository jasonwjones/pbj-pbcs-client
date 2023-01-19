package com.jasonwjones.pbcs.api.v3;

public class Application {

	// other properties come back on this request that could be incorporated:
	// {
	//  "type": "HP",
	//  "items": [
	//    {
	//      "dpEnabled": false,
	//      "appType": "EPBCS",
	//      "adminMode": false,
	//      "appStorage": "Multidim",
	//      "helpServerUrl": "https://www.oracle.com",
	//      "workpaceServerUrl": "https://server.epm.us-phoenix-1.ocs.oraclecloud.com:443",
	//      "unicode": true,
	//      "name": "Vision",
	//      "type": "HP"
	//    }

	private boolean dpEnabled;

	private String name;

	private String type;

	public boolean isDpEnabled() {
		return dpEnabled;
	}

	public void setDpEnabled(boolean dpEnabled) {
		this.dpEnabled = dpEnabled;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Application [name=" + name + ", type=" + type + ", dpEnabled=" + dpEnabled + "]";
	}

}