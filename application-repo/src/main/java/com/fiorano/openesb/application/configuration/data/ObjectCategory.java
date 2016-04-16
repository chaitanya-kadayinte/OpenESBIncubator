/**
 * Copyright (c) 1999-2007, Fiorano Software Technologies Pvt. Ltd. and affiliates.
 * Copyright (c) 2008-2015, Fiorano Software Pte. Ltd. and affiliates.
 *
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Fiorano Software ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * enclosed with this product or entered into with Fiorano.
 */

package com.fiorano.openesb.application.configuration.data;

import com.fiorano.openesb.application.constants.ConfigurationRepoConstants;

import java.io.Serializable;

public enum ObjectCategory implements Serializable {
    SERVICE_CONFIGURATION, PORT_CONFIGURATION, RESOURCE_CONFIGURATION, ROUTE, TRANSFORMATION, SELECTOR, WORKFLOW, MESSAGEFILTERS, RUNTIME_ARG_CONFIGURATION, CONNECTION_FACTORY_CONFIGURATION, DESTINATION, MISCELLANEOUS;

    /**
     * Gets Configuration type as String
     * @return Configuration type
     */
    public String getConfigurationTypeAsString() {
        switch (this) {
            case MISCELLANEOUS: return ConfigurationRepoConstants.MISC;
            case PORT_CONFIGURATION: return ConfigurationRepoConstants.PORT;
            case RESOURCE_CONFIGURATION: return ConfigurationRepoConstants.RESOURCE;
            case ROUTE: return ConfigurationRepoConstants.ROUTE;
            case SELECTOR: return ConfigurationRepoConstants.SELECTOR;
            case SERVICE_CONFIGURATION: return ConfigurationRepoConstants.COMPONENT;
            case TRANSFORMATION: return ConfigurationRepoConstants.TRANSFORMATION;
            case WORKFLOW: return ConfigurationRepoConstants.WORKFLOW;
            case MESSAGEFILTERS: return ConfigurationRepoConstants.MESSAGEFILTERS;
            case RUNTIME_ARG_CONFIGURATION: return ConfigurationRepoConstants.RUNTIME_ARG;
            case CONNECTION_FACTORY_CONFIGURATION: return ConfigurationRepoConstants.CONNECTION_FACTORY;
            case DESTINATION:return ConfigurationRepoConstants.DESTINATION;
            default: return null;
        }
    }

    /**
     * Gets ObjectCategory object based on value parameter
     * @param value Configuration type of ObjectCategory
     * @return  ObjectCategory
     */
    public static ObjectCategory getObjectCategory(String value) {
        if(ConfigurationRepoConstants.MISC.equals(value))
            return MISCELLANEOUS;
        else if(ConfigurationRepoConstants.PORT.equals(value))
            return PORT_CONFIGURATION;
        else if(ConfigurationRepoConstants.RESOURCE.equals(value))
            return RESOURCE_CONFIGURATION;
        else if(ConfigurationRepoConstants.ROUTE.equals(value))
            return ROUTE;
        else if(ConfigurationRepoConstants.SELECTOR.equals(value))
            return SELECTOR;
        else if(ConfigurationRepoConstants.COMPONENT.equals(value))
            return SERVICE_CONFIGURATION;
        else if(ConfigurationRepoConstants.TRANSFORMATION.equals(value))
            return TRANSFORMATION;
        else if(ConfigurationRepoConstants.WORKFLOW.equals(value))
            return WORKFLOW;
        else if(ConfigurationRepoConstants.MESSAGEFILTERS.equals(value))
            return MESSAGEFILTERS;
        else if(ConfigurationRepoConstants.RUNTIME_ARG.equals(value))
            return RUNTIME_ARG_CONFIGURATION;
        else if(ConfigurationRepoConstants.CONNECTION_FACTORY.equals(value))
            return CONNECTION_FACTORY_CONFIGURATION;
        else if(ConfigurationRepoConstants.DESTINATION.equals(value))
            return DESTINATION;
        else
            throw new IllegalArgumentException("INVALID_OBJECT_CATEGORY");
    }
}
