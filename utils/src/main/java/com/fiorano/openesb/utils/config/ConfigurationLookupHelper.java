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

import java.io.*;
import java.util.Properties;

public class ConfigurationLookupHelper {
    private static ConfigurationLookupHelper CONFIGURATION_LOOKUP_HELPER = new ConfigurationLookupHelper();
    private Properties properties;

    private ConfigurationLookupHelper() {
        InputStream inputStream = null;
        try {
            properties = new Properties();
            String propFileName = System.getProperty("karaf.base") + File.separator + "etc" + File.separator + "com.fiorano.openesb.transport.provider.cfg";
            inputStream = new FileInputStream(propFileName);
            properties.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            //todo log e
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                //
            }
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
