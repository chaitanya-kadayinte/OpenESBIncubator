package com.fiorano.openesb.microservice.ccp.event;


public class ComponentCCPEvent {
    public ComponentCCPEvent(String componentId, ControlEvent controlEvent) {
        this.componentId = componentId;
        this.controlEvent = controlEvent;
    }

    private String componentId;
    private ControlEvent controlEvent;

    public String getComponentId() {
        return componentId;
    }

    public ControlEvent getControlEvent() {
        return controlEvent;
    }
}
