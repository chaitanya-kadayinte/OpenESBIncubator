/*
 * Copyright (c) Fiorano Software Pte. Ltd. and affiliates. All rights reserved. http://www.fiorano.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
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