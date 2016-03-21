/*
package com.fiorano.openesb.rmiclient.tests;

import com.fiorano.openesb.rmiconnector.api.IApplicationManagerListener;

import java.rmi.RemoteException;

*/
/**
 * Created by Janardhan on 3/7/2016.
 *//*

public class OnlineEventProcessListener extends AbstractEventListener implements IApplicationManagerListener {

    public OnlineEventProcessListener(EventProcessNode eventProcessNode) {
        super();
        this.eventProcessNode = eventProcessNode;
    }

    public void eventProcessStarted(float version) throws RemoteException {
        eventProcessNode.setRunning(true);
        if (getApplication() == null) {
            return;
        }
        application.setRunning(true);
        if (application.getComponents() != null) {
            EList<ServiceInstanceType> serviceInstances = application.getComponents().getServiceInstances();
            for (ServiceInstanceType serviceInstance : serviceInstances) {
                LaunchType launch = serviceInstance.getExecution().getLaunch();
                if (launch != null && launch.getMode() == LaunchMode.MANUAL) {
                    if (serviceInstance.getState() == State.MANNUAL_LAUNCH_RUNNING) {
                        serviceInstance.setState(State.MANNUAL_LAUNCH_RUNNING);
                    } else {
                        serviceInstance.setState(State.MANUAL_LAUNCH);
                    }
                } else {
                    serviceInstance.setState(State.UNKNOWN);
                }
            }
            EList<RemoteServiceInstanceType> remoteServiceInstances = application.getComponents().getRemoteServiceInstances();
            for (RemoteServiceInstanceType remoteServiceInstance : remoteServiceInstances) {
                // to refresh the remote instance visuals
                remoteServiceInstance.setSyncRequired(remoteServiceInstance.isSyncRequired());
            }
            if (serviceInstances.size() == 0 && remoteServiceInstances.size() != 0)
                application.setFullyLaunched(true);
        }

        // Update all nested SubFlow States....
        // Note: Ignoring State updation for RootFlow
        if (application.getRootFlow() != null) {
            EList<SubFlowType> subFlowTypes = application.getRootFlow().getSubFlows();
            for (SubFlowType subFlowType : subFlowTypes) {
                updateSubFlowState(subFlowType, null, null);
            }
        }

        final String appInfo = ActionHelper.getAppInfo(eventProcessNode);
        Thread addDebuggerThread = new Thread(new Runnable() {
            public void run() {
                addBreakPoints(eventProcessNode);
            }
        });
        addDebuggerThread.start();

        ActionHelper.notifySelectionEvent(eventProcessNode.getGuid(), eventProcessNode.getVersion());
        Logger.logInfoOnConsole(appInfo + Messages_soa_orchestration_online.OnlineEventProcessListener_1 + eventProcessNode.getDeployedServerNode().getName());
    }

    private void addBreakPoints(final EventProcessNode eventProcessNode) {
//		Display.getDefault().asyncExec(new Runnable() {
//
//			public void run() {
        for (RouteType route : eventProcessNode.getEventProcessProject().getApplicationDebugger().getRoutesWithBreakPoints()) {
            try {
                eventProcessNode.getEventProcessProject().getApplicationDebugger().addBreakPoint(route);
            } catch (RemoteException e) {
                route.setHasBreakPoint(false);
                route.setMessageTrapped(false);
                UIHelper.handleException(e, Messages_soa_orchestration_online.OnlineEventProcessListener_4, Messages_soa_orchestration_online.OnlineEventProcessListener_5 + route.getName() + Messages_soa_orchestration_online.OnlineEventProcessListener_6);
            } catch (ServiceException e) {
                route.setHasBreakPoint(false);
                route.setMessageTrapped(false);
                UIHelper.handleException(e, Messages_soa_orchestration_online.OnlineEventProcessListener_7, Messages_soa_orchestration_online.OnlineEventProcessListener_8 + route.getName() + Messages_soa_orchestration_online.OnlineEventProcessListener_9);
            } catch (NamingException e) {
                route.setHasBreakPoint(false);
                route.setMessageTrapped(false);
                UIHelper.handleException(e, Messages_soa_orchestration_online.OnlineEventProcessListener_7, Messages_soa_orchestration_online.OnlineEventProcessListener_8 + route.getName() + Messages_soa_orchestration_online.OnlineEventProcessListener_9);
            } catch (JMSException e) {
                route.setHasBreakPoint(false);
                route.setMessageTrapped(false);
                if(e instanceof InvalidClientIDException) {
                    route.setExternalBreakPoint(true);
                } else {
                    UIHelper.handleException(e, Messages_soa_orchestration_online.OnlineEventProcessListener_7, Messages_soa_orchestration_online.OnlineEventProcessListener_8 + route.getName() + Messages_soa_orchestration_online.OnlineEventProcessListener_9);
                }
            }
        }
//			}
//		});
    }

    public void eventProcessStopped(float version) throws RemoteException {

        if (getApplication() == null) {
            eventProcessNode.setRunning(false);
            return;
        }
        if (application.getComponents() != null) {
            EList<ServiceInstanceType> serviceInstances = application.getComponents().getServiceInstances();
            for (ServiceInstanceType serviceInstance : serviceInstances) {
                LaunchType launch = serviceInstance.getExecution().getLaunch();
                if (launch != null && launch.getMode() == LaunchMode.MANUAL) {
                    serviceInstance.setState(State.UNKNOWN);
                } else {
                    serviceInstance.setState(State.UNKNOWN);
                }
            }
            if (serviceInstances.size() == 0) {
                if (application.getComponents().getRemoteServiceInstances().size() != 0)
                    application.setFullyLaunched(false);
            }
        }

        // Update all nested SubFlow States to STATE.UNKNOWN
        if (application.getRootFlow() != null) {
            EList<SubFlowType> subFlowTypes = application.getRootFlow().getSubFlows();
            for (SubFlowType subFlowType : subFlowTypes) {
                updateSubFlowState(subFlowType, State.UNKNOWN, null);
            }
        }

        application.setRunning(false);
        eventProcessNode.setRunning(false);

        RoutesType routes = application.getRoutes();
        if (application.getComponents() != null) {
            for (Connectable connectable : application.getComponents().getServiceInstances()) {
                connectable.setSyncRequired(false);
            }
            for (Connectable connectable : application.getComponents().getRemoteServiceInstances()) {
                connectable.setSyncRequired(false);
            }
        }
        if (routes != null) {
            for (RouteType route : routes.getRoute()) {
                route.setSyncRequired(false);
            }
        }

        OnlineApplicationProject applicationProject = eventProcessNode.getEventProcessProject();
        final ApplicationDebugger applicationDebugger = applicationProject.getApplicationDebugger();

        if (applicationDebugger.hasBreakPoints()) {
            for (BreakPoint breakPoint : applicationDebugger.getBreakPoints()) {
                applicationDebugger.addStudioBreakPoint(breakPoint.getRoute());
                breakPoint.discardAllMessages();
                breakPoint.dispose();
            }
        }

        List<RouteType> routesWithExtBreakPoints = applicationProject.getApplicationDebugger().getRoutesWithExternalBreakPoints();
        for (RouteType routeType : routesWithExtBreakPoints) {
            if(routeType.isExternalBreakPoint()) {
                routeType.setExternalBreakPoint(false);
            }
        }
        applicationDebugger.clearBreakPointList(); //Bug 21606

        application.setSyncRequired(false);
        ActionHelper.notifySelectionEvent(eventProcessNode.getGuid(), eventProcessNode.getVersion());
        String appInfo = ActionHelper.getAppInfo(eventProcessNode);
        Logger.logInfoOnConsole(appInfo + Messages_soa_orchestration_online.OnlineEventProcessListener_2 + eventProcessNode.getDeployedServerNode().getName());

    }

    public void serviceInstanceStarted(String name, float version, String fpsName) throws RemoteException {
        if (getApplication() == null) {
            EnterpriseServersRepository.notifyTreeChanged(eventProcessNode);
            return;
        }
        if (application.getComponents() != null) {
            ServiceInstanceType serviceInstance = application.getServiceInstance(name);
            // Sometimes if the EP editor is open and if the same EP is modified
            // externally changes may not be reflected and this may be null.
            if (serviceInstance != null) {
                DeploymentType deployment = serviceInstance.getDeployment();
                if (deployment != null) {
                    deployment.setNode(fpsName);
                    serviceInstance.getDeployment().setNodeAvailable(true);
                }
                LaunchType launch = serviceInstance.getExecution().getLaunch();
                if (launch != null && launch.getMode() == LaunchMode.MANUAL) {
                    serviceInstance.setState(State.MANNUAL_LAUNCH_RUNNING);
                } else {
                    application.getServiceInstance(name).setState(State.RUNNING);
                }
                boolean areAllServiceInstRunning = ApplicationExecutionHelper.areAllServiceInstRunning(application);
                application.setFullyLaunched(areAllServiceInstRunning);
                ActionHelper.notifySelectionEvent(eventProcessNode.getGuid(), eventProcessNode.getVersion(), name);
                EnterpriseServersRepository.notifyTreeChanged(eventProcessNode);
            }
        }

        if (application.getRootFlow() != null) {
            EList<SubFlowType> subFlowTypes = application.getRootFlow().getSubFlows();
            for (SubFlowType subFlowType : subFlowTypes) {
                if (subFlowType.getServiceInstance(name) != null) {
                    updateSubFlowState(subFlowType, null, name);
                }
            }
        }
    }

    public void serviceInstanceStopped(String name, float version, String fpsName) throws RemoteException {
        if (getApplication() == null) {
            EnterpriseServersRepository.notifyTreeChanged(eventProcessNode);
            return;
        }
        if (application.getComponents() != null) {
            ServiceInstanceType serviceInstance = application.getServiceInstance(name);
            if (serviceInstance != null) {
                LaunchType launch = serviceInstance.getExecution().getLaunch();
                if (launch != null && launch.getMode() == LaunchMode.MANUAL && !application.isRunning()) {
                    serviceInstance.setState(State.UNKNOWN);
                } else {
                    serviceInstance.setState(State.STOPPED);
                }
                boolean areAllServiceInstRunning = ApplicationExecutionHelper.areAllServiceInstRunning(application);
                application.setFullyLaunched(areAllServiceInstRunning);
                ActionHelper.notifySelectionEvent(eventProcessNode.getGuid(), eventProcessNode.getVersion(), name);
                EnterpriseServersRepository.notifyTreeChanged(eventProcessNode);
            }
        }

        if (application.getRootFlow() != null) {
            EList<SubFlowType> subFlowTypes = application.getRootFlow().getSubFlows();
            for (SubFlowType subFlowType : subFlowTypes) {
                if (subFlowType.getServiceInstance(name) != null) {
                    updateSubFlowState(subFlowType, null, name);
                }
            }
        }
    }

    @Override
    public void serviceInstanceStarting(String name, float version, String fpsName) throws RemoteException {
        if (getApplication() == null) {
            EnterpriseServersRepository.notifyTreeChanged(eventProcessNode);
            return;
        }
        if (application.getComponents() != null) {
            ServiceInstanceType serviceInstance = application.getServiceInstance(name);
            if (serviceInstance != null) {
                serviceInstance.setState(State.STARTING);
            }
        }
    }

    // Adding debugger Listeners Implementation for Bug # 15254
    public void routeBreakPointAdded(String routeName) throws RemoteException {
        ApplicationType application = getApplication();
        if (application == null || application.getRoutes() == null) {
            return;
        }
        final RouteType routeType = application.getRoute(routeName);
        if (routeType != null) {
            if(!routeType.isHasBreakPoint())
                routeType.setExternalBreakPoint(true);
            routeType.setMessageTrapped(false);
        }
    }

    // Adding debugger Listeners Implementation for Bug # 15254
    public void routeBreakPointRemoved(String routeName) throws RemoteException {
        ApplicationType application = getApplication();
        if (application == null || application.getRoutes() == null) {
            return;
        }
        final RouteType routeType = application.getRoute(routeName);
        if (routeType != null) {
            if(routeType.isExternalBreakPoint()) {
                routeType.setExternalBreakPoint(false);
            }
            routeType.setMessageTrapped(false);
            routeType.setHasBreakPoint(false);

            OnlineApplicationProject applicationProject = eventProcessNode.getEventProcessProject();
            final ApplicationDebugger applicationDebugger = applicationProject.getApplicationDebugger();
            applicationDebugger.breakPointRemoved(routeType);
        }
    }

    private ApplicationType getApplication() {
        //get application only if it is fetched already.
        if (eventProcessNode.isProjectFetched()) {
            application = eventProcessNode.getApplication();
        }
        return application;
    }

    public void eventProcessDeleted(float appVersion) throws RemoteException {
    }

    public void eventProcessDeployed(float appVersion) throws RemoteException {
        String message = Messages_soa_orchestration_online.OnlineEventProcessListener_3 + ApplicationKey.getKey(eventProcessNode.getGuid(), appVersion);
        Logger.logInfoOnConsole(message);
    }

    public void eventProcessStarting(float appVersion) throws RemoteException {
        // presently not used
    }

    private void updateSubFlowState(SubFlowType subFlowType, State state, String serviceInstanceName) {
        EList<SubFlowType> nestedFlows = subFlowType.getSubFlows();
        for (SubFlowType nestedSubFlow : nestedFlows) {
            updateSubFlowState(nestedSubFlow, state, serviceInstanceName);
        }

        if (serviceInstanceName != null) {
            if (subFlowType.getServiceInstance(serviceInstanceName) != null) {
                calculateAndUpdateSubFlowState(subFlowType);
            }
        } else {
            if (state == null) {
                calculateAndUpdateSubFlowState(subFlowType);
            } else {
                subFlowType.setState(state);
            }
        }
    }

    private void calculateAndUpdateSubFlowState(SubFlowType subFlowType) {
        State calculatedState = !ActionHelper.caliculateSubFlowEnablement(true, subFlowType) ? State.RUNNING : State.STOPPED;
        subFlowType.setState(calculatedState);
    }

}

*/
