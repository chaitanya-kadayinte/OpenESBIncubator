package com.fiorano.openesb.transport.impl.jms;

import com.fiorano.openesb.transport.Producer;

import javax.jms.JMSException;
import javax.jms.MessageProducer;

public class JMSProducer implements Producer<JMSMessage> {
    private MessageProducer producer;

    public JMSProducer(MessageProducer producer) {
        this.producer = producer;
    }

    public void send(JMSMessage message) throws JMSException {
        producer.send(message.message);
    }
}
