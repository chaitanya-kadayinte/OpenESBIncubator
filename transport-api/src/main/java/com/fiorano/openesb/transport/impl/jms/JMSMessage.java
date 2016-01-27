package com.fiorano.openesb.transport.impl.jms;

import com.fiorano.openesb.transport.Message;

public class JMSMessage implements Message<javax.jms.Message> {
    javax.jms.Message message;

    public JMSMessage(javax.jms.Message message) {
        this.message = message;
    }

    public javax.jms.Message getMessage() {
        return message;
    }
}
