package com.fiorano.openesb.applicationcontroller;

import com.fiorano.openesb.application.ApplicationRepository;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {

    private Logger logger;
    private ApplicationController applicationController;

    @SuppressWarnings("unchecked")
    public void start(BundleContext context) throws Exception {
        logger = LoggerFactory.getLogger(getClass());
        logger.trace("Starting Application Controller bundle.");
        System.out.println("Activating Application Controller");
        ServiceReference<ApplicationRepository> applicationRepositoryRef = context.getServiceReference(ApplicationRepository.class);
        if (applicationRepositoryRef != null) {
            ApplicationRepository applicationRepository = context.getService(applicationRepositoryRef);

            applicationController = new ApplicationController(applicationRepository, context);
            context.registerService(ApplicationController.class.getName(), applicationController, null);
            //applicationController.launchApplication("SELECTOR","1.0");
            //applicationController.launchApplication("TRANSFORMATION","1.0");
            //applicationController.launchApplication("SENDERSELECTOR","1.0",null);
        }
        System.out.println("Activated Application Controller");
        logger.trace("Started Application Controller bundle.");

    }

    public void stop(BundleContext context) {
        logger.info("Stopping Application Controller bundle");
        applicationController.Stop();
        logger.trace("Stopped Application Controller bundle.");
    }

}