package com.fiorano.openesb.transport;

import com.fiorano.openesb.utils.exception.FioranoException;

public interface MessageListener<M extends Message> {
    void messageReceived(M message) throws FioranoException;
}
