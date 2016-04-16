package com.fiorano.openesb.microservice.launch;

public abstract class JavaLaunchConfiguration implements AdditionalConfiguration {
    public abstract boolean isDebugMode();
    public abstract int getDebugPort();
}
