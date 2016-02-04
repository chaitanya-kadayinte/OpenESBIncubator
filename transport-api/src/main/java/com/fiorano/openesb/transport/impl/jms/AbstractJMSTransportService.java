package com.fiorano.openesb.transport.impl.jms;

import com.fiorano.openesb.transport.*;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.Message;

import javax.jms.*;

public abstract class AbstractJMSTransportService implements TransportService<JMSPort, JMSMessage, JMSPortConfiguration> {

    private Session session;

    protected AbstractJMSTransportService(Session session) {
        this.session = session;
    }

    public Consumer<JMSMessage> createConsumer(JMSPort port, ConsumerConfiguration consumerConfiguration) throws Exception {
        System.out.println("Creating Message Consumer for " + port.getDestination().toString());
        MessageConsumer messageConsumer = session.createConsumer(port.getDestination(), ((JMSConsumerConfiguration) consumerConfiguration).getSelector());
        return new JMSConsumer(messageConsumer);
    }

    public Producer<JMSMessage> createProducer(JMSPort port, ProducerConfiguration producerConfiguration) throws JMSException {
        return new JMSProducer(session.createProducer(port.getDestination()));
    }

    public JMSMessage createMessage() throws Exception {
        return new JMSMessage(session.createTextMessage());
    }

    public com.fiorano.openesb.transport.Message createMessage(JMSMessageconfiguration config){
        return (com.fiorano.openesb.transport.Message) new ActiveMQBytesMessage();
    }


    public JMSPort enablePort(JMSPortConfiguration portConfiguration) throws Exception {
        switch (portConfiguration.getPortType()) {
            case QUEUE:
                return new JMSPort(session.createQueue(portConfiguration.getName()));
            case TOPIC:
                return new JMSPort(session.createTopic(portConfiguration.getName()));
        }
        return null;
    }
}
