package com.fiorano.openesb.transport.impl.jms;

import com.fiorano.openesb.transport.Port;

import javax.jms.Destination;

public class JMSPort implements Port {

    private Destination destination;

    public JMSPort(Destination destination) {
        this.destination = destination;
    }

    public Destination getDestination() {
        return destination;
    }
}
