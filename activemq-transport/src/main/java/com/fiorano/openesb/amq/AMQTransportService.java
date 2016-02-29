package com.fiorano.openesb.amq;

import com.fiorano.openesb.transport.*;
import com.fiorano.openesb.transport.impl.jms.*;
import org.apache.activemq.broker.jmx.BrokerViewMBean;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class AMQTransportService extends AbstractJMSTransportService implements TransportService<JMSPort, JMSMessage> {


    private BrokerViewMBean adminMBean;

    public AMQTransportService() throws Exception {
        super();
        Properties properties = new Properties();
        try (FileInputStream inStream = new FileInputStream(System.getProperty("karaf.base") + File.separator
                + "etc" + File.separator + "com.fiorano.openesb.transport.provider.cfg")) {
            properties.load(inStream);
        }

        initializeAdminConnector(properties);
    }

    private void initializeAdminConnector(Properties properties) throws IOException, MalformedObjectNameException {
        String username = properties.getProperty("userName");
        String password = properties.getProperty("password");
        Map<String, String[]> env = new HashMap<>();
        String[] credentials = new String[]{username, password};
        env.put(JMXConnector.CREDENTIALS, credentials);
        String serviceURL = properties.getProperty("jmxURL");
        JMXConnector connector = JMXConnectorFactory.newJMXConnector(new JMXServiceURL(serviceURL), env);
        connector.connect();
        MBeanServerConnection connection = connector.getMBeanServerConnection();
        ObjectName activeMQ = new ObjectName("org.apache.activemq:type=Broker,brokerName=amq-broker");
        adminMBean = MBeanServerInvocationHandler.newProxyInstance(connection, activeMQ, BrokerViewMBean.class, true);
    }

    public ConnectionProvider getConnectionProvider() {
        return new AMQConnectionProvider();
    }

    public void disablePort(PortConfiguration portConfiguration) throws Exception {
        JMSPortConfiguration jmsPortConfiguration = (JMSPortConfiguration) portConfiguration;
        if (jmsPortConfiguration.getPortType() == JMSPortConfiguration.PortType.QUEUE) {
            adminMBean.removeQueue(jmsPortConfiguration.getName());
        } else {
            adminMBean.removeTopic(jmsPortConfiguration.getName());
        }
    }
}
