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

import javax.jms.*;
import java.util.Collection;
import java.util.Hashtable;

public class Activator implements BundleActivator {

    private BundleContext bundleContext;
    private AMQTransportService service;

    public void start(BundleContext context) throws Exception {
        System.out.println("Starting the bundle " + context.getBundle().getSymbolicName());
        this.bundleContext = context;
        service = new AMQTransportService();
        bundleContext.registerService(TransportService.class, service,new Hashtable<String, Object>());
        System.out.println("Started the bundle " + context.getBundle().getSymbolicName());
    }

    public void stop(BundleContext context) {
        System.out.println("Stopping the bundle " + context.getBundle().getSymbolicName());
        try {
            service.stop();
        } catch (Exception e) {

        }
        System.out.println("Stopped the bundle " + context.getBundle().getSymbolicName());
    }

    private ServiceReference<ConnectionFactory> lookupConnectionFactory(String name) {
        Collection<ServiceReference<ConnectionFactory>> references;
        try {
            references = bundleContext.getServiceReferences(ConnectionFactory.class,
                    "(|(osgi.jndi.service.name=" + name + ")(name=" + name + ")(service.id=" + name + "))");
        } catch (InvalidSyntaxException e) {
            throw new RuntimeException("Error finding connection factory service " + name, e);
        }
        if (references == null || references.size() == 0) {
            throw new IllegalArgumentException("No JMS connection factory found for " + name);
        }
        if (references.size() > 1) {
            throw new IllegalArgumentException("Multiple JMS connection factories found for " + name);
        }
        return references.iterator().next();
    }
}