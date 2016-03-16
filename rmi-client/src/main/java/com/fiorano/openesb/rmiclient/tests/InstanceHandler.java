/*
package com.fiorano.openesb.rmiclient.tests;

import com.fiorano.openesb.rmiconnector.api.IApplicationManager;
import com.fiorano.openesb.rmiconnector.api.IApplicationManagerListener;
import com.fiorano.openesb.rmiconnector.api.IRepoEventListener;
import com.fiorano.openesb.rmiconnector.api.proxy.RemoteClientInterceptor;
import com.fiorano.openesb.rmiconnector.impl.IDistributedRemoteObject;
import com.fiorano.openesb.rmiconnector.impl.RemoteServerProxy;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.rmi.RemoteException;

*/
/**
 * Created by Janardhan on 3/7/2016.
 *//*

public class InstanceHandler {
    private volatile ServiceRepositoryEventListener repositoryEventListener;
    private volatile ApplicationManagerEventListener epRepositoryEventListener;

    private RMIUtil rmiUtil;
    private int rmiPort;
    private RemoteServerProxy remoteServerProxy;

    public InstanceHandler() {
        try {
            this.rmiPort = 9713;
        } catch (Exception e) {
            // handled by the text input validator
        }
        rmiUtil = RMIUtil.getInstance();
    }

    public IRepoEventListener getServiceRepositoryEventListener(IRepoEventListener serviceRepository) throws RemoteException {
        if(repositoryEventListener == null) {
            repositoryEventListener = new ServiceRepositoryEventListener((OnlineServiceRepository) serviceRepository);

            RemoteServerProxy remoteServerProxy = new RemoteServerProxy((IDistributedRemoteObject) repositoryEventListener, rmiPort, rmiUtil.getCsf(), rmiUtil.getSsf());
            IRepoEventListener returnObject = (IRepoEventListener)Proxy.newProxyInstance
                    (
                            Thread.currentThread().getContextClassLoader(),
                            new Class[] {IRepoEventListener.class, Serializable.class},
                            //client stub & interceptor instance
                            new RemoteClientInterceptor(remoteServerProxy)
                    );
            repositoryEventListener.setClientProxyInstance(returnObject);
        }
        return repositoryEventListener.getClientProxyInstance();
    }

    public RMIUtil getRmiUtil() {
        return rmiUtil;
    }

    public int getRmiPort() {
        return rmiPort;
    }

    public IApplicationManager getEPManagerListener(EventProcessNode eventProcessNode) throws RemoteException {
        OnlineEventProcessListener eventProcessManagerListener = new OnlineEventProcessListener(eventProcessNode);
        remoteServerProxy = new RemoteServerProxy((IDistributedRemoteObject)eventProcessManagerListener, rmiPort, rmiUtil.getCsf(), rmiUtil.getSsf());
        IApplicationManager returnObject = (IApplicationManager) Proxy.newProxyInstance
                (
                        Thread.currentThread().getContextClassLoader(),
                        new Class[]{IApplicationManagerListener.class, Serializable.class},
                        //client stub & interceptor instance
                        new RemoteClientInterceptor(remoteServerProxy)
                );
        return returnObject;
    }

    public void setRemoteServerProxy(RemoteServerProxy remoteServerProxy) {
        this.remoteServerProxy = remoteServerProxy;
    }
}

*/
