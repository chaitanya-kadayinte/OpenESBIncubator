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
 *
 * This is a callback listener interface which a client has to implement.An implementing class either has to
 * extend UniCastRemoteObject or call {@link  java.rmi.server.UnicastRemoteObject#exportObject(Remote)}.
 * The implementing class should also implement the {@link java.rmi.server.Unreferenced} interface.
 * <br></br>
 * By doing so, the client can get notified if,
 *
 * <br> 1. the Fiorano Enterprise server is down/out of network.</br>
 * <br> 2. a Fiorano Peer Server is available in the network.</br>
 * <br> 3. a Fiorano Peer Server is no longer available in the network.</br>
 * <br></br>
 * Registration for these events can be done via
 * {@link IFPSManager#addListener(IServerStateListener)}
 * <br></br>
 * <br>Note: The unreferenced method in the implementing class of the client will be called when the Fiorano Enterprise server is down/out of network.</br>
 * @author FSTPL
 * @version 10
 */
public interface IServerStateListener extends Remote{

    /**
     * This notification is sent to client when a peer server becomes unavailable
     * @param fpsName fpsName
     * @throws RemoteException A communication-related exception that may occur during the execution of a remote method call
     */

    public void peerUnavailable(String fpsName) throws RemoteException;

    /**
     * This notification is sent to client when a peer server becomes available
     * @param fpsName fpsName
     * @throws RemoteException A communication-related exception that may occur during the execution of a remote method call
     */

    public void peerAvailable(String fpsName) throws RemoteException;
    /**
     * Implementing class should also implement java.rmi.server.Unreferenced interface, to get notified
     * when fes is down. Hence,No method is necessary for fes unavailable event in this interface.
     */
}
