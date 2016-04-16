package com.fiorano.openesb.microservice.launch;


public interface Launcher<H extends MicroServiceRuntimeHandle> {
    MicroServiceRuntimeHandle launch(LaunchConfiguration launchConfiguration, String configuration) throws Exception;
}
