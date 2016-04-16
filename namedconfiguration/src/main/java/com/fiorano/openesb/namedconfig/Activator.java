package com.fiorano.openesb.namedconfig;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;

public class Activator implements BundleActivator {

    private Logger logger;

    public void start(BundleContext context) {
        logger = LoggerFactory.getLogger(getClass());
        logger.trace("Starting Named Configuration bundle.");
        NamedConfigRepository namedConfigRepository = new NamedConfigRepository();
        context.registerService(NamedConfigRepository.class,namedConfigRepository,new Hashtable<String, Object>());
        logger.debug("Started Named Configuration bundle.");
    }

    public void stop(BundleContext context) {
        logger.trace("Stopped Named Configuration bundle.");
    }

}