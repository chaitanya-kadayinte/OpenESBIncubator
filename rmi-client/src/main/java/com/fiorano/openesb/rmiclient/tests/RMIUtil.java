package com.fiorano.openesb.rmiclient.tests;

import com.fiorano.openesb.rmiconnector.server.FioranoRMIMasterSocketFactory;

import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.util.List;

/**
 * Created by Janardhan on 3/7/2016.
 */
public class RMIUtil {
    private static RMIUtil SINGLETON;

    private String rmiServerSocketFactoryClassName="com.fiorano.openesb.rmiconnector.server.FioranoRMIServerSocketFactory"; //$NON-NLS-1$
    private String rmiClientSocketFactoryClassName="com.fiorano.openesb.rmiconnector.client.FioranoRMIClientSocketFactory"; //$NON-NLS-1$
    private RMIServerSocketFactory ssf;
    private RMIClientSocketFactory csf;
    private FioranoRMIMasterSocketFactory masterfac;

    private RMIUtil() {
        masterfac = FioranoRMIMasterSocketFactory.getInstance();
        List factories = masterfac.getSocketFactories(rmiServerSocketFactoryClassName, rmiClientSocketFactoryClassName);
        csf =(RMIClientSocketFactory)factories.get(0);
        ssf =(RMIServerSocketFactory)factories.get(1);
    }

    public static RMIUtil getInstance() {
        if(SINGLETON == null) {
            SINGLETON = new RMIUtil();
        }
        return SINGLETON;
    }

    public RMIServerSocketFactory getSsf() {
        return ssf;
    }

    public RMIClientSocketFactory getCsf() {
        return csf;
    }

}

