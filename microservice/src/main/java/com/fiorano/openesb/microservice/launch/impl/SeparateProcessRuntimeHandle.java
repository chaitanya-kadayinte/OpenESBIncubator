package com.fiorano.openesb.microservice.launch.impl;

import com.fiorano.openesb.application.aps.ServiceInstanceStateDetails;
import com.fiorano.openesb.events.Event;
import com.fiorano.openesb.events.EventIds;
import com.fiorano.openesb.events.EventsManager;
import com.fiorano.openesb.events.MicroServiceEvent;
import com.fiorano.openesb.microservice.ccp.ComponentWorkflowListener;
import com.fiorano.openesb.microservice.ccp.event.CCPEventType;
import com.fiorano.openesb.microservice.ccp.event.ComponentCCPEvent;
import com.fiorano.openesb.microservice.ccp.event.component.StatusEvent;
import com.fiorano.openesb.microservice.launch.LaunchConfiguration;
import com.fiorano.openesb.microservice.launch.MicroServiceRuntimeHandle;
import com.fiorano.openesb.utils.ExceptionUtil;
import com.fiorano.openesb.utils.I18N;
import com.fiorano.openesb.utils.I18NUtil;
import com.fiorano.openesb.utils.LookUpUtil;
import com.fiorano.openesb.utils.exception.FioranoException;
import com.fiorano.openesb.utils.logging.api.FioranoClientLogger;
import com.fiorano.openesb.utils.logging.api.IFioranoLogger;

import java.util.Map;

public class SeparateProcessRuntimeHandle implements MicroServiceRuntimeHandle {

    private Process osProcess;
    private LaunchConfiguration launchConfiguration;
    private CCPCommandHelper ccpCommandHelper;
    private ComponentLifeCycleWorkflow lifeCycleWorkflow;
    private IFioranoLogger coreLogger;
    private boolean shutdownOfCCPComponentInProgress;
    private EventsManager eventManager = new EventsManager();
    private int numberOfForceShutdownAttempts;
    private volatile boolean isKilling;
    private final Object killSyncObject = new Object();
    private String appVersion;
    private String serviceInstName;
    private volatile boolean bServiceDestroyed;
    private long retryIntervalBetweenForceShutdownAttempts;
    private long componentStopWaitTime;
    private long ccpRequestTimeout;

    private ServiceInstanceStateDetails servStateDetails = new ServiceInstanceStateDetails();


    public SeparateProcessRuntimeHandle(Process osProcess, LaunchConfiguration launchConfiguration, CCPCommandHelper ccpCommandHelper) throws FioranoException {
        this.osProcess = osProcess;
        this.launchConfiguration = launchConfiguration;
        
        this.ccpCommandHelper = ccpCommandHelper;
        coreLogger = new FioranoClientLogger().getLogger("service.launch");
    }

    public boolean isRunning() {
        return osProcess.isAlive();
    }

    public void stop() throws Exception {
        killComponent(true, false, "General");
    }

    public void kill() {
        osProcess.destroyForcibly();
    }

    @Override
    public void setLogLevel(Map<String, String> modules) throws Exception {
        ccpCommandHelper.setLogLevel(modules);
    }

    /**
     * Kills this component
     * should only be called from EventProcessHandle.
     *
     * @param isComponentConnOpen whether to clean component connections or not
     * @param reason              Reason for which component is being killed
     * @throws FioranoException If an exception occurs
     */
    public void killComponent(boolean isComponentConnOpen, boolean userAction, String reason) throws Exception {
        if (isComponentConnOpen) {
            ccpCommandHelper.stopComponent(componentStopWaitTime);
            waitForCCPComponentDeath(true, userAction, reason);
        } else {
            cleanupComponentResources(userAction, true, reason);
        }
    }

    public void waitForCCPComponentDeath(boolean isComponentConnectionOpen, boolean userAction, String reason) throws FioranoException {
        synchronized (this) {
            if (shutdownOfCCPComponentInProgress)
                return;
            shutdownOfCCPComponentInProgress = true;
        }

        try {
            if (confirmProcessExit()) {
                if (coreLogger != null)
                    coreLogger.info(Bundle.class, Bundle.COMPONENT_STOPPED, serviceInstName, launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion);

                if (lifeCycleWorkflow != null && getLaunchMode() != LaunchConfiguration.LaunchMode.IN_MEMORY) {
                    ccpCommandHelper.unregisterListener(lifeCycleWorkflow, CCPEventType.STATUS);
                    lifeCycleWorkflow = null;
                }
                generateServiceUnboundEvent(reason, !userAction && reason != null && !(reason.equalsIgnoreCase(CoreConstants.APPLICATION_CLOSED_CONNECTION)));
                if (coreLogger != null)
                    coreLogger.debug(Bundle.class, Bundle.COMPONENT_RESOURCE_CLEANUP, serviceInstName, launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion);
                cleanupComponentResources(userAction, false, reason);
            } else {
                coreLogger.info(Bundle.class, Bundle.COMPONENT_STOP_WAIT, serviceInstName, launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion);

                Thread stopThread = null;
                try {
                    stopThread = new Thread(new Runnable() {
                        public void run() {
                            try {
                                waitFor();
                            } catch (InterruptedException e) {
                                //Ignore this exception. After configured stop wait interval, this thread is intentionally interrupted for the purpose of cleanup.
                            } finally {
                                //On certain platforms, windows for example, interrupted status of the thread is not cleared when interrupted exception is thrown.
                                //We should clear this ourselves. Courtesy : http://kylecartmell.com/?p=9
                                //Though, even if we don't clear interrupted status, it won't effect our logic as we are not calling
                                //any other method on process object from this thread. Without this, the problems will occur only in cases where some method
                                //e.g. waitFor() or exitValue() is called on a process from a thread which has already been interrupted.
                                Thread.interrupted();
                            }
                        }
                    }, "Process wait thread for CCP enabled component " + serviceInstName + " in Event Process " + launchConfiguration.getApplicationName() + "version" + appVersion);   //NOI18N

                    stopThread.setDaemon(true);
                    stopThread.start();
                    stopThread.join(componentStopWaitTime);
                } catch (InterruptedException e) {
                    coreLogger.error(Bundle.class, Bundle.COMPONENT_STOP_WAIT_THREAD_INTERRUPTED, serviceInstName, launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion);

                    if (coreLogger.isDebugEnabled())
                        coreLogger.error(Bundle.class, Bundle.COMPONENT_STOP_WAIT_THREAD_INTERRUPTED, serviceInstName, launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion, e);
                }

                coreLogger.info(Bundle.class, Bundle.COMPONENT_STOP_WAIT_OVER, serviceInstName, launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion);
                if (!confirmProcessExit()) {
                    //Interrupt the thread now as configured wait interval is over.
                    //This will wakeup the thread if it's waiting on process object to terminate.
                    stopThread.interrupt();

                    coreLogger.info(Bundle.class, Bundle.START_FORCE_SHUTDOWN, serviceInstName, launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion);
                    if (lifeCycleWorkflow != null && getLaunchMode() != LaunchConfiguration.LaunchMode.MANUAL) {
                        ccpCommandHelper.unregisterListener(lifeCycleWorkflow, CCPEventType.STATUS);
                        lifeCycleWorkflow = null;
                    }
                    cleanupComponentResources(userAction, true, reason);
                } else {
                    coreLogger.info(Bundle.class, Bundle.COMPONENT_STOPPED, serviceInstName, launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion);

                    if (lifeCycleWorkflow != null && getLaunchMode() != LaunchConfiguration.LaunchMode.MANUAL) {
                        ccpCommandHelper.unregisterListener(lifeCycleWorkflow, CCPEventType.STATUS);
                        lifeCycleWorkflow = null;
                    }
                    generateServiceUnboundEvent(reason, !userAction && reason != null && !(reason.equalsIgnoreCase(CoreConstants.APPLICATION_CLOSED_CONNECTION)));
                    coreLogger.debug(Bundle.class, Bundle.COMPONENT_RESOURCE_CLEANUP, serviceInstName, launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion);
                    cleanupComponentResources(userAction, false, reason);
                }
            }
        } finally {
            synchronized (this) {
                shutdownOfCCPComponentInProgress = false;
            }
        }
    }

    private boolean confirmProcessExit() {
        try {
            osProcess.exitValue();
            return false;
        } catch (IllegalThreadStateException e) {
            return true;
        }
    }

    protected void waitFor() throws InterruptedException {
        osProcess.waitFor();
    }

    private void cleanupComponentResources(boolean userAction, boolean shutdownComponentProcess, String reason) throws FioranoException {
        synchronized (killSyncObject) {
            if (!isKilling)
                isKilling = true;
            else {
                coreLogger.debug(Bundle.class, Bundle.COMPONENT_KILL_INPROGRESS, launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion, serviceInstName);
                return;
            }
        }
        try {
            destroyComponent(shutdownComponentProcess, userAction, reason);
        } catch (FioranoException ex) {
            try {
                generateServiceKillFailedEvent(ExceptionUtil.getMessage(ex));
            } catch (FioranoException e) {
                coreLogger.error(Bundle.class, Bundle.FAILED_TO_GENERATE_STOP_EVENT, serviceInstName, launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion, e);
            }
            throw new FioranoException(Bundle.FAILED_TO_KILL_COMPONENT.toUpperCase(), ex, I18NUtil.getMessage(Bundle.class,
                    Bundle.FAILED_TO_KILL_COMPONENT, serviceInstName, launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion));
        } finally {
            synchronized (killSyncObject) {
                isKilling = false;
            }
            bServiceDestroyed = true;
        }
    }


    private void destroyComponent(boolean shutdownComponentProcess, boolean userAction, String reason) throws FioranoException {
        coreLogger.debug(Bundle.class, Bundle.DESTROY_PROCESS, serviceInstName, launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion);

        if (shutdownComponentProcess) {
            if (numberOfForceShutdownAttempts < 1)
                numberOfForceShutdownAttempts = 1;

            int count = 0;
            for (; count < numberOfForceShutdownAttempts; count++) {
                if (count == 0 || confirmProcessExit()) {
                    coreLogger.info(Bundle.class, Bundle.FORCE_SHUTDOWN_ATTEMPT, serviceInstName, launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion, count + 1);

                    // Bug 19848 - Certain In-Memory components like Sender and other scheduler components which shutdown
                    // themselves after processing should not be shutdown in a separate thread as it leads to a dead-lock like situation (See Stacktrace in the Bug).
                    // The flag cleanConnections can be used to indicate this scenario as component connection is already closed by
                    // the component and thus cleanConnection boolean is set as false. In other cases, where component is shutdown
                    // by user action, cleanConnections boolean is set as true.
                    shutdown(componentStopWaitTime);

                    if (runStop(userAction, reason)) break;
                } else {
                    coreLogger.info(Bundle.class, Bundle.COMPONENT_STOPPED, serviceInstName, launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion);
                    generateServiceUnboundEvent(reason, !userAction && reason != null && !(reason.equalsIgnoreCase(CoreConstants.APPLICATION_CLOSED_CONNECTION)));
                    break;
                }
            }

            if (count == numberOfForceShutdownAttempts) {
                coreLogger.info(Bundle.class, Bundle.COMPONENT_SHUTDOWN_FAILED, serviceInstName, launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion);
            }
        }

        coreLogger.debug(Bundle.class, Bundle.CLOSING_JMX_CONNECTOR, serviceInstName, launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion);

        bServiceDestroyed = true;
        servStateDetails.setRunningVersion(getVersion());
        servStateDetails.setStatusString(EventStateConstants.SERVICE_HANDLE_DESTROYED);
        servStateDetails.setKillTime(System.currentTimeMillis());
    }

    private void shutdown(long componentStopWaitTime) {

    }

    private boolean runStop(boolean userAction, String reason) throws FioranoException {
        Thread stopThread = null;
        try {
            stopThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        waitFor();
                    } catch (InterruptedException e) {
                        //Ignore this exception. After each configured retry interval, this thread is intentionally interrupted for the purpose of cleanup.
                    } finally {
                        //On certain platforms, windows for example, interrupted status of the thread is not cleared when interrupted exception is thrown.
                        //We should clear this ourselves. Courtesy : http://kylecartmell.com/?p=9
                        //Though, even if we don't clear interrupted status, it won't effect our logic as we are not calling
                        //any other method on process object from this thread. Without this, the problems will occur only in cases where some method
                        //e.g. waitFor() or exitValue() is called on a process from a thread which has already been interrupted.
                        Thread.interrupted();
                    }
                }
            }, "Process wait thread for component " + serviceInstName + " in Event Process " + launchConfiguration.getApplicationName() + "version" + appVersion);   //NOI18N

            stopThread.setDaemon(true);
            stopThread.start();
            stopThread.join(retryIntervalBetweenForceShutdownAttempts);
        } catch (InterruptedException e) {
            coreLogger.error(Bundle.class, Bundle.COMPONENT_STOP_WAIT_THREAD_INTERRUPTED, serviceInstName, launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion);

            if (coreLogger.isDebugEnabled())
                coreLogger.error(Bundle.class, Bundle.COMPONENT_STOP_WAIT_THREAD_INTERRUPTED, serviceInstName, launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion, e);
        }

        if (!confirmProcessExit()) {
            //Interrupt the thread now as configured wait interval is over.
            //This will wakeup the thread if it's waiting on process object to terminate.
            stopThread.interrupt();
        } else {
            coreLogger.info(Bundle.class, Bundle.COMPONENT_STOPPED, serviceInstName, launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion);
            generateServiceUnboundEvent(reason, !userAction && reason != null && !(reason.equalsIgnoreCase(CoreConstants.APPLICATION_CLOSED_CONNECTION)));
            return true;
        }
        return false;
    }

    public class ComponentLifeCycleWorkflow extends ComponentWorkflowListener {
        private ComponentLifeCycleWorkflow(String componentInstanceName, String applicationName, String applicationVersion) {
            super(componentInstanceName, applicationName, applicationVersion);
        }

        public void onEvent(ComponentCCPEvent event) {
            try {
                if (event.getComponentId().equalsIgnoreCase(LookUpUtil.getServiceInstanceLookupName(launchConfiguration.getApplicationName(), appVersion, serviceInstName))) {
                    StatusEvent status = (StatusEvent) event.getControlEvent();
                    if (status.getOperationScope() == StatusEvent.OperationScope.COMPONENT_STOP) {
                        if (status.getStatusType() == StatusEvent.StatusType.ERROR) {
                            coreLogger.error(Bundle.class, Bundle.ERROR_STOPPING_COMPONENT, serviceInstName, status.getStatus().toString(), status.getErrorMessage(), launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion);
                            generateServiceKillFailedEvent(status.getErrorMessage());
                        } else if (status.getStatusType() == StatusEvent.StatusType.WARNING)
                            coreLogger.warn(Bundle.class, Bundle.WARN_STOPPING_COMPONENT, serviceInstName, status.getStatus().toString(), status.getErrorMessage(), launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion);
                        else if (status.getStatusType() == StatusEvent.StatusType.INFORMATION) {
                            coreLogger.debug(Bundle.class, Bundle.STOP_COMPONENT_UPDATE, status.getStatus().toString(), serviceInstName, launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion);
                            if (status.getStatus() == StatusEvent.Status.COMPONENT_STOPPED) {
                                coreLogger.info(Bundle.class, Bundle.COMPONENT_STOPPED_EVENT, serviceInstName, launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion);

                                /**
                                 * Unbound event should not be raised at this point. At this point, component has not yet terminated it's JMS connection to Peer Server. We can handle this case by waiting for the process
                                 * object to die using com.fiorano.peer.launch.process.runtime.IProcess.waitFor() API. Thus, unbound event is raised when component process has actually terminated.
                                 */
                                //Un-register workflow listener now for this component.
                                if (lifeCycleWorkflow != null && getLaunchMode() != LaunchConfiguration.LaunchMode.MANUAL) {
                                    ccpCommandHelper.unregisterListener(lifeCycleWorkflow, CCPEventType.STATUS);
                                    lifeCycleWorkflow = null;
                                }
                            } else if (status.getStatus() == StatusEvent.Status.COMPONENT_STOPPING) {
                                coreLogger.info(Bundle.class, Bundle.COMPONENT_STOP_TIME_WAIT, serviceInstName, launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion);
                            } else if (status.getStatus() == StatusEvent.Status.COMPONENT_DISCONNECTING) {
                                coreLogger.info(Bundle.class, Bundle.COMPONENT_DISCONNECTING, serviceInstName, launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion);
                            }
                        }
                    } else if (status.getOperationScope() == StatusEvent.OperationScope.COMPONENT_LAUNCH) {
                        if (status.getStatusType() == StatusEvent.StatusType.ERROR) {
                            coreLogger.error(Bundle.class, Bundle.ERROR_LAUNCH_COMPONENT, serviceInstName, status.getStatus().toString(), status.getErrorMessage(), launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion);
                            generateServiceFailedToLaunchEvent(status.getErrorMessage());
                        } else if (status.getStatusType() == StatusEvent.StatusType.WARNING)
                            coreLogger.warn(Bundle.class, Bundle.WARNING_LAUNCH_COMPONENT, serviceInstName, status.getStatus().toString(), status.getErrorMessage(), launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion);
                        else if (status.getStatusType() == StatusEvent.StatusType.INFORMATION) {
                            coreLogger.debug(Bundle.class, Bundle.LAUNCH_PROCESS_UPDATE, status.getStatus().toString(), serviceInstName, launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion);
                            if (status.getStatus() == StatusEvent.Status.COMPONENT_STARTED) {
                                coreLogger.info(Bundle.class, Bundle.COMPONENT_STARTED, serviceInstName, launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion);
                                generateServiceBoundEvent();
                            } else if (status.getStatus() == StatusEvent.Status.COMPONENT_CONNECTED)
                                coreLogger.info(Bundle.class, Bundle.COMPONENT_CONNECTED, serviceInstName, launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion);
                        }
                    } else if (status.getOperationScope() == StatusEvent.OperationScope.COMPONENT_RUNNING) {
                        if (status.getStatusType() == StatusEvent.StatusType.INFORMATION) {
                            if (status.getStatus() == StatusEvent.Status.COMPONENT_LAUNCHING) {
                                coreLogger.info(Bundle.class, Bundle.COMPONENT_LAUNCHING, serviceInstName, launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion);
                                generateServiceBoundingEvent();
                            } else if (status.getStatusType() == StatusEvent.StatusType.ERROR) {
                                coreLogger.error(Bundle.class, Bundle.ERROR_LAUNCH_COMPONENT, serviceInstName, status.getStatus().toString(), status.getErrorMessage(), launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion);
                                generateServiceFailedToLaunchEvent(status.getErrorMessage());
                            } else if (status.getStatusType() == StatusEvent.StatusType.WARNING)
                                coreLogger.warn(Bundle.class, Bundle.WARNING_LAUNCH_COMPONENT, serviceInstName, status.getStatus().toString(), status.getErrorMessage(), launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion);
                        }
                    }
                }
            } catch (FioranoException e) {
                coreLogger.error(e);
            }
        }
    }

    //todo
    private LaunchConfiguration.LaunchMode getLaunchMode() {
        return LaunchConfiguration.LaunchMode.SEPARATE_PROCESS;
    }

    private void generateServiceBoundEvent() throws FioranoException {
        bServiceDestroyed = false;

        String message = I18N.getMessage(Bundle.class, Bundle.SERVICE_BOUND, serviceInstName, getNodeName());

        servStateDetails.setStatusString(EventStateConstants.SERVICE_HANDLE_BOUND);
        generateMicroServiceEvent(EventIds.SERVICE_HANDLE_BOUND, Event.EventCategory.INFORMATION, EventStateConstants.SERVICE_HANDLE_BOUND, getServiceGUID(), getVersion(), serviceInstName, launchConfiguration.getApplicationName(), appVersion, message, AlertModules.SERVICE_LAUNCH_KILL);

    }

    private void generateServiceKillFailedEvent(String reason) throws FioranoException {
        String status = EventStateConstants.SERVICE_FAILED_TO_KILL;
        int eventID = EventIds.SERVICE_FAILED_TO_KILL;
        Event.EventCategory eventCategory = Event.EventCategory.ERROR;
        String description = I18N.getMessage(Bundle.class, Bundle.SERVICE_KILL_FAILURE, getNodeName());
        servStateDetails.setRunningVersion(getVersion());
        servStateDetails.setStatusString(status);
        generateServiceEvent(eventID, eventCategory, status, getServiceGUID(), getVersion(), serviceInstName, launchConfiguration.getApplicationName(), appVersion, description, AlertModules.SERVICE_LAUNCH_KILL);

    }

    private void generateServiceFailedToLaunchEvent(String reason) throws FioranoException {
        String status = EventStateConstants.SERVICE_FAILED_TO_LAUNCH;
        int eventID = EventIds.SERVICE_FAILED_TO_LAUNCH;
        Event.EventCategory eventCategory = Event.EventCategory.ERROR;
        String description = I18N.getMessage(Bundle.class, Bundle.SERVICE_LAUNCH_FAILURE, getNodeName());
        servStateDetails.setRunningVersion(getVersion());
        servStateDetails.setStatusString(status);
        generateServiceEvent(eventID, eventCategory, status, getServiceGUID(), getVersion(), serviceInstName, launchConfiguration.getApplicationName(), appVersion, description, AlertModules.SERVICE_LAUNCH_KILL);

    }

    private void generateServiceBoundingEvent() throws FioranoException {
        bServiceDestroyed = false;
        String message = I18N.getMessage(Bundle.class, Bundle.SERVICE_BOUNDING, serviceInstName, getNodeName());
        servStateDetails.setStatusString(EventStateConstants.SERVICE_HANDLE_BOUNDING);
        generateMicroServiceEvent(EventIds.SERVICE_HANDLE_BOUNDING, Event.EventCategory.INFORMATION, EventStateConstants.SERVICE_HANDLE_BOUNDING, getServiceGUID(), getVersion(), serviceInstName, launchConfiguration.getApplicationName(), appVersion, message, AlertModules.SERVICE_LAUNCH_KILL);

    }

    private String getNodeName() {
        return "FPS";
    }


    //todo
    private String getServiceGUID() {
        return null;
    }

    private String getVersion() {
        return null;
    }

    public MicroServiceEvent getMicroServiceEvent(int eventID, Event.EventCategory category, String status, String serviceGUID, String serviceVersion, String serviceInstName,
                                                  String appGuid, String appVersion, String description, String moduleName, String serverID) throws FioranoException {
        MicroServiceEvent event = new MicroServiceEvent();
        event.setEventID(eventID);
        event.setEventCategory(category);
        event.setEventScope("OpenESB");
        event.setSource("Peer Server");
        event.setEventModule(moduleName);
        event.setEventGenerationDate(System.currentTimeMillis());
        event.setEventStatus(status);
        event.setServiceGUID(serviceGUID);
        event.setServiceVersion(Float.parseFloat(serviceVersion));
        event.setApplicationGUID(launchConfiguration.getApplicationName());
        event.setApplicationVersion(appVersion);
        event.setServiceInstance(serviceInstName);
        event.setEventDescription(description);
        event.setSourceTPSName("FPS");
        event.setBuildVersionNo(getBuildNo());
        event.setSink(serverID);
        return event;
    }

    /**
     * Generates Service Unbound event
     *
     * @param reason Reason for which component is being killed
     * @throws FioranoException exception
     */
    protected void generateServiceUnboundEvent(String reason, boolean isWarning) throws FioranoException {
        String message;
        if (reason != null && reason.contains("STOPPING_COMPONENT_DUE_TO_PRESENCE_OF_MULTIPLE_INSTANCES_IN_NETWORK")) {
            message = I18N.getMessage(Bundle.class, Bundle.SERVICE_UNBOUND2, serviceInstName, getNodeName(), launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion, reason);
            servStateDetails.setStatusString(EventStateConstants.SERVICE_HANDLE_UNBOUND_SERVER_MANAGEMENT_ACTION);
            generateServiceEvent(EventIds.SERVICE_HANDLE_UNBOUND, isWarning ? Event.EventCategory.WARNING : Event.EventCategory.INFORMATION, EventStateConstants.SERVICE_HANDLE_UNBOUND_SERVER_MANAGEMENT_ACTION,
                    getServiceGUID(), getVersion(), serviceInstName, launchConfiguration.getApplicationName(), appVersion, message, AlertModules.SERVICE_LAUNCH_KILL);
        } else {
            if (isWarning || reason != null)
                message = I18N.getMessage(Bundle.class, Bundle.SERVICE_UNBOUND2, serviceInstName, getNodeName(), launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion, reason);
            else
                message = I18N.getMessage(Bundle.class, Bundle.SERVICE_UNBOUND1, serviceInstName, getNodeName(), launchConfiguration.getApplicationName() + CoreConstants.APP_VERSION_DELIM + appVersion);

            servStateDetails.setStatusString(EventStateConstants.SERVICE_HANDLE_UNBOUND);
            generateServiceEvent(EventIds.SERVICE_HANDLE_UNBOUND, isWarning ? Event.EventCategory.WARNING : Event.EventCategory.INFORMATION, EventStateConstants.SERVICE_HANDLE_UNBOUND,
                    getServiceGUID(), getVersion(), serviceInstName, launchConfiguration.getApplicationName(), appVersion, message, AlertModules.SERVICE_LAUNCH_KILL);
        }
    }

    private void generateServiceEvent(int eventID, Event.EventCategory category, String status, String serviceGUID, String serviceVersion, String serviceInstName,
                                      String appGuid, String appVersion, String description, String moduleName) throws FioranoException {
        MicroServiceEvent event = getMicroServiceEvent(eventID, category, status, serviceGUID, serviceVersion, serviceInstName, launchConfiguration.getApplicationName(), appVersion, description, moduleName, getNodeName());
        eventManager.raiseEvent(event);
    }

    //todo implement in common class
    private static int getBuildNo() {
        return 100;
    }

    private void generateMicroServiceEvent(int eventID, Event.EventCategory category, String status, String serviceGUID, String serviceVersion, String serviceInstName,
                                           String appGuid, String appVersion, String description, String moduleName) throws FioranoException {
        MicroServiceEvent event = getMicroServiceEvent(eventID, category, status, serviceGUID, serviceVersion, serviceInstName, launchConfiguration.getApplicationName(), appVersion, description, moduleName, getNodeName());
        eventManager.raiseEvent(event);
    }

    private class AlertModules {
        public static final String SERVICE_LAUNCH_KILL = "SERVICE_LAUNCH_KILL";
    }

}
