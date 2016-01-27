package com.fiorano.openesb.transport;

public interface MessageListener<M extends Message> {
    void messageReceived(M message);
}
