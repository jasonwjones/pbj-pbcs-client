package com.jasonwjones.di;

import com.jasonwjones.pbcs.utils.PbcsClientUtils;
import org.junit.Test;

import java.util.List;

public class DataManagementIT {

    @Test
    public void test() {
        DataManagementClient client = PbcsClientUtils.dataManagementClient();
        client.getVersions();
        //client.
    }

    @Test
    public void jobs() {
        DataManagementClient client = PbcsClientUtils.dataManagementClient();
        List<DataManagementJob> jobs = client.getJobs();
    }

}