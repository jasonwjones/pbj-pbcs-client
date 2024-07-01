package com.jasonwjones.pbcs.client.memberdimensioncache;

import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;
import com.jasonwjones.pbcs.client.impl.AbstractDelegatingMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Stores dimension/member lookup information in an XML-based Java properties file. This implementation is mostly geared
 * towards speeding up unit and other tests.
 */
public class PropertiesMemberDimensionCache implements PbcsPlanType.MemberResolver {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesMemberDimensionCache.class);

    private static final String PROP_SEPARATOR = ",";

    private static final String KEY_VALUE_SEPARATOR = "=";

    private static final String PROP_NAME = "name";

    private static final String PROP_DIMENSION = "dimension";

    private static final String PROP_ALIAS = "alias";

    private static final String PROP_PARENT_NAME = "parent";

    private static final String PROP_LEVEL = "level";

    private static final String PROP_GENERATION = "gen";

    private final File file;

    public PropertiesMemberDimensionCache(File file) {
        logger.info("Creating properties member dimension cache at {}", file.getAbsolutePath());
        this.file = file;
    }

    public void clear() throws IOException {
        logger.info("Clearing property file member cache");
        Files.delete(file.toPath());
    }

    @Override
    public PbcsMember getMember(PbcsPlanType planType, String memberOrAliasName) {
        String encodedMemberInfo = read().getProperty(memberOrAliasName);
        if (encodedMemberInfo != null) {
            Map<String, String> properties = stringToMap(encodedMemberInfo);
            return new DelegatingMember(properties, planType);
        } else {
            return null;
        }
    }

    @Override
    public void setMember(PbcsPlanType planType, String resolvedName, PbcsMember member) {
        Map<String, String> memberProperties = new TreeMap<>();
        memberProperties.put(PROP_NAME, member.getName());
        memberProperties.put(PROP_DIMENSION, member.getDimensionName());
        if (member.getAlias() != null) memberProperties.put(PROP_ALIAS, member.getAlias());
        if (member.getParentName() != null) memberProperties.put(PROP_PARENT_NAME, member.getParentName());
        memberProperties.put(PROP_LEVEL, String.valueOf(member.getLevel()));
        memberProperties.put(PROP_GENERATION, String.valueOf(member.getGeneration()));
        String encodedProperties = mapToString(memberProperties);

        Properties properties = read();
        properties.setProperty(resolvedName, encodedProperties);
        updateProperties(properties);
    }

    @Override
    public String getDimensionName(PbcsPlanType planType, String memberName) {
        return read().getProperty(memberName);
    }

    @Override
    public void setDimension(PbcsPlanType planType, String memberName, String dimensionName) {
        Properties properties = read();
        properties.setProperty(memberName, dimensionName);
        updateProperties(properties);
    }

    private void updateProperties(Properties properties) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            properties.storeToXML(fos, "Member to dimension mapping");
            logger.info("Wrote {} entries to property file member dimension cache", properties.size());
        } catch (IOException e) {
            throw new PbcsClientException("Unable to set dimension for member", e);
        }
    }

    private Properties read() {
        if (!file.exists()) return new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            Properties properties = new Properties();
            properties.loadFromXML(fis);
            return properties;
        } catch (IOException e) {
            logger.warn("Returning new member dimension cache because unable to read member dimension cache from {}", file.getAbsolutePath());
            return new Properties();
        }
    }

    private static String mapToString(Map<String, String> map) {
        return map.entrySet()
                .stream()
                .map(e -> e.getKey() + KEY_VALUE_SEPARATOR + e.getValue())
                .collect(Collectors.joining(PROP_SEPARATOR));
    }

    private static Map<String, String> stringToMap(String text) {
        String[] keyPairs = text.split(Pattern.quote(PROP_SEPARATOR));
        Map<String, String> map = new HashMap<>();
        for (String keyPair : keyPairs) {
            String[] keyAndValue = keyPair.split(Pattern.quote(KEY_VALUE_SEPARATOR));
            map.put(keyAndValue[0], keyAndValue[1]);
        }
        return map;
    }

    public static class DelegatingMember extends AbstractDelegatingMember implements PbcsMember {

        private final Map<String, String> properties;

        public DelegatingMember(Map<String, String> properties, PbcsPlanType planType) {
            super(planType, properties.get(PROP_NAME), properties.get(PROP_DIMENSION));
            this.properties = properties;
        }

        private String getProperty(String propertyName, Supplier<String> defaultValue) {
            String value = properties.get(propertyName);
            return value != null ? value : defaultValue.get();
        }

        private Integer getPropertyAsInteger(String propertyName, IntSupplier defaultValue) {
            String value = properties.get(propertyName);
            return value != null ? Integer.parseInt(value) : defaultValue.getAsInt();
        }

        @Override
        public String getAlias() {
            return getProperty(PROP_ALIAS, member()::getAlias);
        }

        @Override
        public String getParentName() {
            return getProperty(PROP_PARENT_NAME, member()::getParentName);
        }

        @Override
        public int getLevel() {
            return getPropertyAsInteger(PROP_LEVEL, member()::getLevel);
        }

        @Override
        public int getGeneration() {
            return getPropertyAsInteger(PROP_GENERATION, member()::getGeneration);
        }

    }

}