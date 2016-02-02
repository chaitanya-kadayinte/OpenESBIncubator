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
 */

/**
 * Created by chaitanya on 02-02-2016.
 */
package com.fiorano.openesb.utils.config;

public class ConfigurationLookupHelper {
    private static ConfigurationLookupHelper CONFIGURATION_LOOKUP_HELPER = new ConfigurationLookupHelper();
    private ConfigurationLookupHelper() {
    }

    public static ConfigurationLookupHelper getInstance() {
        return CONFIGURATION_LOOKUP_HELPER;
    }

    public String getValue(String key) {
        return null;
    }
}
