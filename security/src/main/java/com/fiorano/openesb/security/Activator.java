package com.fiorano.openesb.security;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;

public class Activator implements BundleActivator {

    private Logger logger;

    public void start(BundleContext context) {
        logger = LoggerFactory.getLogger(getClass());
        logger.trace("Starting security bundle.");
        context.registerService(SecurityManager.class.getName(), new SecurityManager(context), new Hashtable<String, Object>());
    }

    public void stop(BundleContext context) {
        logger.trace("Stopped security bundle.");
    }

}