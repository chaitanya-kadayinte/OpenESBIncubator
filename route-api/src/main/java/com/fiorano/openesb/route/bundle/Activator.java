package com.fiorano.openesb.route.bundle;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {

    private Logger logger;

    public void start(BundleContext context) throws Exception {
        logger = LoggerFactory.getLogger(getClass());
        logger.trace("Starting route api bundle.");
    }

    public void stop(BundleContext context) {
        logger.trace("Stopping route api bundle.");
    }

}