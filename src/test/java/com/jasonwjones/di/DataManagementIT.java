package com.jasonwjones.di;

import com.jasonwjones.pbcs.util.PbcsClientUtils;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class DataManagementIT {

    @Test
    public void whenGetVersion() {
        DataManagementClient client = PbcsClientUtils.dataManagementClient();
        assertThat(client.getVersion().getVersion(), is("V1"));
    }

    @Test
    public void jobs() {
        DataManagementClient client = PbcsClientUtils.dataManagementClient();
        List<DataManagementJob> jobs = client.getJobs();
        assertThat(jobs, notNullValue());
    }

}