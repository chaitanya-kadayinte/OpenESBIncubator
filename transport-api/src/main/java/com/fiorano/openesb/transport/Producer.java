package com.fiorano.openesb.transport;

public interface Producer<M extends Message> {

    void send(M message) throws Exception;

    void close() throws Exception;
}

