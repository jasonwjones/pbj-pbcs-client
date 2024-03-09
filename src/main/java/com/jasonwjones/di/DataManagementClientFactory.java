package com.jasonwjones.di;

import java.util.List;

public class DataManagementClientFactory {

    public static final String DEFAULT_VERSION = "V1";

    public DataManagementClient createClient(String version) {
        throw new UnsupportedOperationException();
    }

    public List<String> getVersions() {
        throw new UnsupportedOperationException();
    }

}