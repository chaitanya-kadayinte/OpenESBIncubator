package com.fiorano.openesb.microservice.launch.impl;

import com.fiorano.openesb.microservice.ccp.event.CCPEventType;
import com.fiorano.openesb.microservice.launch.LaunchConfiguration;
import com.fiorano.openesb.microservice.launch.MicroServiceRuntimeHandle;
import com.fiorano.openesb.utils.exception.FioranoException;
import com.fiorano.openesb.utils.logging.api.FioranoClientLogger;

import java.util.Map;

/**
 * Created by Janardhan on 4/12/2016.
 */
public class ManualLaunchProcessRuntimeHandle extends SeparateProcessRuntimeHandle {

    protected ManualLaunchProcessRuntimeHandle(LaunchConfiguration launchConfiguration, CCPCommandHelper ccpCommandHelper) throws FioranoException {
        super(launchConfiguration, ccpCommandHelper);
    }

    public boolean isRunning() {
        return isRunning;
    }
}
