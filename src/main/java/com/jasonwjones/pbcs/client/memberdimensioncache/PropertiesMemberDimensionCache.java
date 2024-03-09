package com.jasonwjones.pbcs.client.memberdimensioncache;

import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Stores dimension/member lookup information in an XML-based Java properties file. This implementation is mostly geared
 * towards speeding up unit and other tests.
 */
public class PropertiesMemberDimensionCache implements PbcsPlanType.MemberDimensionCache {

    private final File file;

    public PropertiesMemberDimensionCache(File file) {
        this.file = file;
    }

    @Override
    public String getDimensionName(PbcsPlanType planType, String memberName) {
        return read().getProperty(memberName);
    }

    @Override
    public void setDimension(PbcsPlanType planType, String memberName, String dimensionName) {
        Properties properties = read();
        properties.setProperty(memberName, dimensionName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            properties.storeToXML(fos, "Member to dimension mapping");
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
            throw new PbcsClientException("Unable read member dimension cache", e);
        }
    }

}