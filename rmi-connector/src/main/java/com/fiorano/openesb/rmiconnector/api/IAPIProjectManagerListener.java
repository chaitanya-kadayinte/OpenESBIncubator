/*
 * Copyright (c) Fiorano Software Pte. Ltd. and affiliates. All rights reserved. http://www.fiorano.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.fiorano.openesb.rmiconnector.api;

import java.rmi.RemoteException;

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
