package com.fiorano.openesb.rmiconnector.impl;

import com.fiorano.openesb.application.application.Application;
import com.fiorano.openesb.applicationcontroller.ApplicationController;
import com.fiorano.openesb.rmiconnector.api.*;
import com.fiorano.openesb.rmiconnector.api.ServiceException;
import com.fiorano.openesb.rmiconnector.api.ServiceReference;
import org.osgi.framework.*;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
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
    ApplicationController applicationController;
    private RMIServerSocketFactory ssf;
    private RMIClientSocketFactory csf;
    private int rmiPort;

    public RmiManager(BundleContext context, int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        org.osgi.framework.ServiceReference[] references = new org.osgi.framework.ServiceReference[0];
        try {
            references = context.getServiceReferences(ApplicationController.class.getName(),null);
             applicationController = (ApplicationController) context.getService(references[0]);
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

    public ApplicationController getApplicationController() {
        return applicationController;
    }

    public void setApplicationController(ApplicationController applicationController) {
        this.applicationController = applicationController;
    }

    @Override
    public String login(String userName, String password) throws RemoteException, ServiceException {
        return login(userName, password, "unknown");
    }

    @Override
    public String login(String userName, String password, String agent) throws RemoteException, ServiceException {
        //use jaas
        /*try {
            LoginContext context = new LoginContext("karaf",new Subject());
        } catch (LoginException e) {
            e.printStackTrace();
        }*/
        String clientIP = "UNKNOWN";
        try {
            clientIP = RemoteServer.getClientHost();
        } catch (ServerNotActiveException e) {
            //ignore.we will never get it.we will allways be in a remote call.
        }
        /*LoginResponse login = connectionHandleManager.login(userName, password, agent, clientIP, false);
        String handleId = login.getHandleID();
        storeClientIpAddress(handleId, clientIP);*/

        String handleId = "1234";
        if (!handlerMap.containsKey(handleId)) {
            InstanceHandler instanceHandler = new InstanceHandler(this, handleId, agent);
            handlerMap.put(handleId, instanceHandler);
        }

        return handleId;
    }

    @Override
    public IEventProcessManager getEventProcessManager(String handleID) throws RemoteException, ServiceException {
        return handlerMap.get(handleID).getEventProcessManager();
    }

    @Override
    public IServiceManager getServiceManager(String handleID) throws RemoteException, ServiceException {
        return null;
    }

    @Override
    public IFPSManager getFPSManager(String handleID) throws RemoteException, ServiceException {
        return null;
    }

    @Override
    public IServiceProviderManager getServiceProviderManager(String handleID) throws RemoteException, ServiceException {
        return null;
    }

    @Override
    public IDebugger getBreakpointManager(String handleID) throws RemoteException, ServiceException {
        return null;
    }

    @Override
    public IConfigurationManager getConfigurationManager(String handleID) throws RemoteException, ServiceException {
        return null;
    }

    @Override
    public IUserSecurityManager getUserSecurityManager(String handleID) throws RemoteException, ServiceException {
        return null;
    }

    @Override
    public IKeyStoreManager getKeyStoreManager(String handleID) throws RemoteException, ServiceException {
        return null;
    }

    @Override
    public ISchemaReferenceManager getSchemaReferenceManager(String handleID) throws RemoteException, ServiceException {
        return null;
    }

    @Override
    public IDocTrackManager getDocTrackManager(String handleID) throws RemoteException, ServiceException {
        return null;
    }

    @Override
    public void logout(String handleId) throws RemoteException, ServiceException {

    }

    @Override
    public void unRegisterOldListeners(String handleId) throws RemoteException, ServiceException {

    }
}
