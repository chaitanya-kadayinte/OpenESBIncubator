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
 * This interface provides methods for login and logout of the enterprise server by rmi clients. For connection to the enterprise server,
 * the rmi clients have to look up this reference from RMI registry and login to the rmi manager. Login method checks the authenticity of user
 * and returns a handle id which has to be passed by the client while performing any operation on server.
 * <br></br>
 * <br>Registry registry = LocateRegistry.getRegistry("localhost", 2047);</br>
 * <br>rmiManager = (IRmiManager) registry.lookup(IRmiManager.class.toString());</br>
 * <br>handleID = rmiManager.login("admin", "passwd");</br>
 * <br></br>
 * <br></br>
 * From this rmiManager reference, client can get various managers such as event process manager, service manager , fps manager. These managers are
 * then used to perform operations on Fiorano event processes, Fiorano services  on the enterprise server.
 * logout method has to be called with the handle id by the client to destroy the connection with the enterprise server.
 *
 * @author FSTPL
 * @version 10
 */
public interface IRmiManager extends Remote {

    /**
     * This method logs into the enterprise server with the given credentials. This has to be called before any other call.Returns the handleID
     * of the connection handle created
     *
     * @param userName The Username
     * @param password The Password
     * @return String - handleId
     * @throws java.rmi.RemoteException A communication-related exception that may occur during the execution of a remote method call
     * @throws ServiceException         ServiceException
     */
    public String login(String userName, String password) throws RemoteException, ServiceException;

    /**
     * This method logs into the enterprise server with the given credentials. This has to be called before any other call.Returns the handleID
     * of the connection handle created
     *
     * @param userName The Username
     * @param password The Password
     * @param agent The name of client from which the user is logging in. (The module in which user is logging into. This overloaded method is intended for access to modules
     *                   which are not accessible by default (SOA) users like B2B, MDM. This is also used to log which client has logged in)
     * @return String - handleId
     * @throws java.rmi.RemoteException A communication-related exception that may occur during the execution of a remote method call
     * @throws ServiceException         ServiceException
     */
    public String login(String userName, String password, String agent) throws RemoteException, ServiceException;

    /**
     * This method returns EventProcessManager which performs operation on Event Processes
     *
     * @param handleID The handleID of the connection handle created
     * @return IEventProcessManager - An instance of IEventProcessManager which is used to perform operations on Event Processes
     * @throws RemoteException  A communication-related exception that may occur during the execution of a remote method call
     * @throws ServiceException ServiceException
     */
    public IEventProcessManager getEventProcessManager(String handleID) throws RemoteException, ServiceException;

    /**
     * This method returns ServiceManager which performs operations on services
     *
     * @param handleID The handleID of the connection handle created
     * @return IServiceManager - An instance of IServiceManager which is used to perform operations on services
     * @throws RemoteException  A communication-related exception that may occur during the execution of a remote method call
     * @throws ServiceException ServiceException
     */
    public IServiceManager getServiceManager(String handleID) throws RemoteException, ServiceException;

    /**
     * This method returns FPSManager which performs operation related to Peer Servers
     *
     * @param handleID The handleID of the connection handle created
     * @return IFPSManager - An instance of IFPSManager which is used to perform operations on Peer Servers
     * @throws RemoteException  A communication-related exception that may occur during the execution of a remote method call
     * @throws ServiceException ServiceException
     */
    public IFPSManager getFPSManager(String handleID) throws RemoteException, ServiceException;

    /**
     * This method returns ServiceProviderManager which performs operation related to Enterprise Server
     *
     * @param handleID The handleID of the connection handle created
     * @return IServiceProviderManager - An instance of IServiceProviderManager which is used to perform operations on Enterprise Server
     * @throws RemoteException  A communication-related exception that may occur during the execution of a remote method call
     * @throws ServiceException ServiceException
     */
    public IServiceProviderManager getServiceProviderManager(String handleID) throws RemoteException, ServiceException;
     /**
     * This method returns BreakpointManager which performs operations related to adding/removing breakpoints on routes.
     *
     * @param handleID The handleID of the connection handle created
     * @return IDebugger - An instance of IDebugger used to perform operations on breakpoints
     * @throws RemoteException  A communication-related exception that may occur during the execution of a remote method call
     * @throws ServiceException ServiceException
     */
    public IDebugger getBreakpointManager(String handleID) throws RemoteException, ServiceException;

    /**
     * This method returns ConfigurationManager which performs operations on configuration repository such as saving/deleting/searching named-configurations.
     *
     * @param handleID The handleID of the connection handle created
     * @return IConfigurationManager - An instance of IConfigurationManager used to perform operations on named configurations
     * @throws RemoteException  A communication-related exception that may occur during the execution of a remote method call
     * @throws ServiceException ServiceException
     */
    public IConfigurationManager getConfigurationManager(String handleID) throws RemoteException, ServiceException;

    /**
     * This method returns UserSecurityManager which performs operations on Users, Groups and ACLs
     *
     * @param handleID The handleID of the connection handle created
     * @return IUserSecurityManager - An instance of IUserSecurityManager used to perform operations on Users, Groups and ACLs
     * @throws RemoteException  A communication-related exception that may occur during the execution of a remote method call
     * @throws ServiceException ServiceException
     */
    public IUserSecurityManager getUserSecurityManager(String handleID) throws RemoteException, ServiceException;

    /**
     * This method returns KeyStoreManager which performs operations on FES KeyStore
     *
     * @param handleID The handleID of the connection handle created
     * @return IKeyStoreManager - An instance of IKeyStoreManager to perform operations on FES KeyStore
     * @throws RemoteException  A communication-related exception that may occur during the execution of a remote method call
     * @throws ServiceException ServiceException
     */
    public IKeyStoreManager getKeyStoreManager(String handleID) throws RemoteException, ServiceException;

    /**
     * This method returns SchemaReferenceManager which performs operations on FES Schema Reference repository
     * @param handleID The handleID of the connection handle created
     * @return ISchemaReferenceManager - An instance of ISchemaReferenceManager to perform operations on Schema Reference Repository
     * @throws RemoteException A communication-related exception that may occur during the execution of a remote method call
     * @throws ServiceException ServiceException
     */
    public ISchemaReferenceManager getSchemaReferenceManager(String handleID) throws RemoteException, ServiceException;

    /**
     * This method returns Sbw manager which allows to query for sbw related details
     * @param handleID The handleID of the connection handle created
     * @return IDocTrackManager - An instance of IDocTrackManager
     * @throws RemoteException A communication-related exception that may occur during the execution of a remote method call
     * @throws ServiceException ServiceException
     */
    public IDocTrackManager getDocTrackManager(String handleID) throws RemoteException, ServiceException;


    /**
     * This method removes the connection Handle created in enterprise server with the specified handle ID
     *
     * @param handleId The handleID of the connection handle created
     * @throws RemoteException  A communication-related exception that may occur during the execution of a remote method call
     * @throws ServiceException ServiceException
     */
    public void logout(String handleId) throws RemoteException, ServiceException;

    /**
     * This method removes the Old Listeners created in enterprise server with the specified handle ID in case of FES unavailable
     *
     * @param handleId The handleID of the connection handle created before logout.
     * @throws RemoteException  A communication-related exception that may occur during the execution of a remote method call
     * @throws ServiceException ServiceException
     */
    public void unRegisterOldListeners(String handleId) throws RemoteException, ServiceException;

}
