package com.fiorano.openesb.transport.impl.jms;

import com.fiorano.openesb.transport.ConsumerConfiguration;

public class JMSConsumerConfiguration implements ConsumerConfiguration<String> {
    public String getSelector() {
        return null;
    }
}
