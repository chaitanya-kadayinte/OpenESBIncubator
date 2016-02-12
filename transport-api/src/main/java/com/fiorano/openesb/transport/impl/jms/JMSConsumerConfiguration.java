package com.fiorano.openesb.transport.impl.jms;

import com.fiorano.openesb.transport.ConsumerConfiguration;

public class JMSConsumerConfiguration implements ConsumerConfiguration<String> {
    private String selector;

    public JMSConsumerConfiguration(String selector) {
        this.selector = selector;
    }

    public String getSelector() {
        return selector;
    }
}
