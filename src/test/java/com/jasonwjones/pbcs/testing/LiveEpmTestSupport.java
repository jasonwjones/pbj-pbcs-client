package com.jasonwjones.pbcs.testing;

import org.junit.Assume;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

/**
 * Preconditions shared by tests that connect to a live EPM Cloud environment.
 */
public final class LiveEpmTestSupport {

    public static final String CREDENTIALS_PATH_PROPERTY = "pbcs.test.credentials";

    private static final List<String> REQUIRED_PROPERTIES =
            List.of("server", "username", "password");

    private LiveEpmTestSupport() {
    }

    public static Properties loadConnectionProperties(Path path) {
        Assume.assumeTrue(
                "Skipping live EPM integration tests: credentials file does not exist: " + path,
                Files.isRegularFile(path));

        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(path)) {
            properties.load(reader);
        } catch (IOException e) {
            Assume.assumeNoException(
                    "Skipping live EPM integration tests: cannot read credentials file: " + path,
                    e);
        }

        List<String> missing = REQUIRED_PROPERTIES.stream()
                .filter(name -> properties.getProperty(name) == null
                        || properties.getProperty(name).isBlank())
                .toList();
        Assume.assumeTrue(
                "Skipping live EPM integration tests: missing required properties "
                        + missing + " in " + path,
                missing.isEmpty());
        return properties;
    }

    public static void assumeDefaultConnectionAvailable() {
        loadConnectionProperties(defaultConnectionPath());
    }

    public static Path defaultConnectionPath() {
        String configuredPath = System.getProperty(CREDENTIALS_PATH_PROPERTY);
        return configuredPath == null || configuredPath.isBlank()
                ? Path.of(System.getProperty("user.home"), "pbcs-client.properties")
                : Path.of(configuredPath);
    }
}
