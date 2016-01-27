package com.fiorano.openesb.transport.impl.jms;

import com.fiorano.openesb.transport.Consumer;
import com.fiorano.openesb.transport.MessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;

public class JMSConsumer implements Consumer<JMSMessage> {
    private MessageConsumer messageConsumer;

    public JMSConsumer(MessageConsumer messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    public void attachMessageListener(final MessageListener<JMSMessage> messageListener) throws JMSException {
        System.out.println("Attaching Message Listener");

        messageConsumer.setMessageListener(new javax.jms.MessageListener() {
            public void onMessage(Message message) {
                System.out.println("Message Received from Queue");
                messageListener.messageReceived(new JMSMessage(message));
            }
        });
    }

    public void close() throws JMSException {
        messageConsumer.close();
    }
}
