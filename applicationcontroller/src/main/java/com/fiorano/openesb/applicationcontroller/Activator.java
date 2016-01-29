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
import com.fiorano.openesb.application.application.Application;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class Activator implements BundleActivator {

    public void start(BundleContext context) {
        System.out.println("Starting the application controller bundle");
        ServiceReference[] references = new ServiceReference[0];
        try {
            references = context.getServiceReferences(ApplicationRepository.class.getName(),null);
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
        }
        if (references != null) {
            ApplicationRepository applicationRepository = (ApplicationRepository) context.getService(references[0]);
            ApplicationController applicationController = new ApplicationController(applicationRepository);

            context.registerService(
                    ApplicationController.class.getName(), applicationController, null);
            try {
                Application application = applicationRepository.readApplication("SIMPLECHAT", "1.0");
                if (application != null) {
                    System.out.println(application.toString());

                } else {
                    System.out.println("error occured while reading the application");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void stop(BundleContext context) {
        System.out.println("Stopping the bundle");
    }

}