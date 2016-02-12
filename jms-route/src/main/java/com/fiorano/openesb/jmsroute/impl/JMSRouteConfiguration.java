package com.fiorano.openesb.jmsroute.impl;

import com.fiorano.openesb.route.RouteConfiguration;
import com.fiorano.openesb.route.impl.AbstractRouteConfiguration;
import com.fiorano.openesb.transport.impl.jms.JMSConsumerConfiguration;
import com.fiorano.openesb.transport.impl.jms.JMSPortConfiguration;


public class JMSRouteConfiguration extends AbstractRouteConfiguration implements RouteConfiguration {

    private JMSPortConfiguration sourceConfiguration;

    private JMSPortConfiguration destinationConfiguration;

    public JMSRouteConfiguration(JMSPortConfiguration sourceConfiguration, JMSPortConfiguration destinationConfiguration) {
        this.sourceConfiguration = sourceConfiguration;
        this.destinationConfiguration = destinationConfiguration;
    }

    public JMSPortConfiguration getSourceConfiguration() {
        return sourceConfiguration;
    }

    public JMSPortConfiguration getDestinationConfiguration() {
        return destinationConfiguration;
    }

    public JMSConsumerConfiguration getConsumerConfiguration() {
        return new JMSConsumerConfiguration(null);
    }

}
