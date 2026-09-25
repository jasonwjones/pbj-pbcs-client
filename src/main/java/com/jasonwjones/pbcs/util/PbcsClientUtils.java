package com.jasonwjones.pbcs.util;

import com.jasonwjones.di.DataManagementClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsConnection;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.PbcsPlanningClient;
import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;
import com.jasonwjones.pbcs.client.impl.PbcsConnectionImpl;
import com.jasonwjones.pbcs.client.impl.PlanTypeConfigurationImpl;
import com.jasonwjones.pbcs.client.memberdimensioncache.AggregateMemberResolver;
import com.jasonwjones.pbcs.client.memberdimensioncache.PropertiesKnownInvalidMemberResolver;
import com.jasonwjones.pbcs.client.memberdimensioncache.PropertiesMemberDimensionCache;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Convenience helpers for building a client, connection, or plan type from a local
 * {@code pbcs-client.properties} file, primarily intended for tests and ad hoc scripts.
 */
public class PbcsClientUtils {

    /**
     * The default location of the local connection properties file.
     */
    /**
     * The system property that names a different connection file, for running against a second pod or
     * a cut-down configuration without editing the one in the home directory.
     */
    public static final String CREDENTIALS_PATH_PROPERTY = "pbcs.test.credentials";

    public static final String PROPS = resolveProps();

    private static String resolveProps() {
        String configured = System.getProperty(CREDENTIALS_PATH_PROPERTY);
        return configured == null || configured.isBlank()
                ? System.getProperty("user.home") + "/pbcs-client.properties"
                : configured;
    }

    private PbcsClientUtils() {}

    /**
     * Creates a client using the connection details from the default properties file.
     *
     * @return a new client
     */
    public static PbcsPlanningClient client() {
        return new PbcsClientFactory().createClient(connection());
    }

    /**
     * Loads the connection properties from the default properties file.
     *
     * @return the loaded properties
     * @throws PbcsClientException if the properties file cannot be loaded
     */
    public static Properties connectionProperties() {
        try {
            Properties properties = new Properties();
            properties.load(new FileReader(PROPS));
            return properties;
        } catch (Exception e) {
            throw new PbcsClientException("Couldn't load properties containing server/domain/user/pw from " + PROPS, e);
        }
    }

    /**
     * Creates a connection using the connection details from the default properties file.
     *
     * @return a new connection
     */
    public static PbcsConnection connection() {
        Properties properties = connectionProperties();
        return PbcsConnectionImpl.fromProperties(properties);
    }

    /**
     * Creates a data management client using the connection details from the default properties file.
     *
     * @return a new data management client
     */
    public static DataManagementClient dataManagementClient() {
        return new PbcsClientFactory().createDataManagementClient(connection());
    }

    /**
     * Convenience method to get the "Vision" sample application.
     *
     * @return the Vision application
     */
    public static PbcsApplication vision() {
        return client().getApplication("Vision");
    }

    /**
     * Builds a plan type using the application, plan, dimensions, and member resolver settings from the
     * default properties file.
     *
     * @return the configured plan type
     */
    public static PbcsPlanType planType() {
        PbcsPlanningClient client = client();
        Properties properties = connectionProperties();
        PbcsApplication application = client.getApplication(properties.getProperty("appName"));

        PlanTypeConfigurationImpl planTypeConfiguration = new PlanTypeConfigurationImpl();
        planTypeConfiguration.setName(properties.getProperty("plan"));

        // Discovery unless the properties name the dimensions, because the plan-type dimension
        // endpoint returns them all - attribute dimensions included, typed as ATTRIBUTE - so a
        // properties file that lists them is a copy of something the server will say for itself, and
        // goes stale when the outline changes. Both keys are still honoured for a pod or a test that
        // wants a particular list.
        String dimensionDefinition = properties.getProperty("dimensions");
        if (dimensionDefinition == null || dimensionDefinition.isBlank()) {
            planTypeConfiguration.setDiscoverDimensions(true);
        } else {
            planTypeConfiguration.setExplicitDimensions(Arrays.asList(dimensionDefinition.split(";")));

            String attributeDimensionDefinition = properties.getProperty("attributeDimensions");
            if (attributeDimensionDefinition != null) {
                List<String> attributeDimensions = Arrays.asList(attributeDimensionDefinition.split(";"));
                planTypeConfiguration.setExplicitAttributeDimensions(attributeDimensions);
            }
        }


        String memberResolverType = properties.getProperty("memberResolverType");
        List<PbcsPlanType.MemberResolver> memberResolvers = new ArrayList<>();

        String knownInvalidMemberResolverPath = properties.getProperty("knownInvalidMemberResolverPath");
        if (knownInvalidMemberResolverPath != null) {
            File knownInvalidMemberResolverFile = new File(knownInvalidMemberResolverPath);
            PropertiesKnownInvalidMemberResolver knownInvalidMemberResolver = new PropertiesKnownInvalidMemberResolver(knownInvalidMemberResolverFile);
            memberResolvers.add(knownInvalidMemberResolver);
        }

        if (memberResolverType != null) {
            if ("properties".equalsIgnoreCase(memberResolverType)) {
                File file = new File(properties.getProperty("memberResolverFile", "default-member-resolver.xml"));
                PropertiesMemberDimensionCache propertiesMemberDimensionCache = new PropertiesMemberDimensionCache(file);
                memberResolvers.add(propertiesMemberDimensionCache);
            } else {
                throw new IllegalArgumentException("Unknown member resolver type: " + memberResolverType);
            }
        }


        if (StringUtils.hasText(memberResolverType)) {
            PbcsPlanType.MemberResolver memberResolver = new AggregateMemberResolver(memberResolvers);
            planTypeConfiguration.setMemberResolver(memberResolver);
        }

        return application.getPlanType(planTypeConfiguration);
    }

}