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
