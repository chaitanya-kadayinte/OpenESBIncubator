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
 * Created by chaitanya on 23-02-2016.
 */

/**
 * Created by chaitanya on 23-02-2016.
 */
package com.fiorano.openesb.microservice.launch.impl.cl;

public class LogHelper {
    public static Object getOutMessage(String clm, int i, String log) {
        return log;
    }

    public static Throwable getOutMessage(String clm, int i, String uniqueComponentIdentifier, String s) {
        return new Exception(uniqueComponentIdentifier);
    }

    public static String getErrMessage(String clm, int i, String s) {
        return s;
    }
}
