package com.fiorano.openesb.route;

import com.fiorano.openesb.transport.Message;

public interface RouteOperationHandler<M extends Message> {
    void handleOperation(M message) throws FilterMessageException;
}
