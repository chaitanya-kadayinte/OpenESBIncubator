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
 * @author FSTPL
 * @since Jun 21, 2010 4:21:22 PM
 * @version 10
 *
 * Enum constant representing the type of data to be stored. See javadocs for individual constants for more details.
 */
public enum DataType implements Serializable {
    /**
     * Represents that this data should be stored as text inside the file name specified.
     */
    TEXT,

    /**
     * Represents that this data is a zip stream and it should be un-zipped and stored inside the folder name specified by the user.
     */
    ZIP_STREAM;

    /**
     * Gives appropriate Data type representation in String
     * @return String
     */
    public String toString() {
        switch(this){
            case TEXT: return ConfigurationRepoConstants.DATA_TYPE_TEXT;
            case ZIP_STREAM: return ConfigurationRepoConstants.DATA_TYPE_FOLDER;
            default: return null;
        }
    }

    /**
     * Gets DataType for the <code>value</code>
     * @param value String representation of Data Type
     * @return DataType
     */
    public static DataType getDataType(String value) {
        if(ConfigurationRepoConstants.DATA_TYPE_TEXT.equals(value))
            return TEXT;
        else if(ConfigurationRepoConstants.DATA_TYPE_FOLDER.equals(value))
            return ZIP_STREAM;
        else
            return null;
    }
}
