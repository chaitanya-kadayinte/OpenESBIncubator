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
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Janardhan
 * Date: 12/10/13
 * Time: 10:50 AM
 * To change this template use File | Settings | File Templates.
 */

//This interface provides methods for doing operations on API Projects
public interface IAPIProjectsManager {

    /**
     * This method returns the IDs of all the available API Projects
     *
     * @return Array - IDs of available API Projects
     * @throws java.rmi.RemoteException  RemoteException
     * @throws ServiceException ServiceException
     */
    public String[] getAPIProjectsIds() throws RemoteException, ServiceException;

    /**
     * This method checks whether an API Project exists
     *
     * @param id       The ID of the API Project
     * @param version  The version of the API Project
     * @return boolean - true, if exists, false otherwise
     * @throws RemoteException  RemoteException
     * @throws ServiceException ServiceException
     */
    public boolean exists(String id, String version) throws RemoteException, ServiceException;

    /**
     * This method returns the list of versions available for a particular API Project
     *
     * @param projectId The ID of the API Project
     * @return String Array - Array of versions of particular API Project
     * @throws RemoteException  RemoteException
     * @throws ServiceException ServiceException
     */
    public String[] getVersions(String projectId) throws RemoteException, ServiceException;

    /**
     * This method saves the API Project in the repository. If a Project exists with the same ID & version, it will be
     * overwritten
     *
     * @param zippedContents The contents of the API Project in zipped form
     * @param completed      Notifies server that sending zip contents completed
     * @throws RemoteException  RemoteException
     * @throws ServiceException ServiceException
     */
    public void saveAPIProject(byte[] zippedContents, boolean completed) throws RemoteException, ServiceException;

    /**
     * This method returns the API Project in a zipped form. Clients are expected to extract it and use it.
     *
     * @param projectID  ID of the API Project
     * @param version  The version
     * @param index    index of byte[] from where ro read the contents of zip file.starting index would be 0.
     * @return Byte Array - The contents of the API Project in zipped form.Returns null once all contents have been read.
     * @throws RemoteException  RemoteException
     * @throws ServiceException ServiceException
     */
    public byte[] getAPIProject(String projectID, String version, long index) throws RemoteException, ServiceException;

    /**
     * This method deletes the API Project. Provide -1 for version to delete all versions of the project.
     *
     * @param projectID  Name of the API Project
     * @param version  The version of the API Project
     * @throws RemoteException  RemoteException
     * @throws ServiceException ServiceException
     */
    public void deleteAPIProject(String projectID, String version) throws RemoteException, ServiceException;

    /**
     * This method starts the specified API Project
     *
     * @param projectID  Name of the API Project
     * @param version  The version of the API Project
     * @throws RemoteException  RemoteException
     * @throws ServiceException ServiceException
     */
    public void deployAPIProject(String projectID, String version) throws RemoteException, ServiceException;

    /**
     * This method redeploys the specified API Project
     *
     * @param projectID  Name of the API Project
     * @param version  The version of the API Project
     * @throws RemoteException  RemoteException
     * @throws ServiceException ServiceException
     */
    public void reDeployAPIProject(String projectID, String version) throws RemoteException, ServiceException;

    /**
     * This method undeploys the specified API Project
     *
     * @param projectID  Name of the API Project
     * @param version  Version of the API Project
     * @throws RemoteException  A communication-related exception that may occur during the execution of a remote method call
     * @throws ServiceException ServiceException
     */
    public void unDeployAPIProject(String projectID, String version) throws RemoteException, ServiceException;

    /**
     * This method returns the list of deployed API projects
     *
     * @return List - List of API Projects
     * @throws RemoteException  A communication-related exception that may occur during the execution of a remote method call
     * @throws ServiceException ServiceException
     */
    public List[] getDeployedAPIProjects() throws RemoteException, ServiceException;

    /**
     * This method adds a listener to the changes of IAPIProjectsManager's object
     *
     * @param listener The listener to add
     * @param version version of API Project
     * @param projectID  Name of API Project
     * @throws RemoteException  A communication-related exception that may occur during the execution of a remote method call
     * @throws ServiceException ServiceException
     */
    public void addAPIProjectListener(IAPIProjectManagerListener listener, String projectID, String version) throws RemoteException, ServiceException;

    /**
     * This method removes the listener from the IAPIProjectsManager
     *
     * @param listener The listener to remove
     * @param projectID  Name of API Project
     * @param version version of API Project
     * @throws RemoteException  A communication-related exception that may occur during the execution of a remote method call
     * @throws ServiceException ServiceException
     */
    public void removeAPIProjectListener(IAPIProjectManagerListener listener, String projectID, String version) throws RemoteException, ServiceException;

    /**
     * This method adds a listener to the IAPIProjectsRepositoryEventListener's changes
     *
     * @param listener The listener to add
     * @throws RemoteException  A communication-related exception that may occur during the execution of a remote method call
     * @throws ServiceException ServiceException
     */
    public void addAPIProjectsRepositoryEventListener(IAPIProjectsRepositoryEventListener listener) throws RemoteException, ServiceException;

    /**
     * This method removes the listener from the IAPIProjectsManager
     *
     * @throws RemoteException  A communication-related exception that may occur during the execution of a remote method call
     * @throws ServiceException ServiceException
     */
    public void removeAPIProjectsRepositoryEventListener() throws RemoteException, ServiceException;

    /**
     * This method returns true if the specified API Project is Deployed
     *
     * @param projectID  Name of API Project
     * @param version Version of API Project
     * @return boolean - Running status of the API Project
     * @throws RemoteException
     * @throws ServiceException
     */
    public boolean isDeployed(String projectID, String version) throws RemoteException, ServiceException;

    /**
     * This method sets the log level for the API Project
     *
     * @param projectID Name of API Project
     * @param version Version of API Project
     * @param modules         The name log of modules for which log level is to be set.
     * @throws RemoteException  A communication-related exception that may occur during the execution of a remote method call
     * @throws ServiceException ServiceException
     */
    public void setLogLevel(String projectID, String version, Hashtable modules) throws RemoteException, ServiceException;

    /**
     * This method returns the info of all API Projects
     *
     * @return Hashtable - containing the info of all API Projects
     * @throws RemoteException  A communication-related exception that may occur during the execution of a remote method call
     * @throws ServiceException ServiceException
     */
    public Hashtable<String, ArrayList> getAllAPIProjectsInfo() throws RemoteException, ServiceException;

    /**
     * This method returns API Project info
     * @param projectID Name of API Project
     * @param version Version of API Project
     * @return List retuns the Project info in the List.
     * @throws RemoteException  A communication-related exception that may occur during the execution of a remote method call
     * @throws ServiceException ServiceException
     */
    public List getAPIProjectInfo(String projectID, String version) throws RemoteException, ServiceException;
}
