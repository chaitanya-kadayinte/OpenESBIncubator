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

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This is a callback listener interface which a client has to implement. An implementing class either has to
 * extend UniCastRemoteObject or call {@link  java.rmi.server.UnicastRemoteObject#exportObject(java.rmi.Remote)}.
 * When an implementing class has registered to receive such events, it will be notified of Service events
 * such as service instance deleted and deployed etc. Registration for these events can be done via
 * <p>
 * {@link IServiceManager#addServiceRepositoryEventListener(IRepositoryEventListener)} For Service Repository Update events
 * </p><p>
 * {@link IEventProcessManager#addRepositoryEventListener(IRepositoryEventListener)} For Event Process Repository Update Events.
 * </p>
 *
 * @author FSTPL
 * @version 10
 *
 */
public interface IRepositoryEventListener extends Remote {
    /**
     * This notification is sent to the client when a Service Instance is deleted from the server
     *
     * @param serviceGUID    The name of the Service Instance deleted
     * @param serviceVersion The version of the Service Instance deleted
     * @throws java.rmi.RemoteException A communication-related exception that may occur during the execution of a remote method call
     */
    public void serviceDeleted(String serviceGUID, float serviceVersion) throws RemoteException;

    /**
     * This notification is sent to the client when a Service Instance is deployed in the server
     *
     * @param serviceGUID    The name of the Service Instance deployed
     * @param serviceVersion The version of the Service Instance deployed
     * @throws RemoteException A communication-related exception that may occur during the execution of a remote method call
     */
    public void serviceDeployed(String serviceGUID, float serviceVersion) throws RemoteException;

    /**
     * This notification is sent to the client when an Event Process is deleted from the server
     *
     * @param appGUID    The Application GUID of the Event Process
     * @param appVersion The version of the Event Process deleted
     * @param handleId   Id of the deleting Client
     * @throws RemoteException A communication-related exception that may occur during the execution of a remote method call     *
     */
    public void eventProcessDeleted(String appGUID, float appVersion, String handleId) throws RemoteException;

    /**
     * This notification is sent to the client when an Event Process is deployed(saved) in the server (i.e. any changes to the event process done and hence saved).
     *
     * @param appGUID    The Application GUID of the Event Process
     * @param appVersion The version of the Event Process deployed
     * @param handleId   Id of the deploying Client
     * @throws RemoteException A communication-related exception that may occur during the execution of a remote method call     *
     */
    public void eventProcessDeployed(String appGUID, float appVersion, String handleId) throws RemoteException;
     /**
     * This notification is sent to the client when a resource of a service is deployed(saved) to the server.
     * @param serviceGUID The name of the Service Instance
     * @param resourceName The name of resource deployed
     * @param serviceVersion The version of the service whose resource is created
     * @throws RemoteException A communication-related exception that may occur during the execution of a remote method call
     */
    public void resourceDeployed(String resourceName,String serviceGUID, float serviceVersion) throws RemoteException;
    /**
     * This notification is sent to the client when a resource of a service is removed from the server.
     * @param serviceGUID The name of the Service Instance
     * @param resourceName The name of resource deleted
     * @param serviceVersion The version of the service whose resource is created
     * @throws RemoteException A communication-related exception that may occur during the execution of a remote method call
     */
    public void resourceDeleted(String resourceName,String serviceGUID, float serviceVersion) throws RemoteException;

    /**
     * This notification is sent to the client when the serviceDescriptor.xml of a Service is modified in the server
     *
     * @param serviceGUID    The name of the Service Instance whose descriptor is modified
     * @param serviceVersion The version of the Service whose descriptor is modified
     * @throws RemoteException A communication-related exception that may occur during the execution of a remote method call
     */
    public void descriptorModified(String serviceGUID, float serviceVersion) throws RemoteException;
}
