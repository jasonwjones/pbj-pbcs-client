package com.jasonwjones.pbcs.util;

import com.jasonwjones.di.DataManagementClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsConnection;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.PbcsPlanningClient;
import com.jasonwjones.pbcs.client.impl.PbcsConnectionImpl;
import com.jasonwjones.pbcs.client.impl.PlanTypeConfigurationImpl;
import com.jasonwjones.pbcs.client.memberdimensioncache.PropertiesMemberDimensionCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class PbcsClientUtils {

    private static final Logger logger = LoggerFactory.getLogger(PbcsClientUtils.class);

    public static final String PROPS = System.getProperty("user.home") + "/pbcs-client.properties";

    public static PbcsPlanningClient client() {
        return new PbcsClientFactory().createClient(connection());
    }

    public static Properties connectionProperties() {
        try {
            Properties properties = new Properties();
            properties.load(new FileReader(PROPS));
            return properties;
        } catch (Exception e) {
            System.out.println("Couldn't load properties...");
            System.out.println("Looking for a file at " + PROPS + " containing server/domain/user/pw");
            throw new RuntimeException(e);
        }
    }

    public static PbcsConnection connection() {
        Properties properties = connectionProperties();
        return PbcsConnectionImpl.fromProperties(properties);
    }

    public static DataManagementClient dataManagementClient() {
        return new PbcsClientFactory().createDataManagementClient(connection());
    }

    public static PbcsApplication vision() {
        return client().getApplication("Vision");
    }

    public static PbcsPlanType planType() {
        PbcsPlanningClient client = client();
        Properties properties = connectionProperties();
        PbcsApplication application = client.getApplication(properties.getProperty("appName"));

        PlanTypeConfigurationImpl planTypeConfiguration = new PlanTypeConfigurationImpl();
        planTypeConfiguration.setName(properties.getProperty("plan"));

        List<String> dimensions = Arrays.asList(properties.getProperty("dimensions").split(";"));
        planTypeConfiguration.setExplicitDimensions(dimensions);

        String attributeDimensionDefinition = properties.getProperty("attributeDimensions");
        if (attributeDimensionDefinition != null) {
            List<String> attributeDimensions = Arrays.asList(attributeDimensionDefinition.split(";"));
            planTypeConfiguration.setExplicitAttributeDimensions(attributeDimensions);
        }

        String memberResolverType = properties.getProperty("memberResolverType");
        if (memberResolverType != null) {
            if ("properties".equalsIgnoreCase(memberResolverType)) {
                File file = new File(properties.getProperty("memberResolverFile", "default-member-resolver.xml"));
                PropertiesMemberDimensionCache propertiesMemberDimensionCache = new PropertiesMemberDimensionCache(file);
                planTypeConfiguration.setMemberResolver(propertiesMemberDimensionCache);
            } else {
                throw new IllegalArgumentException("Unknown member resolver type: " + memberResolverType);
            }
        }
        return application.getPlanType(planTypeConfiguration);
    }


}