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
package com.fiorano.openesb.rmiconnector.api.proxy;

import com.fiorano.openesb.rmiconnector.api.*;
import com.fiorano.openesb.rmiconnector.api.ServiceException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

/**
 * The Remote Object to exported to the client. It acts as a proxy to server side
 * DAPI service providers which implement IDistributedRemoteObject i.e.
 * IEventProcessManager , IServiceProviderManager , IServiceManager, IFPSManager.
 * @author Vishnu (Chander)
 * @see com.fiorano.openesb.rmi.impl.IDistributedRemoteObject
 * @see IEventProcessManager
 * @see IServiceProviderManager
 * @see IServiceManager
 * @see IFPSManager
 */

public interface IRemoteServerProxy extends Remote {

    public Object invoke (String methodName,Object []methodArgs, HashMap additionalInfo) throws RemoteException, ServiceException;

}
