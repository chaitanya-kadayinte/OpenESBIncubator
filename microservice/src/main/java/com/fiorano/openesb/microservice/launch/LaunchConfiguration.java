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

    enum LaunchMode {SEPARATE_PROCESS, IN_MEMORY,DOCKER, NONE, MANUAL}
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
