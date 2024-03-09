package com.jasonwjones.pbcs.utils;

import com.jasonwjones.di.DataManagementClient;
import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsConnection;
import com.jasonwjones.pbcs.client.impl.PbcsConnectionImpl;

import java.io.FileReader;
import java.util.Properties;

public class PbcsClientUtils {

    public static final String PROPS = System.getProperty("user.home") + "/pbcs-client.properties";

    public static PbcsClient client() {
        return new PbcsClientFactory().createClient(connection());
    }

    public static PbcsConnection connection() {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(PROPS));
        } catch (Exception e) {
            System.out.println("Couldn't load properties...");
            System.out.println("Looking for a file at " + PROPS + " containing server/domain/user/pw");
        }
        return PbcsConnectionImpl.fromProperties(properties);
    }

    public static DataManagementClient dataManagementClient() {
        return new PbcsClientFactory().createDataManagementClient(connection());
    }

    public static PbcsApplication vision() {
        return client().getApplication("Vision");
    }

}