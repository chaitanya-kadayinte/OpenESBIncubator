
package com.fiorano.openesb.amq;

import com.fiorano.openesb.transport.impl.jms.AbstractJMSConnectionProvider;
import org.apache.activemq.ActiveMQConnectionFactory;

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
        try(FileInputStream inStream = new FileInputStream(System.getProperty("karaf.base") + File.separator
                + "etc" + File.separator + "com.fiorano.openesb.transport.provider.cfg")) {
            properties.load(inStream);
            activeMQConnectionFactory.buildFromProperties(properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return activeMQConnectionFactory;
    }
}
