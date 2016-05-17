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

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RmiMain {
    public static void main(String [] args){
        System.out.println("Starting the bundle- rmi client");
        RmiClient rmiClient = null;
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
            eventProcessManager.startApplication("SIMPLECHAT", "1.0");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }
}
