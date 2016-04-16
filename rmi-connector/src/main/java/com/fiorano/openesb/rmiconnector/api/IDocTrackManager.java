package com.fiorano.openesb.rmiconnector.api;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IDocTrackManager extends Remote {

    /**
     * Returns whether sbw is a custom database with call out.
     *
     * @throws java.rmi.RemoteException  A communication-related exception that may occur during the execution of a remote method call
     * @throws ServiceException Thrown if the Schema Reference is failed to be added to Schema repository
     */
    public boolean isCallOutEnabled() throws RemoteException, ServiceException;


    /**
     * Returns Data base call out query config
     *
     * @throws java.rmi.RemoteException  A communication-related exception that may occur during the execution of a remote method call
     * @throws ServiceException Thrown if the Schema Reference is failed to be added to Schema repository
     */
    public String getDBCallOutConfig() throws RemoteException, ServiceException;
}
