package com.jasonwjones.pbcs.client.sso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * A {@link RefreshTokenStorage} implementation that persists each refresh token to its own properties file
 * in a base directory, named by a hash of the tenant/client ID/scope.
 */
public class SimpleRefreshTokenStorage implements RefreshTokenStorage {

    private static final Logger logger = LoggerFactory.getLogger(SimpleRefreshTokenStorage.class);

    /**
     * The filename prefix used for cache files, followed by a hash of the tenant/client ID/scope.
     */
    public static final String CACHE_FILE_PREFIX = ".pbj-refresh-token-";

    /**
     * The properties key under which the refresh token is stored.
     */
    public static final String KEY_REFRESH_TOKEN = "refresh-token";

    private final File baseDirectory;

    /**
     * Constructs an instance storing cache files in the user's home directory.
     */
    public SimpleRefreshTokenStorage() {
        this(new File(System.getProperty("user.home")));
    }

    /**
     * Constructs an instance storing cache files in the given directory.
     *
     * @param baseDirectory the directory to store cache files in
     */
    public SimpleRefreshTokenStorage(File baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    private File storageFile(String tenant, String clientId, String scope) {
        DeviceCodeCacheKey key = new DeviceCodeCacheKey(tenant, clientId, scope);
        return new File(baseDirectory, CACHE_FILE_PREFIX + key.hashCode());
    }

    @Override
    public void put(String tenant, String clientId, String scope, String deviceCode) {
        File deviceCodeFile = storageFile(tenant, clientId, scope);
        try (FileOutputStream fos = new FileOutputStream(deviceCodeFile)) {
            Properties properties = new Properties();
            properties.setProperty(KEY_REFRESH_TOKEN, deviceCode);
            properties.store(fos, "Created by PBJ library");
            logger.debug("Wrote device cache entry to {}", deviceCodeFile);
        } catch (IOException e) {
            throw new RuntimeException("Unable to write device cache properties", e);
        }
    }

    @Override
    public String getRefreshToken(String tenant, String clientId, String scope) {
        File deviceCodeFile = storageFile(tenant, clientId, scope);
        if (!deviceCodeFile.exists()) {
            return null;
        } else {
            try (FileInputStream fis = new FileInputStream(deviceCodeFile)) {
                Properties properties = new Properties();
                properties.load(fis);
                return properties.getProperty(KEY_REFRESH_TOKEN);
            } catch (IOException e) {
                throw new RuntimeException("Unable to read device cache properties", e);
            }
        }
    }

    @Override
    public boolean clear(String tenant, String clientId, String scope) {
        File deviceCodeFile = storageFile(tenant, clientId, scope);
        if (deviceCodeFile.exists()) {
            return deviceCodeFile.delete();
        }
        return false;
    }

    @Override
    public void clear() {
        File[] files = baseDirectory.listFiles((dir, name) -> name.startsWith(CACHE_FILE_PREFIX));
        if (files == null) return;
        for (File file : files) {
            if (!file.delete()) {
                logger.warn("Unable to clear refresh token file {}", file);
            }
        }
    }

}