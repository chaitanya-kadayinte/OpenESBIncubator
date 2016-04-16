package com.fiorano.openesb.rmiconnector.impl;

import com.fiorano.openesb.rmiconnector.api.ServiceException;

import java.rmi.RemoteException;
import java.util.HashMap;

public interface IDistributedRemoteObject {

    /**
     * The method is invoked to get a service.
     * @param methodName method to invoke.
     * @param args arguments provided to the method
     * @param additionalInfo additional info like Client Locale, Client IP Addresses etc. Serves as a place holder.
     * @return Object service to be provided.
     * @throws RemoteException
     * @throws ServiceException
     */
    Object invoke(String methodName, Object[] args, HashMap additionalInfo) throws RemoteException, ServiceException;

    /**
     * Gets called when the object is no longer being referenced.
     * @see java.rmi.server.Unreferenced
     */
    void unreferenced();
}
