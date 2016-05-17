
package com.fiorano.openesb.amq;

import com.fiorano.openesb.transport.impl.jms.AbstractJMSConnectionProvider;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.ConnectionFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AMQConnectionProvider extends AbstractJMSConnectionProvider {
    public AMQConnectionProvider(Properties properties) {
        super(properties);
    }

    @Override
    public ConnectionFactory getConnectionFactory(String name) {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setClientID(name);
        activeMQConnectionFactory.buildFromProperties(properties);
        return activeMQConnectionFactory;
    }
}
