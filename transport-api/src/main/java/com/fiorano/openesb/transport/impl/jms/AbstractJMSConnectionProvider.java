/**
 * Copyright (c) 1999-2007, Fiorano Software Technologies Pvt. Ltd. and affiliates.
 * Copyright (c) 2008-2014, Fiorano Software Pte. Ltd. and affiliates.
 * <p>
 * All rights reserved.
 * <p>
 * This software is the confidential and proprietary information
 * of Fiorano Software ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * enclosed with this product or entered into with Fiorano.
 * <p>
 * Created by chaitanya on 09-02-2016.
 */

/**
 * Created by chaitanya on 09-02-2016.
 */
package com.fiorano.openesb.transport.impl.jms;

import com.fiorano.openesb.transport.ConnectionProvider;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractJMSConnectionProvider implements ConnectionProvider<Connection,JMSConnectionConfiguration> {
    private Map<String,ConnectionFactory> connectionFactories = new HashMap<>();
    public void prepareConnectionMD(JMSConnectionConfiguration jmsConnectionConfiguration) throws Exception {
        String cfName = jmsConnectionConfiguration.getClientId();
        ConnectionFactory connectionFactory = getConnectionFactory(cfName);
        connectionFactories.put(cfName,connectionFactory);
    }

    protected abstract ConnectionFactory getConnectionFactory(String name);

    public Connection createConnection(JMSConnectionConfiguration jmsConnectionConfiguration) throws Exception{
        return connectionFactories.get(jmsConnectionConfiguration.getClientId()).createConnection();
    }

    public void releaseConnectionMD(JMSConnectionConfiguration jmsConnectionConfiguration) {
        ConnectionFactory connectionFactory = connectionFactories.remove(jmsConnectionConfiguration.getClientId());
    }
}
