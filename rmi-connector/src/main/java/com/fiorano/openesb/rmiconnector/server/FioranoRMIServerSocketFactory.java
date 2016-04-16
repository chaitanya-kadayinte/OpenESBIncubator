package com.fiorano.openesb.rmiconnector.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMISocketFactory;

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
