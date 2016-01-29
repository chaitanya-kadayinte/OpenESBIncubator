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
import java.util.List;
import java.util.Map;

/**
 * This interface provides methods for doing operations on services/components.
 *
 * @author FSTPL
 * @version 10
 *
 * */
public interface IServiceManager extends Remote {

    /**
     * Constant used to denote any version.
     *
     * @see IServiceManager#exists(String, float)
     * @see IServiceManager#getService(String, float, long)
     * @see IServiceManager#delete(String, float,boolean)
     */
    public float ANY_VERSION = -1;


    /**
     * This method returns the ids of all the available services
     *
     * @return String Array - Array of GUIDs of all available services, never <code>null</code>.
     * @throws ServiceException ServiceException
     * @throws RemoteException  RemoteException
     */
    public String[] getServiceIds() throws RemoteException, ServiceException;

    /**
     * This method checks whether a service exists
     *
     * @param id      The GUID of the service
     * @param version The version of the service
     * @return boolean - true, if exists, false otherwise
     * @throws ServiceException ServiceException
     * @throws RemoteException  RemoteException
     * @see IServiceManager#ANY_VERSION
     */
    public boolean exists(String id, float version) throws RemoteException, ServiceException;

    /**
     * This method returns the list of versions available for a particular service
     *
     * @param id The GUID of the service
     * @return Float Array - Array of versions available for a particular service, never <code>null</code>
     * @throws ServiceException ServiceException
     * @throws RemoteException  RemoteException
     */
    public float[] getVersions(String id) throws RemoteException, ServiceException;

    /**
     * This method returns the list of Service References available in the server
     *
     * @return ServiceReference Array - Array of Service References corresponding to a single (serviceGUID, version) pair in the server's component repository
     * @throws RemoteException  RemoteException
     * @throws ServiceException ServiceException
     */
    public ServiceReference[] getAllServices() throws RemoteException, ServiceException;

    /**
     *
     * @param zippedContents
     * @param completed
     * @throws RemoteException
     * @throws ServiceException
     */
    public void deployService(byte[] zippedContents, boolean completed) throws RemoteException, ServiceException;
    /**
     * This method deploys the service. If a service exists with the same GUID & version, it will be overwritten
     *
     * @param zippedContents The contents of the service in zipped form
     * @param completed      boolean specifying if specified <code>zippedContents</code> is the last chunk to be deployed for this event process
     * @param resync        boolean specifying whether to kill all dependent services or not
     * @param retainOldResources boolean specifying whether to retain old unique resources
     * @param servicesToImport List of service(s) to be imported from the zip file other service will be ignored. If value of the list is 'null' or empty, no services in the zip file will be imported
     *                         Format of each element in the list is ServiceGUID__ServiceVersion
     * @throws RemoteException  RemoteException
     * @throws ServiceException ServiceException
     */
    public void deployService(byte[] zippedContents, boolean completed, boolean resync, boolean retainOldResources, List servicesToImport ) throws RemoteException, ServiceException;

    /**
     * This method returns a map of dependencies for the service.
     * @param id        The GUID of the Service
     * @param version   The Version of the Service
     * @return Map - GUID and version map of dependencies
     * @throws RemoteException  RemoteException
     * @throws ServiceException ServiceException
     */
    public Map<String, Float> getDependenciesForService(String id, String version) throws RemoteException, ServiceException;

    /**
     * This method returns the service in a zipped form. Clients are expected to extract it and use it. If
     * <code>ANY_VERSION</code> is passed then the latest version is given
     *
     * @param id      The GUID of the service
     * @param version The version of the service
     * @param index   index of byte[] from where to read the service.starting index would be 0.
     * @return Byte Array - The contents of the service in zipped form. Returns null once all contents have been read.
     * @throws ServiceException ServiceException
     * @throws RemoteException  RemoteException
     * @see IServiceManager#ANY_VERSION
     */
    public byte[] getService(String id, float version, long index) throws RemoteException, ServiceException;

    /**
     * This method deletes the service. If <code>ANY_VERSION</code> is passed then the all the versions are
     * deleted
     *
     * @param id       The GUID of the service
     * @param version  The version of the service
     * @param killRunningInstances boolean indicating whether all current running instances
     *      of this service should be killed in all connected Peer Servers
     * @throws ServiceException ServiceException
     * @throws RemoteException  RemoteException
     * @see IServiceManager#ANY_VERSION
     */
    public void delete(String id, float version, boolean killRunningInstances) throws RemoteException, ServiceException;

    /**
     * This method checks for existence of dependencies
     *
     * @param serviceRefs The dependencies of a Service
     * @return boolean - returns true if all the dependencies exist, false otherwise
     * @throws ServiceException ServiceException
     * @throws RemoteException  RemoteException
     */
    public boolean dependenciesExists(ServiceReference[] serviceRefs) throws RemoteException, ServiceException;

    /**
     * This method adds a listener for service Repository update related events.
     *
     * @param listener The listener to add
     * @throws ServiceException ServiceException
     * @throws RemoteException  RemoteException
     */
    public void addServiceRepositoryEventListener(IRepositoryEventListener listener) throws RemoteException, ServiceException;

    /**
     * This method removes the listener for service Repository update related events.
     *
     * @throws ServiceException ServiceException
     * @throws RemoteException  RemoteException
     */
    public void removeServiceRepositoryEventListener() throws RemoteException, ServiceException;

    /**
     * This method downloads the resource from the Enterprise Server's Service Repository. All the resources which are the part of the service can be downloaded
     * from the enterprise server using this API.
     *
     * @param id      The GUID of the service from which the resource is to be retrieved
     * @param version he version of the service from which the resource is to be retrieved
     * @param resName The name of the resource to be fetched.
     * @param index   index of byte[] from where ro read the Resource
     * @return Byte Array - The contents of the Resource as byte Array
     * @throws RemoteException  A communication-related exception that may occur during the execution of a remote method call
     * @throws ServiceException ServiceException
     */
    public byte[] fetchResourceForService(String id, float version, String resName, long index) throws RemoteException, ServiceException;

}
