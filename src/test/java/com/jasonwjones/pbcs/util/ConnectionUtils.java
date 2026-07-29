package com.jasonwjones.pbcs.util;

import com.jasonwjones.pbcs.client.PbcsConnection;
import com.jasonwjones.pbcs.client.impl.PbcsConnectionImpl;
import com.jasonwjones.pbcs.testing.LiveEpmTestSupport;

import java.nio.file.Path;
import java.util.Properties;

public class ConnectionUtils {

    public static final String DEFAULT_PROPS_FILE = "pbcs-client.properties";

    private ConnectionUtils() {}

    public static PbcsConnection defaultConnection() {
        return connection(LiveEpmTestSupport.defaultConnectionPath());
    }

    public static PbcsConnection connection(String filename) {
        return connection(Path.of(filename));
    }

    private static PbcsConnection connection(Path path) {
        Properties properties = LiveEpmTestSupport.loadConnectionProperties(path);
        return PbcsConnectionImpl.fromProperties(properties);
    }

}
