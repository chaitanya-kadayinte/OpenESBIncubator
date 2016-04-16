package com.fiorano.openesb.transport;



public interface Consumer<M extends Message> {
    void attachMessageListener(MessageListener<M> messageListener) throws Exception;
    void close() throws Exception;
}
