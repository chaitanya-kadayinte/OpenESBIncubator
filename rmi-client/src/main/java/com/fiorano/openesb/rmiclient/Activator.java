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
package com.fiorano.openesb.rmiclient;

import com.fiorano.openesb.rmiconnector.api.IApplicationManager;
import com.fiorano.openesb.rmiconnector.api.IRmiManager;
import com.fiorano.openesb.rmiconnector.api.ServiceException;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Activator implements BundleActivator {

    public void start(BundleContext context) {
        System.out.println("Starting the bundle - " + context.getBundle().getSymbolicName());
        InputStream in = getClass().getResourceAsStream("/totest.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        try {
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("#")) {
                    Class testClass = Class.forName(line);
                    Object testObject = testClass.newInstance();
                    Method testMethod = testClass.getMethod("test");
                    testMethod.invoke(testObject);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }






        /*RmiClient rmiClient = null;
        try {
            rmiClient = new RmiClient();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
        IRmiManager rmiManager = rmiClient.getRmiManager();
        String handleid = null;
        try {
            handleid = rmiManager.login("", "");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        System.out.println(handleid);
        try {
           IApplicationManager eventProcessManager = rmiManager.getApplicationManager(handleid);
           eventProcessManager.startApplication("OS_TEST", "1.0", false);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }*/
    }

    public void stop(BundleContext context) {
        System.out.println("Stopping the bundle - " + context.getBundle().getSymbolicName());
    }

}