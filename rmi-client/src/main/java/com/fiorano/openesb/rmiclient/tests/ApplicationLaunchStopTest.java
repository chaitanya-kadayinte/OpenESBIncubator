/*
 * Copyright (c) Fiorano Software Pte. Ltd. and affiliates. All rights reserved. http://www.fiorano.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.fiorano.openesb.rmiclient.tests;

import com.fiorano.openesb.rmiclient.RmiClient;
import com.fiorano.openesb.rmiconnector.api.IApplicationManager;
import com.fiorano.openesb.rmiconnector.api.IRmiManager;
import com.fiorano.openesb.rmiconnector.api.ServiceException;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ApplicationLaunchStopTest implements TestCase{
    IRmiManager rmiManager;
    IApplicationManager eventProcessManager;

    public void test() throws RemoteException, ServiceException {
        RmiClient rmiClient = null;
        try {
            rmiClient = new RmiClient();
        } catch (NotBoundException e) {
            throw new ServiceException(e.getMessage());
        }
        rmiManager = rmiClient.getRmiManager();
        String handleid = rmiManager.login("karaf", "karaf");
        eventProcessManager = rmiManager.getApplicationManager(handleid);
        eventProcessManager.startApplication("EVENT_PROCESS2", "1.0");
        eventProcessManager.stopApplication("EVENT_PROCESS2", "1.0");
        rmiManager.logout(handleid);
    }
}
