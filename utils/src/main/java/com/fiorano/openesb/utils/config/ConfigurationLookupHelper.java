/**
 * Copyright (c) 1999-2007, Fiorano Software Technologies Pvt. Ltd. and affiliates.
 * Copyright (c) 2008-2014, Fiorano Software Pte. Ltd. and affiliates.
 * <p>
 * All rights reserved.
 * <p>
 * This software is the confidential and proprietary information
 * of Fiorano Software ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * enclosed with this product or entered into with Fiorano.
 * <p>
 * Created by chaitanya on 02-02-2016.
 * <p>
 * Created by chaitanya on 02-02-2016.
 */

/**
 * Created by chaitanya on 02-02-2016.
 */
package com.fiorano.openesb.utils.config;

import com.fiorano.openesb.utils.ConfigReader;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

public class ConfigurationLookupHelper {
    private static ConfigurationLookupHelper CONFIGURATION_LOOKUP_HELPER = new ConfigurationLookupHelper();
    private Properties properties;
    private String userName = "karaf";
    private String password = "karaf";
    private String brokerURL ="tcp://localhost:61616?wireFormat.maxInactivityDuration=0";
    private String jmxURL = "service:jmx:rmi:///jndi/rmi://localhost:1099/karaf-root";
    private String providerURL = "tcp://localhost:61616";

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getUserName() {
        return userName;
    }

    public void setuserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setpassword(String password) {
        this.password = password;
    }

    public String getBrokerURL() {
        return brokerURL;
    }

    public void setbrokerURL(String brokerURL) {
        this.brokerURL = brokerURL;
    }

    public String getJmxURL() {
        return jmxURL;
    }

    public void setjmxURL(String jmxURL) {
        this.jmxURL = jmxURL;
    }

    public String getProviderURL() {
        return providerURL;
    }

    public void setproviderURL(String providerURL) {
        this.providerURL = providerURL;
    }

    private ConfigurationLookupHelper() {
        File configFile = new File(System.getProperty("karaf.base") + File.separator
                + "etc" + File.separator + "com.fiorano.openesb.transport.provider.cfg");
        if (!configFile.exists()) {
            return;
        }
        try {
            ConfigReader.readConfigFromPropertiesFile(configFile, this);
            properties = new Properties();
            ConfigReader.readPropertiesFromFile(configFile, properties);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static ConfigurationLookupHelper getInstance() {
        return CONFIGURATION_LOOKUP_HELPER;
    }

    public String getValue(String key) {
        return properties.getProperty(key);
    }

    public String getValue(String key, String defaultValue) {
        String property = properties.getProperty(key);
        return property != null ? property : defaultValue;
    }

    public Properties getProperties(){
        return properties;
    }
}
