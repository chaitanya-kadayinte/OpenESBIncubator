package com.fiorano.openesb.route.impl;

import com.fiorano.openesb.route.*;
import com.fiorano.openesb.transport.Message;
import com.fiorano.openesb.utils.exception.FioranoException;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRouteImpl<M extends Message> implements Route<M> {
    protected List<RouteOperationHandler> routeOperationHandlers = new ArrayList<>();

    public AbstractRouteImpl(List<RouteOperationConfiguration> operationConfigurations) throws Exception {

        routeOperationHandlers = new ArrayList<>(operationConfigurations.size());
        //routeOperationHandlers.add(new CarryForwardContextHandler());
        //routeOperationHandlers.add(new MessageCreationHandler());
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
                    handler.handleOperation(message);
                }
            } catch (FilterMessageException e) {
                // TODO: 17-01-2016
                // Message skipped by selector - trace log.
            } catch (FioranoException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
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
