package com.fiorano.openesb.route;

import com.fiorano.openesb.transport.Message;
import com.fiorano.openesb.utils.exception.FioranoException;

public interface RouteOperationHandler<M extends Message> {
    void handleOperation(M message) throws FilterMessageException, FioranoException;
}
