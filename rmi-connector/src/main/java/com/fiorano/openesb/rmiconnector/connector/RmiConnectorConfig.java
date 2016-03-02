package com.fiorano.openesb.rmiconnector.connector;

/**
 * Created by Janardhan on 3/2/2016.
 */
public class RmiConnectorConfig {
    private int     namingPort=2047;
    private String  interceptorClassName="fiorano.jmx.connector.FioranoJMXInterceptor";
    /**Port on which Rmi Registry will export the Mx4J RMIConnector stubs.*/
    private int rmiPortExportObjects=2047;
    /** specifies host name on which the registry would export objects. This value is set as the system property 'java.rmi.server.hostname'*/
    private String hostname="localhost";

    private String rmiServerSocketFactoryClassName="fiorano.rmi.serverfac.def.FioranoRMIServerSocketFactory";
    private String rmiClientSocketFactoryClassName="fiorano.rmi.clientfac.def.FioranoRMIClientSocketFactory";

    public int getRMIServerPort()
    {
        return namingPort;
    }

    public int getRmiPortExportObjects() {
        return rmiPortExportObjects;
    }

    public String getHostname() {
        return hostname;
    }

    public String getInterceptorClassName()
    {
        return interceptorClassName;
    }

    public void setRmiServerPort(String port)
    {
        namingPort = Integer.valueOf(port);
    }

    public void setRmiPortExportObjects(String rmiPortExportObjects) {
        this.rmiPortExportObjects = Integer.valueOf(rmiPortExportObjects);
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setInterceptorClassName(String interceptorClassName)
    {
        interceptorClassName = interceptorClassName;
    }

    public String getRmiServerSocketFactoryClassName()
    {
        return rmiServerSocketFactoryClassName;
    }

    public String getRmiClientSocketFactoryClassName()
    {
        return rmiClientSocketFactoryClassName;
    }

    public void setRmiClientSocketFactoryClassName(String rmiClientSocketFactoryClassName)
    {
        this.rmiClientSocketFactoryClassName=rmiClientSocketFactoryClassName;
    }

    public void setRmiServerSocketFactoryClassName(String rmiServerSocketFactoryClassName)
    {
        this.rmiServerSocketFactoryClassName=rmiServerSocketFactoryClassName;
    }
}
