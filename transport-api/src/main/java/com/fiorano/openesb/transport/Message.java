package com.fiorano.openesb.transport;

public interface Message<M> {

    M getMessage();

    String getBody() throws Exception;


    void setInternalMessage(M message);

}
