package com.jasonwjones.di;

import com.jasonwjones.pbcs.aif.AifDimension;
import com.jasonwjones.pbcs.client.PbcsApi;

import java.util.List;

public interface DataManagementClient {

    PbcsApi getVersion();

    List<DataManagementJob> getJobs();

    List<AifDimension> getDimensions(String applicationName);

}