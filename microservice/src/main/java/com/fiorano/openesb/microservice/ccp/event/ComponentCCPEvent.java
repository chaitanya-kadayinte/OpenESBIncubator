package com.fiorano.openesb.microservice.ccp.event;


public class ComponentCCPEvent {
    public ComponentCCPEvent(String componentInstanceName, ControlEvent controlEvent) {
        this.componentInstanceName = componentInstanceName;
        this.controlEvent = controlEvent;
    }

    private String componentInstanceName;
    private ControlEvent controlEvent;

    public String getComponentInstanceName() {
        return componentInstanceName;
    }

    public ControlEvent getControlEvent() {
        return controlEvent;
    }
}
