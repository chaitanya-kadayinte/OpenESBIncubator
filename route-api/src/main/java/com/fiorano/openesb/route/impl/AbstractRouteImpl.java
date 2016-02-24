package com.fiorano.openesb.route.impl;

import com.fiorano.openesb.route.FilterMessageException;
import com.fiorano.openesb.route.Route;
import com.fiorano.openesb.route.RouteOperationHandler;
import com.fiorano.openesb.route.RouteOperationConfiguration;
import com.fiorano.openesb.transport.Message;
import com.fiorano.openesb.utils.exception.FioranoException;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRouteImpl<M extends Message> implements Route<M> {
    protected List<RouteOperationHandler> routeOperationHandlers = new ArrayList<RouteOperationHandler>();

    public AbstractRouteImpl(List<RouteOperationConfiguration> operationConfigurations) {
        if(!operationConfigurations.isEmpty()) {
            routeOperationHandlers = new ArrayList<RouteOperationHandler>(operationConfigurations.size());
            for (RouteOperationConfiguration configuration : operationConfigurations) {
                RouteOperationHandler routeOperationHandler = createHandler(configuration);
                routeOperationHandlers.add(routeOperationHandler);
            }
        }
    }

    public void handleMessage(M message)  {
        if(!routeOperationHandlers.isEmpty()) {
            for (RouteOperationHandler handler : routeOperationHandlers) {
                try {
                    handler.handleOperation(message);
                } catch (FilterMessageException e) {
                    // TODO: 17-01-2016
                    // Message skipped by selector - trace log.
                } catch (FioranoException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private RouteOperationHandler createHandler(RouteOperationConfiguration configuration) {
        if(configuration instanceof TransformationConfiguration) {
            return new TransformationOperationHandler((TransformationConfiguration) configuration);
        }
        return null;
    }

}
