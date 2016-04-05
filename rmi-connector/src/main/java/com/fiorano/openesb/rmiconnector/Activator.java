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
package com.fiorano.openesb.rmiconnector;

import com.fiorano.openesb.rmiconnector.api.IRmiManager;
import com.fiorano.openesb.rmiconnector.api.ServiceException;
import com.fiorano.openesb.rmiconnector.connector.RmiConnector;
import com.fiorano.openesb.rmiconnector.impl.RmiManager;
import com.fiorano.openesb.utils.exception.FioranoException;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Activator implements BundleActivator {

    private Logger logger;

    public void start(BundleContext context) {
        logger = LoggerFactory.getLogger(getClass());
        logger.trace("Starting RMI bundle.");
        Thread.currentThread().setContextClassLoader(
                this.getClass().getClassLoader());
        RmiConnector rmiConnector = new RmiConnector();
        try {
            rmiConnector.createService();
            int rmiRegisryPort = rmiConnector.getRmiConnectorConfig().getRmiRegistryPort();
            int rmiServerPort = rmiConnector.getRmiConnectorConfig().getRmiServerPort();
            IRmiManager rmiManager = new RmiManager(context, rmiConnector);
            IRmiManager rmiManagerStub = (IRmiManager) UnicastRemoteObject.exportObject(rmiManager, rmiServerPort, rmiConnector.getCsf(), rmiConnector.getSsf());
            Registry registry = LocateRegistry.getRegistry(rmiRegisryPort);
            try {
                registry.unbind("rmi");
            } catch (NotBoundException | RemoteException ignored) {

            }
            registry.bind("rmi", rmiManagerStub);
        } catch (Exception e) {
            logger.error("Errors occurred while activating RMI modules", e);
        }
        logger.trace("Started RMI bundle.");
    }

    public void stop(BundleContext context) {
        logger.trace("Stopped RMI bundle.");
    }

}