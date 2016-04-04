package com.fiorano.openesb.route.impl;

import com.fiorano.openesb.application.application.Application;
import com.fiorano.openesb.application.application.InputPortInstance;
import com.fiorano.openesb.application.application.PortInstance;
import com.fiorano.openesb.route.RouteOperationConfiguration;

/**
 * Created by root on 3/4/16.
 */
public class CarryForwardContextConfiguration extends RouteOperationConfiguration {
    private Application application;
    private String serviceInstanceName;
    private PortInstance portInstance;

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public String getServiceInstanceName() {
        return serviceInstanceName;
    }

    public void setServiceInstanceName(String serviceInstanceName) {
        this.serviceInstanceName = serviceInstanceName;
    }

    public PortInstance getPortInstance() {
        return portInstance;
    }

    public void setPortInstance(PortInstance portInstance) {
        this.portInstance = portInstance;
    }
}
