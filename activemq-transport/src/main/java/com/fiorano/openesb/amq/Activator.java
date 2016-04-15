/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fiorano.openesb.amq;

import com.fiorano.openesb.transport.ConnectionProvider;
import com.fiorano.openesb.transport.TransportService;
import com.fiorano.openesb.transport.impl.jms.JMSConnectionConfiguration;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.util.Collection;
import java.util.Hashtable;

public class Activator implements BundleActivator {

    private BundleContext bundleContext;
    private AMQTransportService service;
    private Logger logger;

    public Activator() {
        logger = LoggerFactory.getLogger(getClass());
    }

    public void start(BundleContext context) throws Exception {
        System.out.println("Starting Active MQ Transport");
        this.bundleContext = context;
        try {
            service = new AMQTransportService();
        } catch (JMSException e) {
            System.out.println("Could not connect to MQ Server.");
            context.getBundle(0).stop();
        }
        bundleContext.registerService(TransportService.class, service, new Hashtable<String, Object>());
        System.out.println("Started Active MQ Transport");
    }

    public void stop(BundleContext context) {
        System.out.println("Stopping Active MQ Transport");
        try {
            service.stop();
        } catch (Exception e) {
            logger.debug("Error stopping Active MQ Transport " + e.getMessage());
        }
        System.out.println("Stopped Active MQ Transport");
    }

}