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

/**
 * This class defines the valid values for label that can be used
 * @author FSTPL
 * @version 10
 */
public enum Label implements Serializable {
    DEVELOPMENT, TESTING, STAGING, PRODUCTION;


    /**
     * This method returns the label in String format
     * @return String - Label
     */
    @Override
    public String toString() {
        switch (this){
            case DEVELOPMENT: return ConfigurationRepoConstants.DEVELOPMENT;
            case TESTING: return ConfigurationRepoConstants.TESTING;
            case STAGING: return ConfigurationRepoConstants.STAGING;
            case PRODUCTION: return ConfigurationRepoConstants.PRODUCTION;
            default: return null;
        }
    }

}
