package com.fiorano.openesb.rmiconnector.impl;

import com.fiorano.openesb.rmiconnector.api.ServiceException;
import com.fiorano.openesb.rmiconnector.api.proxy.IRemoteServerProxy;

import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;
import java.util.HashMap;

/**
 * Created by Janardhan on 1/27/2016.
 */
    public class RemoteServerProxy extends UnicastRemoteObject implements IRemoteServerProxy, Unreferenced {


        private static final long serialVersionUID = 3635998601035212892L;
        private transient IDistributedRemoteObject resource;

        public RemoteServerProxy(IDistributedRemoteObject resource,int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
            super(port, csf, ssf);
            this.resource = resource;
        }

        public Object invoke(String methodName, Object[] args,HashMap additionalInfo) throws RemoteException, ServiceException {

            return resource.invoke(methodName,args,additionalInfo);
        }

        public void unreferenced() {
            resource.unreferenced();
        }
    }

