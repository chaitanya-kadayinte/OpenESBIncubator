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
 * This class defines allowed values for port type
 * @author FSTPL
 * @version: 10
 */
public enum PortType implements Serializable {
    INPUT, OUTPUT;

    /**
     * Returns String converted port type
     * @return String - Port type
     */
    public String toString() {
        switch(this){
            case INPUT: return ConfigurationRepoConstants.INPUT_PORT;
            case OUTPUT: return ConfigurationRepoConstants.OUTPUT_PORT;
            default:return null;
        }
    }

    /**
     * Returns port type
     * @param value Value
     * @return PortType - Port type as object of this class
     */
    public static PortType getPortType(String value) {
        if(ConfigurationRepoConstants.INPUT_PORT.equals(value))
            return INPUT;
        else if(ConfigurationRepoConstants.OUTPUT_PORT.equals(value))
            return OUTPUT;
        else
            return null;
    }
}
