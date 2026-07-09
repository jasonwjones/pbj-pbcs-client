package com.jasonwjones.pbcs.util;

import com.jasonwjones.pbcs.client.PbcsConnection;
import com.jasonwjones.pbcs.client.impl.PbcsConnectionImpl;

import java.io.FileReader;
import java.util.Properties;

public class ConnectionUtils {

    public static final String DEFAULT_PROPS_FILE = "pbcs-client.properties";

    private ConnectionUtils() {}

    public static PbcsConnection defaultConnection() {
        return connection(System.getProperty("user.home") + "/" + DEFAULT_PROPS_FILE);
    }

    public static PbcsConnection connection(String filename) {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(filename));
        } catch (Exception e) {
            System.out.println("Couldn't load properties...");
            System.out.println("Looking for a file at " + filename + " containing server/domain/user/pw");
        }
        return PbcsConnectionImpl.fromProperties(properties);
    }

}