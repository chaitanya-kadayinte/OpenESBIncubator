package com.fiorano.openesb.route.impl;

import com.fiorano.openesb.route.*;
import com.fiorano.openesb.route.bundle.Activator;
import com.fiorano.openesb.transport.Message;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class AbstractRouteImpl<M extends Message> implements Route<M> {
    protected Map<RouteOperationType, RouteOperationHandler> routeOperationHandlers;

    public AbstractRouteImpl(List<RouteOperationConfiguration> operationConfigurations) throws Exception {

        routeOperationHandlers = Collections.synchronizedMap(new LinkedHashMap<RouteOperationType, RouteOperationHandler>(operationConfigurations.size()));
        if (!operationConfigurations.isEmpty()) {
            for (RouteOperationConfiguration configuration : operationConfigurations) {
                RouteOperationHandler routeOperationHandler = createHandler(configuration);
                routeOperationHandlers.put(configuration.getRouteOperationType(), routeOperationHandler);
            }
        }
    }

    public void handleMessage(M message) {
        if (!routeOperationHandlers.isEmpty()) {
            try {
                for (RouteOperationHandler handler : routeOperationHandlers.values()) {
                    LoggerFactory.getLogger(Activator.class).trace("Handling Operation " + handler.toString());
                    handler.handleOperation(message);
                }
            } catch (FilterMessageException e) {
                LoggerFactory.getLogger(Activator.class).debug("Message skipped by selector : " + e.getMessage());// Message skipped by selector - debug log.
            } catch (Throwable e) {
                LoggerFactory.getLogger(Activator.class).error("Exception while applying handlers "+ e.getMessage() + " Trace " + Arrays.toString(e.getStackTrace()));
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

    public void modifyHandler(RouteOperationConfiguration configuration) throws Exception {
        if (configuration instanceof MessageCreationConfiguration) {
             routeOperationHandlers.put(configuration.getRouteOperationType(),new MessageCreationHandler((MessageCreationConfiguration) configuration));
        } else if (configuration instanceof CarryForwardContextConfiguration) {
            routeOperationHandlers.put(configuration.getRouteOperationType(),new CarryForwardContextHandler((CarryForwardContextConfiguration) configuration));
        } else if (configuration instanceof TransformationConfiguration) {
            routeOperationHandlers.put(configuration.getRouteOperationType(),new TransformationOperationHandler((TransformationConfiguration) configuration));
        } else if (configuration instanceof SelectorConfiguration) {
            routeOperationHandlers.put(configuration.getRouteOperationType(),new XmlSelectorHandler((XmlSelectorConfiguration) configuration));
        } else if (configuration instanceof SenderSelectorConfiguration) {
            routeOperationHandlers.put(configuration.getRouteOperationType(),new SenderSelector((SenderSelectorConfiguration) configuration));
        }
    }

}
