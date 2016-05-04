package com.fiorano.openesb.amq;

import com.fiorano.openesb.transport.*;
import com.fiorano.openesb.transport.impl.jms.*;
import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
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
    private JMXConnector connector;
    private AMQConnectionProvider amqConnectionProvider;

    public AMQTransportService() throws Exception {
        super();
        Properties properties = new Properties();
        try (FileInputStream inStream = new FileInputStream(System.getProperty("user.dir") + File.separator
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
        connector = JMXConnectorFactory.newJMXConnector(new JMXServiceURL(serviceURL), env);
        connector.connect();
        MBeanServerConnection connection = connector.getMBeanServerConnection();
        ObjectName activeMQ = new ObjectName("org.apache.activemq:type=Broker,brokerName=amq-broker");
        adminMBean = MBeanServerInvocationHandler.newProxyInstance(connection, activeMQ, BrokerViewMBean.class, true);
    }

    public ConnectionProvider getConnectionProvider() {
        amqConnectionProvider = new AMQConnectionProvider();
        return amqConnectionProvider;
    }

    public void disablePort(PortConfiguration portConfiguration) throws Exception {
        JMSPortConfiguration jmsPortConfiguration = (JMSPortConfiguration) portConfiguration;
        if (jmsPortConfiguration.getPortType() == JMSPortConfiguration.PortType.QUEUE) {
            adminMBean.removeQueue(jmsPortConfiguration.getName());
        } else {
            adminMBean.removeTopic(jmsPortConfiguration.getName());
        }
    }

    public void stop() {
        try {
            if (adminMBean != null) {
                try {
                    adminMBean.stop();
                } catch (InstanceNotFoundException e) {
                    Logger myLogger = LoggerFactory.getLogger(Activator.class);
                    myLogger.trace("Error stopping Admin MBean " + e.getMessage());
                }
            }
            if (connector != null) {
                //this could take time
                connector.close();
            }
        } catch(Exception e){
                //todo
                e.printStackTrace();
            }
        }
    }
