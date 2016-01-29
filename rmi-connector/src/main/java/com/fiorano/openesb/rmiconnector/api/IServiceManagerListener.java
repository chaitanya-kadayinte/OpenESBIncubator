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
 * extend UniCastRemoteObject or call {@link  java.rmi.server.UnicastRemoteObject#exportObject(Remote)}.
 * When an implementing class has registered to receive such events, it will be notified of Service events
 * such as service instance deleted and deployed etc. Registration for these events can be done via
 * {@link IServiceManager#addListener(IServiceManagerListener, String, String)}
 *
 * @author FSTPL
 * @version 10
 * @deprecated use {@link IRepositoryEventListener} to register for repository Updation events : ServiceDeployed, Servicedeleted.
 */
public interface IServiceManagerListener extends Remote {

    /**
     * This notification is sent to the client when a Service Instance is deleted from the server
     *
     * @param serviceInstanceName The name of the Service Instance deleted
     * @param serviceVersion      The version of the Service Instance deleted
     * @throws RemoteException A communication-related exception that may occur during the execution of a remote method call
     */
    public void serviceDeleted(String serviceInstanceName, float serviceVersion) throws RemoteException;

    /**
     * This notification is sent to the client when a Service Instance is deployed in the server
     *
     * @param serviceInstanceName The name of the Service Instance deployed
     * @param serviceVersion      The version of the Service Instance deployed
     * @throws RemoteException A communication-related exception that may occur during the execution of a remote method call
     */
    public void serviceDeployed(String serviceInstanceName, float serviceVersion) throws RemoteException;

}
