package com.fiorano.openesb.rmiclient.tests;

import com.fiorano.openesb.rmiclient.RmiClient;
import com.fiorano.openesb.rmiconnector.api.IEventProcessManager;
import com.fiorano.openesb.rmiconnector.api.IRmiManager;
import com.fiorano.openesb.rmiconnector.api.ServiceException;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ApplicationLaunchStopTest implements TestCase{
    IRmiManager rmiManager;
    IEventProcessManager eventProcessManager;

    public void test() throws RemoteException, ServiceException {
        RmiClient rmiClient = null;
        try {
            rmiClient = new RmiClient();
        } catch (NotBoundException e) {
            throw new ServiceException(e.getMessage());
        }
        rmiManager = rmiClient.getRmiManager();
        String handleid = rmiManager.login("karaf", "karaf");
        eventProcessManager = rmiManager.getEventProcessManager(handleid);
        eventProcessManager.startEventProcess("SIMPLECHAT", "1.0", false);
        eventProcessManager.stopEventProcess("SIMPLECHAT", "1.0");
        rmiManager.logout(handleid);
    }
}
