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
package com.fiorano.openesb.rmiconnector.server;

import com.fiorano.openesb.rmiconnector.client.FioranoRMIClientSocketFactory;

import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the Master RMI Socket factory of the server. It produces different type of factories on demand.
 * Example usage: It can be used to create instances of ssl socket factories for client and server. This factory is a singleton instance.
 * @author Chander (vishnu)
 * @bundle classes.0.1.provided.for.rmi.server.socket.client.factories.could.not.be.found.returning.default.fiorano.socket.factories=Classes :{0},{1} provided for RMI Server Socket & Client Factories could not be found. Returning default Fiorano socket factories.
 * @bundle returning.fiorano.default.rmi.socketfactories.error.occured.while.creating.instances.of.configured.factory.classes=Returning fiorano default RMI SocketFactories. Error occured while creating instances of configured factory classes
 */
public class FioranoRMIMasterSocketFactory {

    private static FioranoRMIMasterSocketFactory masterfac;

    /**
     * gets an instance of the RMI master socket factory.
     * @return FioranoRMIMasterSocketFactory
     */
    public synchronized static FioranoRMIMasterSocketFactory getInstance(){
       if(masterfac == null)     //singleton instance
            masterfac = new FioranoRMIMasterSocketFactory();
       return masterfac;
    }

    /**
     * Returns an array list of size 2. The first element is an instance of RMIClientSocketFactory & the
     * second , an instance of RMIServerSocketFactory. If the classes are not found or if any error
     * occurs, it returns the default fiorano rmi server & client socket factories.
     * @param serverFactoryClassName
     * @param clientFactoryClassName
     * @return List
     * @see com.fiorano.openesb.rmiconnector.client.FioranoRMIClientSocketFactory
     * @see com.fiorano.openesb.rmiconnector.server.FioranoRMIServerSocketFactory
     */
    public List getSocketFactories(String serverFactoryClassName,String clientFactoryClassName)
    {
        RMIServerSocketFactory ssf = null;
        RMIClientSocketFactory csf = null;
        try
        {
            Class client = Class.forName(clientFactoryClassName);
            Class server = Class.forName(serverFactoryClassName);
            csf = (RMIClientSocketFactory)client.newInstance();
            ssf = (RMIServerSocketFactory)server.newInstance();
        }
        catch (ClassNotFoundException e)
        {
            csf = new FioranoRMIClientSocketFactory();
            ssf = new FioranoRMIServerSocketFactory();
        } catch (IllegalAccessException e) {
            csf = new FioranoRMIClientSocketFactory();
            ssf = new FioranoRMIServerSocketFactory();
        } catch (InstantiationException e) {
            csf = new FioranoRMIClientSocketFactory();
            ssf = new FioranoRMIServerSocketFactory();
        }

        List list = new ArrayList(2);
        list.add(0,csf);
        list.add(1,ssf);
        return list;
    }

}
