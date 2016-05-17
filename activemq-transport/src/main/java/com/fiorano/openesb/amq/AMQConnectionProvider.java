/*
 * Copyright (c) Fiorano Software Pte. Ltd. and affiliates. All rights reserved. http://www.fiorano.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

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
