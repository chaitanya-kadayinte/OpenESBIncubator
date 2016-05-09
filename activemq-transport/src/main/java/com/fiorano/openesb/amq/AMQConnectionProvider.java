
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
    @Override
    public ConnectionFactory getConnectionFactory(String name) {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setClientID(name);
        Properties properties = new Properties();
        try(FileInputStream inStream = new FileInputStream(System.getProperty("user.dir") + File.separator
                + "etc" + File.separator + "com.fiorano.openesb.transport.provider.cfg")) {
            properties.load(inStream);
            activeMQConnectionFactory.buildFromProperties(properties);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            Logger logger = LoggerFactory.getLogger(Activator.class);
            logger.debug("JMS connection failed - " + e.getMessage());
        }
        return activeMQConnectionFactory;
    }
}
