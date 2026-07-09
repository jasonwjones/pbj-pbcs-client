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

public class PbcsClientUtils {

    public static final String PROPS = System.getProperty("user.home") + "/pbcs-client.properties";

    private PbcsClientUtils() {}

    public static PbcsPlanningClient client() {
        return new PbcsClientFactory().createClient(connection());
    }

    public static Properties connectionProperties() {
        try {
            Properties properties = new Properties();
            properties.load(new FileReader(PROPS));
            return properties;
        } catch (Exception e) {
            throw new PbcsClientException("Couldn't load properties containing server/domain/user/pw from " + PROPS, e);
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