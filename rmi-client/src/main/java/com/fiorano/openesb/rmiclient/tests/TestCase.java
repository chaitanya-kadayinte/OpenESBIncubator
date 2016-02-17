package com.fiorano.openesb.rmiclient.tests;

import com.fiorano.openesb.rmiconnector.api.ServiceException;

import java.rmi.RemoteException;

public interface TestCase {
    public void test() throws RemoteException, ServiceException;
}
