package com.fiorano.openesb.microservice.ccp;

public abstract class ComponentWorkflowListener implements IEventListener {
    private String componentInstanceName;
    private String applicationName;
    private String applicationVersion;

    protected ComponentWorkflowListener(String componentInstanceName, String applicationName, String applicationVersion) {
        this.componentInstanceName = componentInstanceName;
        this.applicationName = applicationName;
        this.applicationVersion = applicationVersion;
    }

    public String getComponentInstanceName() {
        return componentInstanceName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getApplicationVersion() {
        return applicationVersion;
    }
}

