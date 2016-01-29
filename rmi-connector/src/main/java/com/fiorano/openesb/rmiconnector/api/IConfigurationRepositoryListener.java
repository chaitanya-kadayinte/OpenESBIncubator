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

import com.fiorano.openesb.application.configuration.data.NamedObject;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This is a callback listener interface which a client has to implement. An implementing class either has to
 * extend UniCastRemoteObject or call {@link  java.rmi.server.UnicastRemoteObject#exportObject(java.rmi.Remote)}.
 * When an implementing class has registered to receive such events, it will be notified of configuration repository events
 * such as add new configuration, delete configuration etc. Registration for these events can be done via
 * {@link com.fiorano.openesb.rmi.impl.ConfigurationManager#addConfigurationRepositoryListener(IConfigurationRepositoryListener)}
 *
 * @author FSTPL
 * @version 10
 */
public interface IConfigurationRepositoryListener extends Remote {

    /**
     * This notification is sent to the client whenever a configuration is persisted into configuration repository
     * @param namedObject An object containing essential parameters which define the configuration persisted
     */
    public void configurationPersisted(NamedObject namedObject) throws RemoteException;

    /**
     * This notification is sent to the client whenever a configuration is deleted from configuration repository
     * @param namedObject An object containing essential parameters which define the configuration deleted
     */
    public void configurationDeleted(NamedObject namedObject) throws RemoteException;
}
