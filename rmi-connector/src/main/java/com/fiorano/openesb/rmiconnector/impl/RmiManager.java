package com.fiorano.openesb.rmiconnector.impl;

import com.fiorano.openesb.application.ApplicationRepository;
import com.fiorano.openesb.applicationcontroller.ApplicationController;
import com.fiorano.openesb.events.EventsManager;
import com.fiorano.openesb.microservice.repository.MicroServiceRepoManager;
import com.fiorano.openesb.namedconfig.NamedConfigRepository;
import com.fiorano.openesb.security.ConnectionHandle;
import com.fiorano.openesb.security.SecurityManager;
import com.fiorano.openesb.rmiconnector.api.*;
import com.fiorano.openesb.rmiconnector.api.ServiceException;
import org.osgi.framework.*;

import javax.security.auth.login.LoginException;
import java.rmi.RemoteException;
import java.rmi.server.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Janardhan on 1/22/2016.
 */
public class RmiManager implements IRmiManager{

    Map<String, InstanceHandler> handlerMap = new HashMap<String, InstanceHandler>();
    private ApplicationController applicationController;
    private ApplicationRepository applicationRepository;
    private EventsManager eventsManager;
    private SecurityManager securityManager;
    private MicroServiceRepoManager microServiceRepoManager;
    private NamedConfigRepository namedConfigRepository;
    private RMIServerSocketFactory ssf;
    private RMIClientSocketFactory csf;
    private int rmiPort;
    private DapiEventManager dapiEventManager;

    public RmiManager(BundleContext context, int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        org.osgi.framework.ServiceReference[] references = new org.osgi.framework.ServiceReference[0];
        try {
            references = context.getServiceReferences(ApplicationController.class.getName(),null);
            applicationController = (ApplicationController) context.getService(references[0]);
            references = context.getServiceReferences(ApplicationRepository.class.getName(),null);
            applicationRepository = (ApplicationRepository) context.getService(references[0]);
            references = context.getServiceReferences(EventsManager.class.getName(), null);
            eventsManager = (EventsManager) context.getService(references[0]);
            references = context.getServiceReferences(SecurityManager.class.getName(), null);
            securityManager = (SecurityManager) context.getService(references[0]);
            references = context.getServiceReferences(MicroServiceRepoManager.class.getName(), null);
            microServiceRepoManager = (MicroServiceRepoManager) context.getService(references[0]);
            references = context.getServiceReferences(NamedConfigRepository.class.getName(), null);
            namedConfigRepository = (NamedConfigRepository) context.getService(references[0]);

            dapiEventManager = new DapiEventManager(eventsManager, namedConfigRepository);
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

    public ApplicationRepository getApplicationRepository() {
        return applicationRepository;
    }

    public NamedConfigRepository getNamedConfigRepository(){
        return namedConfigRepository;
    }

    public EventsManager getEventsManager() {
        return eventsManager;
    }

    public void setEventsManager(EventsManager eventsManager) {
        this.eventsManager = eventsManager;
    }

    public SecurityManager getSecurityManager() {
        return securityManager;
    }

    public void setSecurityManager(SecurityManager securityManager) {
        this.securityManager = securityManager;
    }

    public MicroServiceRepoManager getMicroServiceRepoManager() {
        return microServiceRepoManager;
    }

    public void setMicroServiceRepoManager(MicroServiceRepoManager microServiceRepoManager) {
        this.microServiceRepoManager = microServiceRepoManager;
    }

    public void startDapiEventListener() {
        dapiEventManager = new DapiEventManager( eventsManager, namedConfigRepository);
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

    public IApplicationManager getApplicationManager(String handleID) throws RemoteException, ServiceException {
        return handlerMap.get(handleID).getApplicationManager();
    }

    public IServiceManager getServiceManager(String handleID) throws RemoteException, ServiceException {
        return handlerMap.get(handleID).getMicroServiceManager();
    }

    public IFPSManager getFPSManager(String handleID) throws RemoteException, ServiceException {
        return null;
    }


    public IServiceProviderManager getServiceProviderManager(String handleID) throws RemoteException, ServiceException {
        return null;
    }


    public IDebugger getBreakpointManager(String handleID) throws RemoteException, ServiceException {
        return handlerMap.get(handleID).getBreakpointManager();
    }


    public IConfigurationManager getConfigurationManager(String handleID) throws RemoteException, ServiceException {
        return handlerMap.get(handleID).getConfigurationManager();
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
