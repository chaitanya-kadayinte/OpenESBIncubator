/*
 * Copyright (c) Fiorano Software Pte. Ltd. and affiliates. All rights reserved. http://www.fiorano.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.fiorano.openesb.route;

import com.fiorano.openesb.transport.Message;
import com.fiorano.openesb.utils.exception.FioranoException;

import javax.jms.JMSException;

public interface RouteOperationHandler<M extends Message> {
    @SuppressWarnings("DuplicateThrows")
    void handleOperation(M message) throws FilterMessageException, Exception;
}
