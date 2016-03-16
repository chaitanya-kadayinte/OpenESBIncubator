package com.fiorano.openesb.route;

import com.fiorano.openesb.transport.Message;
import com.fiorano.openesb.utils.exception.FioranoException;

import javax.jms.JMSException;

public interface RouteOperationHandler<M extends Message> {
    @SuppressWarnings("DuplicateThrows")
    void handleOperation(M message) throws FilterMessageException, Exception;
}
