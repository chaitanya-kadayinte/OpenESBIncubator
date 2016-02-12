package com.fiorano.openesb.transport.impl.jms;

import com.fiorano.openesb.transport.MessageConfiguration;

public class JMSMessageConfiguration implements MessageConfiguration {
    public MessageType getType() {
        return type;
    }

    public enum MessageType {
        Bytes, Text , Object , Stream;
    }
    private MessageType type;
    public JMSMessageConfiguration(MessageType type) {
        this.type = type;
    }
}
