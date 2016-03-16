package com.fiorano.openesb.rmiconnector.impl;

import com.fiorano.openesb.applicationcontroller.ApplicationController;
import com.fiorano.openesb.applicationcontroller.ApplicationHandle;
import com.fiorano.openesb.rmiconnector.api.BreakpointMetaData;
import com.fiorano.openesb.rmiconnector.api.IDebugger;
import com.fiorano.openesb.rmiconnector.api.ServiceException;

import java.rmi.RemoteException;

public class BreakPointManager extends AbstractRmiManager implements IDebugger {

    private ApplicationController applicationController;


    protected BreakPointManager(RmiManager rmiManager,ApplicationController applicationController) {
        super(rmiManager);
        this.applicationController = applicationController;
    }

    @Override
    public BreakpointMetaData addBreakpoint(String appGUID, float appVersion, String routeName, String handleID) throws RemoteException, ServiceException {
        ApplicationHandle appHandle = applicationController.getApplicationHandle(appGUID, appVersion, handleID);
        if(appHandle==null){
            throw new ServiceException("application not running");
        }
        try {
            appHandle.addBreakPoint(routeName);
            BreakpointMetaData metaData = new BreakpointMetaData();
            /*metaData.setSourceQName();
            metaData.setTargetQName();
            metaData.setConnectionProperties();*/
            return metaData;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public BreakpointMetaData getBreakpointMetaData(String appGUID, float appVersion, String routeName) throws RemoteException, ServiceException {
        return null;
    }

    @Override
    public String[] getRoutesWithDebugger(String appGUID, float appVersion) throws RemoteException, ServiceException {
        return new String[0];
    }

    @Override
    public void removeBreakpoint(String appGUID, float appVersion, String routeName) throws RemoteException, ServiceException {

    }

    @Override
    public void removeAllBreakpoints(String appGUID, float appVersion) throws RemoteException, ServiceException {

    }

    @Override
    public void messageModifiedonDebugger(String appGUID, float version, String routeGUID, String messageID) throws RemoteException, ServiceException {

    }

    @Override
    public void messageDeletedonDebugger(String appGUID, float version, String routeGUID, String messageID) throws RemoteException, ServiceException {

    }

    @Override
    public void pauseRoute(String appGUID, float version, String routeGUID, String handleId, boolean maintainSequence, long pauseTime) throws RemoteException, ServiceException {

    }
}
