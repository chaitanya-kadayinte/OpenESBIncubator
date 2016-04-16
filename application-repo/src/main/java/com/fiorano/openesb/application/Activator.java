package com.fiorano.openesb.application;

import com.fiorano.openesb.application.ApplicationRepository;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {

    private Logger logger;

    public void start(BundleContext context) {
        logger = LoggerFactory.getLogger(getClass());
        logger.trace("Starting Application repo bundle.");
        context.registerService(
                ApplicationRepository.class.getName(), new ApplicationRepository(), null);
        System.out.println("Activated Fiorano Application Repository");
        logger.debug("Started Application repo bundle.");

    }

    public void stop(BundleContext context) {
        logger.trace("Stopped Application repo bundle.");
    }
}