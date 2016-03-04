package com.fiorano.openesb.rmiconnector.impl;

import com.fiorano.openesb.rmiconnector.api.IApplicationManager;
import com.fiorano.openesb.rmiconnector.api.IServiceManager;
import com.fiorano.openesb.rmiconnector.api.ServiceException;
import com.fiorano.openesb.rmiconnector.api.proxy.RemoteClientInterceptor;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.rmi.RemoteException;

/**
 * Created by Janardhan on 1/27/2016.
 */
public class InstanceHandler {

    private static final String APPLICATION_MANAGER = "APPLICATION_MANANGER";
    private static final String MICRO_SERVICE_MANAGER = "MICRO_SERVICE_MANANGER";
    //Rmi Manager Instance
    private RmiManager rmiManager;
    //Handle ID of the client
    private String handleID;

    private String context;
    //ApplicationManager
    private volatile ApplicationManager applicationManager;

    private volatile MicroServiceManager microServiceManager;


    public InstanceHandler(RmiManager rmiManager, String handleID) {
        this(rmiManager, handleID,"ESB");
    }

    public InstanceHandler(RmiManager rmiManager, String handleID, String context) {
        this.rmiManager = rmiManager;
        this.handleID = handleID;
        this.context = context;
    }
    /**
     * onUnreferenced will be called from the unreferenced method of Remote Object
     * @param e String
     */
    public synchronized void onUnReferenced(String e) {
        if (e.equals(APPLICATION_MANAGER)) {
            applicationManager = null;
        }else if (e.equals(MICRO_SERVICE_MANAGER)){
            microServiceManager=null;
        }
    }

    private void canlogoutForceFully() {
            try {
                rmiManager.logout(handleID);
            } catch (RemoteException willNeverHappen) {
            } catch (ServiceException ignore) {
            }
    }

    public synchronized IApplicationManager getApplicationManager() throws RemoteException {
        if (applicationManager == null) {
            //original resource == server side stub
            applicationManager = new ApplicationManager(rmiManager, this);
            //server side proxy instance to original resource.
            RemoteServerProxy serverSideProxy = new RemoteServerProxy(applicationManager,rmiManager.getRmiPort(),rmiManager.getCsf(),rmiManager.getSsf());

            //client proxy instance
            IApplicationManager returnObject = (IApplicationManager) Proxy.newProxyInstance
                    (
                            this.getClass().getClassLoader(),
                            new Class[]{IApplicationManager.class, Serializable.class},
                            //client stub & interceptor instance
                            new RemoteClientInterceptor(serverSideProxy)
                    );
            applicationManager.setClientProxyInstance(returnObject);
        }
        return applicationManager.getClientProxyInstance();
    }

    public synchronized IServiceManager getMicroServiceManager() throws RemoteException {
        if (microServiceManager == null) {
            //original resource == server side stub
            microServiceManager = new MicroServiceManager(rmiManager, this);
            //server side proxy instance to original resource.
            RemoteServerProxy serverSideProxy = new RemoteServerProxy(microServiceManager,rmiManager.getRmiPort(),rmiManager.getCsf(),rmiManager.getSsf());

            //client proxy instance
            IServiceManager returnObject = (IServiceManager) Proxy.newProxyInstance
                    (
                            this.getClass().getClassLoader(),
                            new Class[]{IServiceManager.class, Serializable.class},
                            //client stub & interceptor instance
                            new RemoteClientInterceptor(serverSideProxy)
                    );
            microServiceManager.setClientProxyInstance(returnObject);
        }
        return microServiceManager.getClientProxyInstance();
    }


    public void removeHandler() {
        applicationManager=null;
        microServiceManager=null;
    }

    String getHandleID() {
        return handleID;
    }
}

