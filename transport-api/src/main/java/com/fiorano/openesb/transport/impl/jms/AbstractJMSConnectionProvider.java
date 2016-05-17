package com.fiorano.openesb.transport.impl.jms;

import com.fiorano.openesb.transport.ConnectionProvider;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class AbstractJMSConnectionProvider implements ConnectionProvider<Connection,JMSConnectionConfiguration> {
    protected Properties properties;

    public AbstractJMSConnectionProvider(Properties properties) {
        this.properties = properties;
    }

    private Map<String,ConnectionFactory> connectionFactories = new HashMap<>();
    public void prepareConnectionMD(JMSConnectionConfiguration jmsConnectionConfiguration) throws Exception {
        String cfName = jmsConnectionConfiguration.getClientId();
        ConnectionFactory connectionFactory = getConnectionFactory(cfName);
        connectionFactories.put(cfName,connectionFactory);
    }

    protected abstract ConnectionFactory getConnectionFactory(String name) throws Exception;

    public Connection createConnection(JMSConnectionConfiguration jmsConnectionConfiguration) throws Exception{
        return connectionFactories.get(jmsConnectionConfiguration.getClientId()).createConnection();
    }

    public void releaseConnectionMD(JMSConnectionConfiguration jmsConnectionConfiguration) {
        ConnectionFactory connectionFactory = connectionFactories.remove(jmsConnectionConfiguration.getClientId());
    }
}
