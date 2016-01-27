package com.fiorano.openesb.amq;

import com.fiorano.openesb.transport.*;
import com.fiorano.openesb.transport.impl.jms.AbstractJMSTransportService;
import com.fiorano.openesb.transport.impl.jms.JMSPort;
import com.fiorano.openesb.transport.impl.jms.JMSMessage;
import com.fiorano.openesb.transport.impl.jms.JMSPortConfiguration;

import javax.jms.Session;

public class AMQTransportService extends AbstractJMSTransportService implements TransportService<JMSPort, JMSMessage, JMSPortConfiguration> {

    public AMQTransportService(Session session) {
        super(session);
    }

    public void disablePort(JMSPortConfiguration portConfiguration) {

    }

}
