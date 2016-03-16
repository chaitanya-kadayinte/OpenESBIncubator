package com.fiorano.openesb.route.impl;

import com.fiorano.openesb.route.RouteOperationConfiguration;
import com.fiorano.openesb.transport.Port;
import com.fiorano.openesb.transport.TransportService;
import com.fiorano.openesb.transport.impl.jms.JMSMessage;
import com.fiorano.openesb.transport.impl.jms.JMSPort;

/**
 * Created by root on 3/8/16.
 */
public class MessageCreationConfiguration implements RouteOperationConfiguration {
    private TransportService<JMSPort,JMSMessage> transportService;

    public TransportService<JMSPort, JMSMessage> getTransportService() {
        return transportService;
    }

    public void setTransportService(TransportService<JMSPort, JMSMessage> transportService) {
        this.transportService = transportService;
    }
}
