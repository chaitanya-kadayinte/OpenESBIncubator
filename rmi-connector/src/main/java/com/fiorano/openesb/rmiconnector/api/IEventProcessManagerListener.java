/**
 * Copyright (c) 1999-2007, Fiorano Software Technologies Pvt. Ltd. and affiliates.
 * Copyright (c) 2008-2015, Fiorano Software Pte. Ltd. and affiliates.
 *
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Fiorano Software ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * enclosed with this product or entered into with Fiorano.
 */

package com.fiorano.openesb.rmiconnector.api;

import com.fiorano.openesb.rmiconnector.impl.EventProcessManager;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This is a callback listener interface which a client has to implement. An implementing class either has to
 * extend UniCastRemoteObject or call {@link  java.rmi.server.UnicastRemoteObject#exportObject(Remote)}.
 * When an implementing class has registered to receive such events, it will be notified of Event Process events
 * such as launch, kill, etc. Registration for these events can be done via
 * {@link EventProcessManager#addEventProcessListener(IEventProcessManagerListener, String, float)}
 *
 * @author FSTPL
 * @version 10
 */
public interface IEventProcessManagerListener extends Remote {

    /**
     * This method sends a notification to the client when an Event Process is deleted from the server
     *
     * @param appVersion The version of the Event Process deleted
     * @throws RemoteException A communication-related exception that may occur during the execution of a remote method call
     * @deprecated Use {@link IRepositoryEventListener} to register for repository Updation events : eventProcessDeleted, eventProcessDeployed.
     */
    public void eventProcessDeleted(float appVersion) throws RemoteException;

    /**
     * This method sends a notification to the client when an Event Process is deployed(saved) in the server (i.e. any changes to the event process done and hence saved).
     *
     * @param appVersion The version of the Event Process deployed
     * @throws RemoteException A communication-related exception that may occur during the execution of a remote method call
     * @deprecated Use {@link IRepositoryEventListener} to register for repository Update events : eventProcessDeleted, eventProcessDeployed.
     */
    public void eventProcessDeployed(float appVersion) throws RemoteException;

    /**
     * This method sends a notification to the client when a Service Instance is launched in the server
     *
     * @param serviceInstanceName The name of the Service Instance started
     * @param serviceVersion      The version of the Service Instance started
     * @param fpsName             peer server name on which the service instance has started
     * @throws RemoteException A communication-related exception that may occur during the execution of a remote method call
     */
    public void serviceInstanceStarted(String serviceInstanceName, float serviceVersion, String fpsName) throws RemoteException;

    /**
     * This method sends a notification to the client when a Service Instance is in Starting state.
     *
     * @param serviceInstanceName The name of the Service Instance started
     * @param serviceVersion      The version of the Service Instance started
     * @param fpsName             peer server name on which the service instance has started
     * @throws RemoteException A communication-related exception that may occur during the execution of a remote method call
     */
    public void serviceInstanceStarting(String serviceInstanceName, float serviceVersion, String fpsName) throws RemoteException;

    /**
     * This method sends a notification to the client when a Service Instance is stopped in the server
     *
     * @param serviceInstanceName The name of the Service Instance stopped
     * @param serviceVersion      The version of the Service Instance stopped
     * @param fpsName             peer server name on which the service instance is stopped
     * @throws RemoteException A communication-related exception that may occur during the execution of a remote method call
     */
    public void serviceInstanceStopped(String serviceInstanceName, float serviceVersion, String fpsName) throws RemoteException;

    /**
     * This method sends a notification to the client when a breakpoint is added to a route
     *
     * @param routeGUID route GUID
     * @throws RemoteException A communication-related exception that may occur during the execution of a remote method call
     */
    public void routeBreakPointAdded(String routeGUID) throws RemoteException;

    /**
     * This method sends a notification to the client when a breakpoint is removed from a route
     *
     * @param routeGUID route GUID
     * @throws RemoteException A communication-related exception that may occur during the execution of a remote method call
     */
    public void routeBreakPointRemoved(String routeGUID) throws RemoteException;

    /**
     * This method sends a notification to the client when an Event Process has started up successfully in the server
     *
     * @param appVersion The version of the Event Process launched
     * @throws RemoteException A communication-related exception that may occur during the execution of a remote method call
     */
    public void eventProcessStarted(float appVersion) throws RemoteException;

    /**
     * This method sends a notification to the client when an Event Process is starting up in the server.
     *
     * @param appVersion The version of the Event Process launched
     * @throws RemoteException A communication-related exception that may occur during the execution of a remote method call
     */
    public void eventProcessStarting(float appVersion) throws RemoteException;

    /**
     * This method sends a notification to the client when an Event Process is stopped in the server
     *
     * @param appVersion The version of the Event Process stopped
     * @throws RemoteException A communication-related exception that may occur during the execution of a remote method call
     */
    public void eventProcessStopped(float appVersion) throws RemoteException;
}
