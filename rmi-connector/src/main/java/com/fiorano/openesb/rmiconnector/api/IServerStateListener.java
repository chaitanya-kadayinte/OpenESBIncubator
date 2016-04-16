package com.fiorano.openesb.rmiconnector.api;

import java.rmi.Remote;
import java.rmi.RemoteException;

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
