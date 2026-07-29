package com.jasonwjones.di;

import com.jasonwjones.pbcs.util.PbcsClientUtils;
import com.jasonwjones.pbcs.testing.LiveEpmTestSupport;
import com.jasonwjones.pbcs.testing.ReadOnlyIntegrationTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@Category(ReadOnlyIntegrationTest.class)
public class DataManagementIT {

    @BeforeClass
    public static void requireLiveEpmCredentials() {
        LiveEpmTestSupport.assumeDefaultConnectionAvailable();
    }

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
