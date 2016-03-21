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
package com.fiorano.openesb.applicationcontroller;

import com.fiorano.openesb.application.ApplicationRepository;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class Activator implements BundleActivator {

    @SuppressWarnings("unchecked")
    public void start(BundleContext context) throws Exception {
        System.out.println("Starting bundle - " + context.getBundle().getSymbolicName());
        ServiceReference<ApplicationRepository> applicationRepositoryRef = context.getServiceReference(ApplicationRepository.class);
        if (applicationRepositoryRef != null) {
            ApplicationRepository applicationRepository = context.getService(applicationRepositoryRef);

            ApplicationController applicationController = new ApplicationController(applicationRepository, context);
            context.registerService(ApplicationController.class.getName(), applicationController, null);
            //applicationController.launchApplication("SELECTOR","1.0");
            //applicationController.launchApplication("TRANSFORMATION","1.0");
            //applicationController.launchApplication("SENDERSELECTOR","1.0",null);
        }
        System.out.println("Started bundle - " + context.getBundle().getSymbolicName());

    }

    public void stop(BundleContext context) {
        System.out.println("Stopping the bundle - " + context.getBundle().getSymbolicName());
    }

}