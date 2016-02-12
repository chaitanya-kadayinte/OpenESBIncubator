package com.fiorano.openesb.transport.impl.jms;

import com.fiorano.openesb.transport.Consumer;
import com.fiorano.openesb.transport.MessageListener;
import com.fiorano.openesb.utils.exception.FioranoException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;

public class JMSConsumer implements Consumer<JMSMessage> {
    private MessageConsumer messageConsumer;

    public JMSConsumer(MessageConsumer messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    public void attachMessageListener(final MessageListener<JMSMessage> messageListener) throws JMSException {

        messageConsumer.setMessageListener(new javax.jms.MessageListener() {
            public void onMessage(Message message) {
                try {
                    messageListener.messageReceived(new JMSMessage(message));
                } catch (FioranoException e) {
                    //todo log
                }
            }
        });
    }

    public void close() throws JMSException {
        messageConsumer.close();
    }
}
