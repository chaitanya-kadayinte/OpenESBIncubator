package com.fiorano.openesb.rmiconnector.api.proxy;

import com.fiorano.openesb.rmiconnector.api.*;
import com.fiorano.openesb.rmiconnector.api.ServiceException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface IRemoteServerProxy extends Remote {

    public Object invoke (String methodName,Object []methodArgs, HashMap additionalInfo) throws RemoteException, ServiceException;

}
