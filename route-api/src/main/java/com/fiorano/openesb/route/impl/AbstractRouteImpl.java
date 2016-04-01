package com.fiorano.openesb.route.impl;

import com.fiorano.openesb.route.*;
import com.fiorano.openesb.route.bundle.Activator;
import com.fiorano.openesb.transport.Message;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRouteImpl<M extends Message> implements Route<M> {
    protected List<RouteOperationHandler> routeOperationHandlers = new ArrayList<>();

    public AbstractRouteImpl(List<RouteOperationConfiguration> operationConfigurations) throws Exception {

        routeOperationHandlers = new ArrayList<>(operationConfigurations.size());
        if (!operationConfigurations.isEmpty()) {
            for (RouteOperationConfiguration configuration : operationConfigurations) {
                RouteOperationHandler routeOperationHandler = createHandler(configuration);
                routeOperationHandlers.add(routeOperationHandler);
            }

        }
    }

    public void handleMessage(M message) {
        if (!routeOperationHandlers.isEmpty()) {
            try {
                for (RouteOperationHandler handler : routeOperationHandlers) {
                    LoggerFactory.getLogger(Activator.class).trace("Handling Operation " + handler.toString());
                    handler.handleOperation(message);
                }
            } catch (FilterMessageException e) {
                // TODO: 17-01-2016
                LoggerFactory.getLogger(Activator.class).debug("Message skipped by selector : " + e.getMessage());// Message skipped by selector - debug log.
            } catch (Throwable e) {
                LoggerFactory.getLogger(Activator.class).error("severe","Exception while applying handlers "+ e.getMessage());
            }
        }
    }

    private RouteOperationHandler createHandler(RouteOperationConfiguration configuration) throws Exception {
        if (configuration instanceof MessageCreationConfiguration) {
            return new MessageCreationHandler((MessageCreationConfiguration) configuration);
        } else if (configuration instanceof CarryForwardContextConfiguration) {
            return new CarryForwardContextHandler((CarryForwardContextConfiguration) configuration);
        } else if (configuration instanceof TransformationConfiguration) {
            return new TransformationOperationHandler((TransformationConfiguration) configuration);
        } else if (configuration instanceof SelectorConfiguration) {
            return new XmlSelectorHandler((XmlSelectorConfiguration) configuration);
        } else if (configuration instanceof SenderSelectorConfiguration) {
            return new SenderSelector((SenderSelectorConfiguration) configuration);
        }
        return null;
    }

}
