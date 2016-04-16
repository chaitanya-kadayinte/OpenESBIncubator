
package com.fiorano.openesb.application.configuration.data;

import com.fiorano.openesb.application.constants.ConfigurationRepoConstants;

import java.io.Serializable;

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
