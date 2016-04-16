package com.fiorano.openesb.jmsroute.impl;

import com.fiorano.openesb.route.Route;
import com.fiorano.openesb.route.RouteService;
import com.fiorano.openesb.transport.TransportService;
import com.fiorano.openesb.transport.impl.jms.JMSMessage;
import com.fiorano.openesb.transport.impl.jms.JMSPort;
import com.fiorano.openesb.transport.impl.jms.JMSPortConfiguration;


public class JMSRouteServiceImpl implements RouteService<JMSRouteConfiguration> {
    private TransportService<JMSPort,JMSMessage> transportService;


    public JMSRouteServiceImpl(TransportService<JMSPort,JMSMessage>  transportService) {
        this.transportService = transportService;
    }

    public Route createRoute(JMSRouteConfiguration routeConfiguration) throws Exception {
        return new JMSRouteImpl(transportService,routeConfiguration);
    }


}
