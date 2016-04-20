package com.fiorano.openesb.utils;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {

    private Logger logger;

    public void start(BundleContext context) {
        logger = LoggerFactory.getLogger(getClass());
        logger.trace("Started utilities bundle.");
        System.setProperty("FIORANO_HOME", System.getProperty("user.home"));
    }

    public void stop(BundleContext context) {
        logger.trace("Stopped utilities bundle.");
    }

}