package com.fiorano.openesb.transport.impl.jms;

import com.fiorano.openesb.transport.PortConfiguration;

public class JMSPortConfiguration implements PortConfiguration {

    public enum PortType {QUEUE, TOPIC}

    private PortType portType = PortType.QUEUE;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PortType getPortType() {
        return portType;
    }

    public void setPortType(PortType portType) {
        this.portType = portType;
    }
}
