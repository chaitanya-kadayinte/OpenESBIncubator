package com.fiorano.openesb.microservice.launch.impl;

import com.fiorano.openesb.microservice.launch.LaunchConfiguration;
import com.fiorano.openesb.utils.exception.FioranoException;

public class ManualLaunchProcessRuntimeHandle extends SeparateProcessRuntimeHandle {

    protected ManualLaunchProcessRuntimeHandle(LaunchConfiguration launchConfiguration, CCPCommandHelper ccpCommandHelper) throws FioranoException {
        super(launchConfiguration, ccpCommandHelper);
    }

    public boolean isRunning() {
        return isRunning;
    }
}
