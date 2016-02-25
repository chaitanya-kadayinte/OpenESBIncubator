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
