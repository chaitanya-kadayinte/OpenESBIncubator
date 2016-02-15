package com.fiorano.openesb.rmiconnector.impl;

import com.fiorano.openesb.application.application.Application;
import com.fiorano.openesb.applicationcontroller.ApplicationController;
import com.fiorano.openesb.events.EventsManager;
import com.fiorano.openesb.security.ConnectionHandle;
import com.fiorano.openesb.security.SecurityManager;
import com.fiorano.openesb.rmiconnector.api.*;
import com.fiorano.openesb.rmiconnector.api.ServiceException;
import org.osgi.framework.*;
import org.osgi.framework.ServiceReference;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.rmi.RemoteException;
import java.rmi.server.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Janardhan on 1/22/2016.
 */
public class RmiManager implements IRmiManager{

    Map<String, InstanceHandler> handlerMap = new HashMap<String, InstanceHandler>();
    ApplicationController applicationController;
    EventsManager eventsManager;
    SecurityManager securityManager;
    private RMIServerSocketFactory ssf;
    private RMIClientSocketFactory csf;
    private int rmiPort;
    private DapiEventManager dapiEventManager;

    public RmiManager(BundleContext context, int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        org.osgi.framework.ServiceReference[] references = new org.osgi.framework.ServiceReference[0];
        try {
            references = context.getServiceReferences(ApplicationController.class.getName(),null);
            applicationController = (ApplicationController) context.getService(references[0]);
            references = context.getServiceReferences(EventsManager.class.getName(), null);
            eventsManager = (EventsManager) context.getService(references[0]);
            references = context.getServiceReferences(SecurityManager.class.getName(), null);
            securityManager = (SecurityManager) context.getService(references[0]);
            dapiEventManager = new DapiEventManager(eventsManager);
            dapiEventManager.startEventListener();

        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
        }
        this.ssf = ssf;
        this.csf = csf;
        this.rmiPort = port;
    }
    public RMIServerSocketFactory getSsf() {
        return ssf;
    }

    public RMIClientSocketFactory getCsf() {
        return csf;
    }

    public int getRmiPort(){
        return rmiPort;
    }

    public DapiEventManager getDapiEventsManager() {
        return dapiEventManager;
    }

    public ApplicationController getApplicationController() {
        return applicationController;
    }

    public void setApplicationController(ApplicationController applicationController) {
        this.applicationController = applicationController;
    }

    public void startDapiEventListener() {
        dapiEventManager = new DapiEventManager( eventsManager);
        dapiEventManager.startEventListener();
    }

    public void stopDapiEventListener() {
        if (dapiEventManager != null)
            dapiEventManager.stopEventListener();
    }


    public String login(String userName, String password) throws RemoteException, ServiceException {
        return login(userName, password, "unknown");
    }


    public String login(String userName, String password, String agent) throws RemoteException, ServiceException {
        String handleId = null;
        try {
            handleId = securityManager.login(userName, password);
            System.out.println("User " + userName + " logged in successfully");
            String clientIP = "UNKNOWN";
            try {
                clientIP = RemoteServer.getClientHost();
            } catch (ServerNotActiveException e) {
                //ignore.we will never get it.we will allways be in a remote call.
            }
            securityManager.addConnectionHandle(handleId, new ConnectionHandle(handleId, userName, agent, clientIP));
        } catch (LoginException e) {
            throw new ServiceException(e.getMessage());
        }

        if (!handlerMap.containsKey(handleId)) {
            InstanceHandler instanceHandler = new InstanceHandler(this, handleId, agent);
            handlerMap.put(handleId, instanceHandler);
        }
        return handleId;
    }


    public IEventProcessManager getEventProcessManager(String handleID) throws RemoteException, ServiceException {
        return handlerMap.get(handleID).getEventProcessManager();
    }


    public IServiceManager getServiceManager(String handleID) throws RemoteException, ServiceException {
        return null;
    }


    public IFPSManager getFPSManager(String handleID) throws RemoteException, ServiceException {
        return null;
    }


    public IServiceProviderManager getServiceProviderManager(String handleID) throws RemoteException, ServiceException {
        return null;
    }


    public IDebugger getBreakpointManager(String handleID) throws RemoteException, ServiceException {
        return null;
    }


    public IConfigurationManager getConfigurationManager(String handleID) throws RemoteException, ServiceException {
        return null;
    }


    public IUserSecurityManager getUserSecurityManager(String handleID) throws RemoteException, ServiceException {
        return null;
    }


    public IKeyStoreManager getKeyStoreManager(String handleID) throws RemoteException, ServiceException {
        return null;
    }


    public ISchemaReferenceManager getSchemaReferenceManager(String handleID) throws RemoteException, ServiceException {
        return null;
    }


    public IDocTrackManager getDocTrackManager(String handleID) throws RemoteException, ServiceException {
        return null;
    }


    public void logout(String handleId) throws RemoteException, ServiceException {
        if(handleId==null){
            throw new ServiceException("handle ID should not be null");
        }
        InstanceHandler instanceHandler = handlerMap.get(handleId);
        if(instanceHandler != null)
            instanceHandler.removeHandler();
        handlerMap.remove(handleId);
        ConnectionHandle connectionHandle = securityManager.removeConnectionHandle(handleId);
        System.out.println("User "+connectionHandle.getUserName() + " logged out successfullly");
    }


    public void unRegisterOldListeners(String handleId) throws RemoteException, ServiceException {

    }

    public ConnectionHandle getConnectionHandle(String handleId) {
        return securityManager.getConnectionHandle(handleId);
    }
}
