package com.fiorano.openesb.rmiclient;

import com.fiorano.openesb.rmiconnector.api.IRmiManager;
import com.fiorano.openesb.rmiconnector.client.FioranoRMIClientSocketFactory;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by Janardhan on 1/25/2016.
 */
public class RmiClient {

    public IRmiManager rmiManager;

    public RmiClient() throws RemoteException, NotBoundException {
        FioranoRMIClientSocketFactory csf = new FioranoRMIClientSocketFactory();
        Registry registry = LocateRegistry.getRegistry("localhost", 2047, csf);
        rmiManager = (IRmiManager) registry.lookup("rmi");
    }
    public IRmiManager getRmiManager() {
        return rmiManager;
    }

    public void setRmiManager(IRmiManager rmiManager) {
        this.rmiManager = rmiManager;
    }

}
