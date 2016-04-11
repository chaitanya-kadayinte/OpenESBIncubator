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
 * Created by chaitanya on 13-01-2016.
 */

/**
 * Created by chaitanya on 13-01-2016.
 */
package com.fiorano.openesb.microservice.launch;


import com.fiorano.openesb.application.application.LogManager;
import com.fiorano.openesb.application.service.RuntimeArgument;
import com.fiorano.openesb.application.service.ServiceRef;

import java.util.Enumeration;
import java.util.List;

public interface LaunchConfiguration<A extends AdditionalConfiguration> {
    String getUserName();
    String getPassword();

    List<RuntimeArgument> getRuntimeArgs();

    List getLogModules();

    Enumeration<ServiceRef> getRuntimeDependencies();

    enum LaunchMode {SEPARATE_PROCESS, IN_MEMORY, NONE, MANUAL}
    LaunchMode getLaunchMode();
    long getStopRetryInterval();
    int getNumberOfStopAttempts();
    String getMicroserviceId();
    String getMicroserviceVersion();
    String getServiceName();
    String getApplicationName();
    String getApplicationVersion();
    A getAdditionalConfiguration();
    LogManager getLogManager();
}
