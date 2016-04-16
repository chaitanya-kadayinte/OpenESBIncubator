package com.fiorano.openesb.route;

import com.fiorano.openesb.transport.ConsumerConfiguration;
import com.fiorano.openesb.transport.PortConfiguration;

import java.util.List;

public interface RouteConfiguration {

    PortConfiguration getSourceConfiguration();

    PortConfiguration getDestinationConfiguration();

    ConsumerConfiguration getConsumerConfiguration();

    List<RouteOperationConfiguration> getRouteOperationConfigurations();

}
