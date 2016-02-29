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
 * Created by chaitanya on 21-01-2016.
 */

/**
 * Created by chaitanya on 21-01-2016.
 */
package com.fiorano.openesb.microservice.launch.impl;

import com.fiorano.openesb.microservice.launch.LaunchConfiguration;
import com.fiorano.openesb.microservice.launch.MicroServiceRuntimeHandle;
import com.fiorano.openesb.utils.LoggerUtil;
import com.fiorano.openesb.utils.exception.FioranoException;
import com.fiorano.openesb.utils.logging.FioranoLogHandler;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InMemoryRuntimeHandle implements MicroServiceRuntimeHandle {

    private Object service;
    private Class serviceClass;
    private LaunchConfiguration launchConfiguration;
    private boolean isRunning;

    public InMemoryRuntimeHandle(Object service, Class serviceClass, LaunchConfiguration launchConfiguration) {
        this.service = service;
        this.serviceClass = serviceClass;
        this.launchConfiguration = launchConfiguration;
        isRunning = true;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void stop() throws Exception {
        Method shutDownMethod = serviceClass.getMethod("shutdown", Object.class);
        shutDownMethod.invoke(service, "Shutdown Microservice");
        isRunning = false;
    }

    public void kill() throws Exception {
        stop();
    }

    @Override
    public void setLogLevel(Map<String, String> modules) throws FioranoException {
        for(Map.Entry<String, String> modifiedLevel:modules.entrySet()){
            Logger logger = LoggerUtil.getServiceLogger(modifiedLevel.getKey(), launchConfiguration.getApplicationName(),
                    launchConfiguration.getApplicationVersion(), launchConfiguration.getMicroserviceId(), launchConfiguration.getServiceName());
            for(Handler handler:logger.getHandlers()){
                logger.removeHandler(handler);
                if(handler instanceof FioranoLogHandler)
                    ((FioranoLogHandler)handler).setLogLevel(Level.parse(modifiedLevel.getValue()));
                else
                    handler.setLevel(Level.parse(modifiedLevel.getValue()));
                logger.addHandler(handler);
            }
        }
    }

}
