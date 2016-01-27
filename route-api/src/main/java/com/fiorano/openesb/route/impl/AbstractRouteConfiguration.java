package com.fiorano.openesb.route.impl;

import com.fiorano.openesb.route.RouteConfiguration;
import com.fiorano.openesb.route.RouteOperationConfiguration;
import com.fiorano.openesb.transport.PortConfiguration;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRouteConfiguration<E> implements RouteConfiguration {
    private List<RouteOperationConfiguration> routeOperationConfigurations = new ArrayList<RouteOperationConfiguration>();

    public List<RouteOperationConfiguration> getRouteOperationConfigurations() {
        return routeOperationConfigurations;
    }
}
