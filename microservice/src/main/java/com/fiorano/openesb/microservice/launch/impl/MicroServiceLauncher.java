package com.fiorano.openesb.microservice.launch.impl;

import com.fiorano.openesb.microservice.ccp.CCPEventManager;
import com.fiorano.openesb.microservice.launch.LaunchConfiguration;
import com.fiorano.openesb.microservice.launch.Launcher;
import com.fiorano.openesb.microservice.launch.MicroServiceRuntimeHandle;

import java.util.Map;

public class MicroServiceLauncher implements Launcher<MicroServiceRuntimeHandle>{
    private final CCPEventManager ccpEventManager;

    public MicroServiceLauncher(CCPEventManager ccpEventManager) throws Exception {
        this.ccpEventManager = ccpEventManager;
    }

    public MicroServiceRuntimeHandle launch(LaunchConfiguration launchConfiguration, String configuration) throws Exception {
        Launcher launcher = null;
        if(launchConfiguration.getLaunchMode() == LaunchConfiguration.LaunchMode.SEPARATE_PROCESS) {
             launcher = new SeparateProcessLauncher(ccpEventManager);
        } else if(launchConfiguration.getLaunchMode() == LaunchConfiguration.LaunchMode.IN_MEMORY) {
            launcher = new InMemoryLauncher();
        } else  if(launchConfiguration.getLaunchMode() == LaunchConfiguration.LaunchMode.MANUAL) {
            return new ManualLaunchProcessRuntimeHandle(launchConfiguration, new CCPCommandHelper(ccpEventManager,launchConfiguration));
        } else if (launchConfiguration.getLaunchMode() == LaunchConfiguration.LaunchMode.DOCKER) {
            return new ManualLaunchProcessRuntimeHandle(launchConfiguration, new CCPCommandHelper(ccpEventManager,launchConfiguration));
        }
        return launcher.launch(launchConfiguration, configuration);
    }
}
