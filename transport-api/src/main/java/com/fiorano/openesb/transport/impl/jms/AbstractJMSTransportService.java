package com.fiorano.openesb.transport.impl.jms;

import com.fiorano.openesb.transport.*;
import com.fiorano.openesb.utils.config.ConfigurationLookupHelper;
import org.osgi.framework.BundleException;

import javax.jms.*;
import javax.jms.Message;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class AbstractJMSTransportService implements TransportService<JMSPort, JMSMessage> {

    private Session session;

    protected AbstractJMSTransportService() throws JMSException {
        ConnectionFactory cf = ((AbstractJMSConnectionProvider) getConnectionProvider()).getConnectionFactory("ConnectionFactory");
        Connection connection;
        String connectionRetryCount = ConfigurationLookupHelper.getInstance().getValue("CONNECTION_RETRY_COUNT", "5");
        int count = Integer.valueOf(connectionRetryCount), i = 0;
        while ((connection = getConnection(cf))== null && i++ < count) {
            try {
                String connectionRetryInterval = ConfigurationLookupHelper.getInstance().getValue("CONNECTION_LOOKUP_INTERVAL", "2000");
                Thread.sleep(Long.valueOf(connectionRetryInterval));
            } catch (InterruptedException e1) {
                //
            }
        }
        if(connection == null) {
            throw new JMSException("Could not connect to JMS server");
        }
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    private Connection getConnection(ConnectionFactory cf) throws JMSException {
        try {
            ConfigurationLookupHelper configurationLookupHelper = ConfigurationLookupHelper.getInstance();
            Connection connection = cf.createConnection(configurationLookupHelper.getValue("userName"),configurationLookupHelper.getValue("password"));
            connection.start();
            return connection;
        } catch (JMSException e) {
            if (e.getMessage().toUpperCase().contains("CONNECT ")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    public Consumer<JMSMessage> createConsumer(JMSPort port, ConsumerConfiguration consumerConfiguration) throws Exception {
        String selector = ((JMSConsumerConfiguration) consumerConfiguration).getSelector();
        MessageConsumer messageConsumer = selector != null ? session.createConsumer(port.getDestination(), selector) :
                session.createConsumer(port.getDestination());
        return new JMSConsumer(messageConsumer);
    }

    public Producer<JMSMessage> createProducer(JMSPort port, ProducerConfiguration producerConfiguration) throws JMSException {
        return new JMSProducer(session.createProducer(port.getDestination()));
    }

    public JMSMessage createMessage(MessageConfiguration messageConfiguration) throws Exception {
        JMSMessageConfiguration config = (JMSMessageConfiguration) messageConfiguration;
        Message message;
        switch (config.getType()) {
            case Bytes:
                message = session.createBytesMessage();
                break;
            case Text:
                message = session.createTextMessage();
                break;
            case Stream:
                message = session.createStreamMessage();
                break;
            case Object:
                message = session.createObjectMessage();
                break;
            default:
                message = session.createMessage();
        }
        return new JMSMessage(message);
    }


    public JMSPort enablePort(PortConfiguration configuration) throws Exception {
        JMSPortConfiguration portConfiguration = (JMSPortConfiguration) configuration;
        switch (portConfiguration.getPortType()) {
            case QUEUE:
                return new JMSPort(session.createQueue(portConfiguration.getName()));
            case TOPIC:
                return new JMSPort(session.createTopic(portConfiguration.getName()));
        }
        return null;
    }
}
