package com.fiorano.openesb.rmiconnector.connector;

import com.fiorano.openesb.rmiconnector.server.FioranoRMIMasterSocketFactory;
import com.fiorano.openesb.utils.ConfigReader;
import com.fiorano.openesb.utils.exception.FioranoException;

import javax.management.MBeanServerFactory;
import javax.management.remote.*;
import javax.management.remote.rmi.RMIConnectorServer;
import javax.naming.Context;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.util.*;

/**
 * Created by Janardhan on 1/20/2016.
 */
public class RmiConnector {
    private RMIServerSocketFactory ssf;
    private RMIClientSocketFactory csf;
    FioranoNamingService namingService;
    private JMXConnectorServer connectorServer;
    private RmiConnectorConfig rmiConnectorConfig;

    public RmiConnector(){
        rmiConnectorConfig = new RmiConnectorConfig();
        try {
            File configFile = new File(System.getProperty("karaf.base") + File.separator
                    + "etc" + File.separator + "com.fiorano.openesb.rmiconnector.cfg");
            if(!configFile.exists()){
                return;
            }
            ConfigReader.readConfigFromProperties(configFile, rmiConnectorConfig);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public RmiConnectorConfig getRmiConnectorConfig(){
        return rmiConnectorConfig;
    }

    public void createService()
            throws FioranoException
    {
        try
        {
            System.setProperty("java.rmi.server.hostname", rmiConnectorConfig.getHostname());
            //Start Naming Service first.
            FioranoRMIMasterSocketFactory masterfac = FioranoRMIMasterSocketFactory.getInstance();
            List factories = masterfac.getSocketFactories("FioranoRMIServerSocketFactory",
                    "FioranoRMIClientSocketFactory");

            csf =(RMIClientSocketFactory)factories.get(0);
            ssf =(RMIServerSocketFactory)factories.get(1);
            setRmiRegistryIpAddress();// ---->do before starting fiorano naming service !
            //namingService = new FioranoNamingService(rmiConnectorConfig.getRmiRegistryPort(),csf,ssf);
            namingService = new FioranoNamingService(rmiConnectorConfig.getRmiRegistryPort(),csf,ssf);
            namingService.start();

            /*JMXServiceURL address = new JMXServiceURL("rmi", rmiConnectorConfig.getHostname(), rmiConnectorConfig.getRMIServerPort(), "/fiorano");

            Map environment = new HashMap();

            environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
            environment.put(Context.PROVIDER_URL, "rmi://"+rmiConnectorConfig.getHostname()+":"+rmiConnectorConfig.getRMIServerPort());
            environment.put(RMIConnectorServer.RMI_CLIENT_SOCKET_FACTORY_ATTRIBUTE, csf);
            environment.put(RMIConnectorServer.RMI_SERVER_SOCKET_FACTORY_ATTRIBUTE, ssf);*/
            /*JMXAuthenticator auth = JMXAuthenticatorFactory.createAuthenticator("fiorano.jms.jmx.authenticator.FMQJmxAuthenticator");
            Method method = auth.getClass().getMethod("setRealmManager", new Class[]{Object.class});

            method.invoke(auth, new Object[]{realmManager});
            ////////////////////////////////////////////////////////

            environment.put(JMXConnectorServer.AUTHENTICATOR, auth);*/

           // connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(address, environment, MBeanServerFactory.createMBeanServer("Fiorano"));
            /*listener = new JMXConnectionListener();
            listener.setLogger(m_loggerRuntime);
            connectorServer.addNotificationListener(listener, null, null);*/

          /*  String interceptorClassName = config.getInterceptorClassName();
            MBeanServerForwarder interceptor = null;

            try
            {
                if (interceptorClassName != null && !interceptorClassName.equalsIgnoreCase(""))
                {
                    Class c = Class.forName(interceptorClassName);

                    interceptor = (MBeanServerForwarder) c.newInstance();
                    interceptor.setMBeanServer(m_mBeanServer);

                    if (interceptor instanceof FioranoJMXInterceptor)
                        ((FioranoJMXInterceptor) interceptor).setLoggerFactory(m_logFactory);

                    connectorServer.setMBeanServerForwarder(interceptor);
                }
            }
            catch (Throwable t)
            {
                System.out.println(FMQI18Nutil.getL10NMsg(this.getClass(), "warning.unable.to.set.interceptor.for.jmx.calls"));
            }
*/
            //connectorServer.start();

            System.out.println("rmi registry listening on " + rmiConnectorConfig.getRmiRegistryPort());
        }
        catch (Exception ex)
        {

        }
    }

    /** Sets the 'java.rmi.server.hostname' system property. <br>WARNING: Call it before creating rmi registry else it is of no use. </br>*/
    private void setRmiRegistryIpAddress(){

        /*if(this.config.getHostname() != null)
        {
            System.setProperty("java.rmi.server.hostname",this.config.getHostname());
            m_loggerRuntime.info(RmiConnectorMBean.class, FMQI18Nutil.getL10NMsg(this.getClass(), "exporting.rmi.objects", this.config.getHostname()));
            return;
        }*/
        StringBuilder bf= new StringBuilder();
        try
        {
            NetworkInterface iface;
            for(Enumeration ifaces = NetworkInterface.getNetworkInterfaces();ifaces.hasMoreElements();)
            {
                iface = (NetworkInterface)ifaces.nextElement();
                InetAddress ia;
                Enumeration ips =    iface.getInetAddresses();
                while(ips.hasMoreElements())
                {
                    ia = (InetAddress)ips.nextElement();
                    // get all the ip aliases from all the network cards except for
                    // loopback addresses. loop back addresses are not included because, then rmi registry would
                    // export objects on 127.0.0.1 or localhost => hence server would be accessible to clients present only on localhost i.e the same machine as server!!!!
                    if(ia instanceof Inet4Address && !ia.isLoopbackAddress())
                    {
                        String ip=ia.getHostAddress();
                        bf.append(ip);
                        bf.append(',');
                    }
                }
            }
            String hosts=bf.toString();
            if(hosts.endsWith(","))//hosts="a,b,c,"
                hosts=hosts.substring(0,hosts.lastIndexOf(","));// hosts="a,b,c"
            //Note: if network card is disabled=>hosts is empty, rmi runtimes uses localhost/127.0.0.1.
            System.setProperty("java.rmi.server.hostname",hosts);
        }
        catch (SocketException e)
        {
            e.printStackTrace();
        }
    }

    public RMIServerSocketFactory getSsf() {
        return ssf;
    }

    public RMIClientSocketFactory getCsf() {
        return csf;
    }
}
