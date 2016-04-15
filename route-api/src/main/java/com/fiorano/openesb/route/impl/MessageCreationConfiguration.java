package com.fiorano.openesb.route.impl;

import com.fiorano.openesb.route.RouteOperationConfiguration;
import com.fiorano.openesb.transport.Port;
import com.fiorano.openesb.transport.TransportService;
import com.fiorano.openesb.transport.impl.jms.JMSMessage;
import com.fiorano.openesb.transport.impl.jms.JMSPort;
public class MessageCreationConfiguration extends RouteOperationConfiguration {
    private TransportService<JMSPort,JMSMessage> transportService;

    public TransportService<JMSPort, JMSMessage> getTransportService() {
        return transportService;
    }

    @SuppressWarnings("unchecked")
    public void setTransportService(TransportService transportService) {
        this.transportService = transportService;
    }
}
