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
 * Provides apis to query for state base workflow details
 *
 * User: sudharshan
 * Date: 1/3/14
 * Time: 3:43 PM
 */
public interface IDocTrackManager extends Remote {

    /**
     * Returns whether sbw is a custom database with call out.
     *
     * @throws java.rmi.RemoteException  A communication-related exception that may occur during the execution of a remote method call
     * @throws ServiceException Thrown if the Schema Reference is failed to be added to Schema repository
     */
    public boolean isCallOutEnabled() throws RemoteException, ServiceException;


    /**
     * Returns Data base call out query config
     *
     * @throws java.rmi.RemoteException  A communication-related exception that may occur during the execution of a remote method call
     * @throws ServiceException Thrown if the Schema Reference is failed to be added to Schema repository
     */
    public String getDBCallOutConfig() throws RemoteException, ServiceException;
}
