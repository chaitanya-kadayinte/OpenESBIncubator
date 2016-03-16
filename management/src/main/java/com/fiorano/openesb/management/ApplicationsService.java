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
 * Created by chaitanya on 05-03-2016.
 * <p>
 * Created by chaitanya on 05-03-2016.
 */

/**
 * Created by chaitanya on 05-03-2016.
 */
package com.fiorano.openesb.management;


public interface ApplicationsService {


    Response getApplications();

    Response launchApplication(String applicationName , String applicationVersion);

    Response stopApplication(String applicationName , String applicationVersion);

  /*  boolean startAllMicroServices(String appGuid, String version, String handleID);

    boolean stopAllMicroServices(String appGuid, String version, String handleID);*/

    Response startMicroService(String appGuid, String version, String microServiceName);

    Response stopMicroService(String appGuid, String version, String microServiceName);

    Response synchronizeApplication(String appGuid, String version);
}
