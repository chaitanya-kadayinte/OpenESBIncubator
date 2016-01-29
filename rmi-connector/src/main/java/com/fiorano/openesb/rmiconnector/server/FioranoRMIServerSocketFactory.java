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

import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMISocketFactory;

/**
 * Default Fiorano RMI Server Socket Factory.
 * @see  com.fiorano.openesb.rmiconnector.client.FioranoRMIClientSocketFactory
 * @author Chander (Vishnu)
 */
public class FioranoRMIServerSocketFactory implements RMIServerSocketFactory {

    public final String socketFactoryID = "FioranoRMIServerDef";

    public ServerSocket createServerSocket(int port) throws IOException {

        return RMISocketFactory.getDefaultSocketFactory().createServerSocket(port);
    }

    public boolean equals(Object obj) {

        boolean ret=false;

        if (this == obj) ret= true;

        else if (obj == null) ret= false;

        else if (getClass() == obj.getClass()) ret = true;

        return ret;
    }

    public int hashCode() {    //keeping hashcode inline with .equals as per javadoc.
        return socketFactoryID.hashCode();
    }
}
