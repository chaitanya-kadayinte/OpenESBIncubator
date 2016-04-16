package com.fiorano.openesb.route;

import javax.jms.Message;

public interface JMSMessageTransformer {

    /**
     * The function takes in a message and returns the transformed message.
     * Usage : Route provides an api which allows this call back to be set.
     * On receipt of any message a transformation can be applied.
     */
    public String transform(Message msg)
            throws Exception;
}
