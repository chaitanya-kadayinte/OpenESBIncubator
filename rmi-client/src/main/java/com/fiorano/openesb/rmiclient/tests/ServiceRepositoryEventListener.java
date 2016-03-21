package com.fiorano.openesb.rmiclient.tests;

import com.fiorano.openesb.rmiconnector.api.IRepoEventListener;

import java.rmi.RemoteException;

/**
 * Created by Janardhan on 3/7/2016.
 */
public class ServiceRepositoryEventListener implements IRepoEventListener{
    public void serviceDeleted(String serviceGUID, float serviceVersion) throws RemoteException {

    }

    public void serviceDeployed(String serviceGUID, float serviceVersion) throws RemoteException {

    }

    public void applicationDeleted(String appGUID, float appVersion, String handleId) throws RemoteException {

    }

    public void applicationDeployed(String appGUID, float appVersion, String handleId) throws RemoteException {

    }

    public void resourceDeployed(String resourceName, String serviceGUID, float serviceVersion) throws RemoteException {

    }

    public void resourceDeleted(String resourceName, String serviceGUID, float serviceVersion) throws RemoteException {

    }

    public void descriptorModified(String serviceGUID, float serviceVersion) throws RemoteException {

    }
}
