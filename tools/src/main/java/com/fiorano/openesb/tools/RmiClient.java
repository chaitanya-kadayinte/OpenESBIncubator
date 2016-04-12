package com.fiorano.openesb.tools;

import com.fiorano.openesb.microservice.repository.MicroServiceRepoManager;
import com.fiorano.openesb.rmiconnector.api.IApplicationManager;
import com.fiorano.openesb.rmiconnector.api.IRmiManager;
import com.fiorano.openesb.rmiconnector.api.IServiceManager;
import com.fiorano.openesb.rmiconnector.api.ServiceException;
import com.fiorano.openesb.rmiconnector.client.FioranoRMIClientSocketFactory;
import com.fiorano.openesb.rmiconnector.impl.MicroServiceManager;
import com.fiorano.openesb.rmiconnector.impl.RmiManager;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by Janardhan on 4/11/2016.
 */
public class RmiClient {
    IRmiManager rmiManager;
    IApplicationManager applicationManager;
    IServiceManager microServiceManager;
    public RmiClient(RmiLoginInfo loginInfo) throws RemoteException, NotBoundException, ServiceException {
        FioranoRMIClientSocketFactory csf = new FioranoRMIClientSocketFactory();
        Registry registry = LocateRegistry.getRegistry(loginInfo.hostname, loginInfo.port, csf);
        rmiManager = (IRmiManager) registry.lookup("rmi");
        String handleId = rmiManager.login(loginInfo.user, loginInfo.pwd);
        applicationManager = rmiManager.getApplicationManager(handleId);
        microServiceManager = rmiManager.getServiceManager(handleId);
    }

    public IRmiManager getRmiManager() {
        return rmiManager;
    }

    public IApplicationManager getApplicationManager() {
        return applicationManager;
    }

    public IServiceManager getMicroServiceManager() {
        return microServiceManager;
    }
}
