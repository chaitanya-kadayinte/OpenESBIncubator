package com.fiorano.openesb.amq;

import com.fiorano.openesb.transport.*;
import com.fiorano.openesb.transport.impl.jms.*;

import javax.jms.JMSException;
import javax.jms.Session;

public class AMQTransportService extends AbstractJMSTransportService implements TransportService<JMSPort, JMSMessage> {

    public AMQTransportService() throws JMSException {
        super();
    }

    public ConnectionProvider getConnectionProvider() {
        return new AMQConnectionProvider();
    }

    public void disablePort(PortConfiguration portConfiguration) throws Exception {

    }
}
