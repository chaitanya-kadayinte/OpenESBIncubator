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
package com.fiorano.openesb.microservice.bundle;

import com.fiorano.openesb.microservice.ccp.CCPEventManager;
import com.fiorano.openesb.microservice.launch.impl.MicroServiceLauncher;
import com.fiorano.openesb.microservice.repository.MicroServiceRepoManager;
import com.fiorano.openesb.transport.TransportService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;

public class Activator implements BundleActivator {

    private Logger logger;

    @SuppressWarnings("unchecked")
    public void start(BundleContext context) throws Exception {
        logger = LoggerFactory.getLogger(getClass());
        logger.trace("Starting Microservice bundle.");
        TransportService service = context.getService(context.getServiceReference(TransportService.class));
        CCPEventManager ccpEventManager = new CCPEventManager(service);
        MicroServiceLauncher microServiceLauncher = new MicroServiceLauncher(ccpEventManager);
        MicroServiceRepoManager microServiceRepoManager = MicroServiceRepoManager.getInstance();
        context.registerService(CCPEventManager.class,ccpEventManager,new Hashtable<String, Object>());
        context.registerService(MicroServiceLauncher.class, microServiceLauncher, new Hashtable<String, Object>());
        context.registerService(MicroServiceRepoManager.class, microServiceRepoManager, new Hashtable<String, Object>());
        logger.debug("Started Microservice bundle.");
    }

    public void stop(BundleContext context) {
        logger.trace("Stopped Microservice bundle.");
    }

}