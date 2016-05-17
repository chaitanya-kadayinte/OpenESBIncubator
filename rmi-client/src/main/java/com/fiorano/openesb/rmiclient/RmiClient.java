/*
 * Copyright (c) Fiorano Software Pte. Ltd. and affiliates. All rights reserved. http://www.fiorano.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.fiorano.openesb.rmiclient;

import com.fiorano.openesb.rmiconnector.api.IRmiManager;
import com.fiorano.openesb.rmiconnector.client.FioranoRMIClientSocketFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;

public class RmiClient {

    public IRmiManager rmiManager;

    public RmiClient() throws RemoteException, NotBoundException {
        FioranoRMIClientSocketFactory csf = new FioranoRMIClientSocketFactory();
        File configFile = new File(System.getProperty("user.dir") + File.separator
                + "etc" + File.separator + "com.fiorano.openesb.rmiconnector.cfg");
        if(!configFile.exists()){
            return;
        }
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(configFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Integer rmiRegistryPort = Integer.valueOf(prop.getProperty("RmiRegistryPort"));
        if(rmiRegistryPort==null){
            rmiRegistryPort = 2099;
        }
        Registry registry = LocateRegistry.getRegistry("localhost", rmiRegistryPort, csf);
        rmiManager = (IRmiManager) registry.lookup("rmi");
    }
    public IRmiManager getRmiManager() {
        return rmiManager;
    }

    public void setRmiManager(IRmiManager rmiManager) {
        this.rmiManager = rmiManager;
    }

}
