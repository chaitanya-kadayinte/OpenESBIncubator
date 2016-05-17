package com.fiorano.openesb.mq;

import com.fiorano.openesb.transport.TransportService;
import fiorano.jms.runtime.naming.FioranoJNDIContext;
import fiorano.jms.util.tunnel.TunneledSocket;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

public class Activator implements BundleActivator {

    private Logger logger;

    public void start(BundleContext context) {
        logger = LoggerFactory.getLogger(getClass());
        Properties properties = new Properties();
        try {
            try (FileInputStream inStream = new FileInputStream(System.getProperty("user.dir") + File.separator
                    + "etc" + File.separator + "com.fiorano.openesb.transport.provider.cfg")) {
                properties.load(inStream);
            }
            if (!properties.containsKey("provider.name") || !"fiorano".equalsIgnoreCase(properties.getProperty("provider.name"))) {
                return;
            }
            NamingException.class.getCanonicalName();
            FioranoJNDIContext.class.getCanonicalName();
            TunneledSocket.class.getCanonicalName();
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            MQTransportService mqTransportService = new MQTransportService(properties);
            context.registerService(TransportService.class, mqTransportService, new Hashtable<String, Object>());
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }

    }

    public void stop(BundleContext context) {
        logger.trace("Stopped Named Configuration bundle.");
    }

}