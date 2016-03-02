package com.fiorano.openesb.rmiconnector.impl;

import com.fiorano.openesb.rmiconnector.api.IApplicationManager;
import com.fiorano.openesb.rmiconnector.api.ServiceException;
import com.fiorano.openesb.rmiconnector.api.proxy.RemoteClientInterceptor;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.rmi.RemoteException;

/**
 * Created by Janardhan on 1/27/2016.
 */
public class InstanceHandler {

    private static final String EVENT_PROCESS_MANAGER = "EVENT_PROCESS_MANANGER";
    //Rmi Manager Instance
    private RmiManager rmiManager;
    //Handle ID of the client
    private String handleID;

    private String context;
    //ApplicationManager
    private volatile ApplicationManager applicationManager;


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
        if (e.equals(EVENT_PROCESS_MANAGER)) {
            applicationManager = null;
        }
    }

    private void canlogoutForceFully() {
        if (applicationManager == null) {
            try {
                rmiManager.logout(handleID);
            } catch (RemoteException willNeverHappen) {
            } catch (ServiceException ignore) {
            }
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


    public void removeHandler() {
        if (applicationManager != null) {
            onUnReferenced(EVENT_PROCESS_MANAGER);
        }
    }

    String getHandleID() {
        return handleID;
    }
}

