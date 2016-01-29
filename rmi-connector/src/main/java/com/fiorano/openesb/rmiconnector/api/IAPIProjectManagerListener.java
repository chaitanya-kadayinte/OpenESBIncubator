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

import java.rmi.RemoteException;

/**
 * Created with IntelliJ IDEA.
 * User: Janardhan
 * Date: 12/10/13
 * Time: 11:41 AM
 * To change this template use File | Settings | File Templates.
 */

/**
 * This is a callback listener interface which a client has to implement. An implementing class either has to
 * extend UniCastRemoteObject or call {@link  java.rmi.server.UnicastRemoteObject#exportObject(java.rmi.Remote)}.
 * When an implementing class has registered to receive such events, it will be notified of API Projects events
 * such as deploy, undeploy, etc. Registration for these events can be done via
 * {@link com.fiorano.openesb.rmi.impl.APIProjectsManager#addAPIProjectListener(IAPIProjectManagerListener, String, String)}
 *
 */

public interface IAPIProjectManagerListener {

    /**
     * This method sends a notification to the client when an API Project is deleted from the server
     *
     * @param version The version of the API Project deleted
     * @throws java.rmi.RemoteException A communication-related exception that may occur during the execution of a remote method call
     * @deprecated Use {@link IRepositoryEventListener} to register for repository Updation events : apiProjectDeleted, apiProjectDeployed.
     */
    public void apiProjectDeleted(float version) throws RemoteException;

    /**
     * This method sends a notification to the client when an API Project is saved in the server repository (i.e. any changes to the API Project done and hence saved).
     *
     * @param version The version of the API Project saved
     * @throws RemoteException A communication-related exception that may occur during the execution of a remote method call
     * @deprecated Use {@link IRepositoryEventListener} to register for repository Update events : apiProjectDeleted, apiProjectDeployed.
     */
    public void apiProjectSaved(float version) throws RemoteException;

    /**
     * This method sends a notification to the client when an API Project has Deployed up successfully in the server
     *
     * @param version The version of the API Project Deployed
     * @throws RemoteException A communication-related exception that may occur during the execution of a remote method call
     */
    public void apiProjectDeployed(float version) throws RemoteException;

    /**
     * This method sends a notification to the client when an API Project is starting up in the server.
     *
     * @param version The version of the API Project Deployed
     * @throws RemoteException A communication-related exception that may occur during the execution of a remote method call
     */
    public void apiProjectDeploying(float version) throws RemoteException;

    /**
     * This method sends a notification to the client when an API Project is UnDeployed in the server
     *
     * @param version The version of the API Project UnDeployed
     * @throws RemoteException A communication-related exception that may occur during the execution of a remote method call
     */
    public void apiProjectUnDeployed(float version) throws RemoteException;
}
