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
        System.out.println("Activating Fiorano Applications Module");
        ServiceReference<ApplicationRepository> applicationRepositoryRef = context.getServiceReference(ApplicationRepository.class);
        if (applicationRepositoryRef != null) {
            ApplicationRepository applicationRepository = context.getService(applicationRepositoryRef);

            applicationController = new ApplicationController(applicationRepository, context);
            context.registerService(ApplicationController.class.getName(), applicationController, null);

        }
        System.out.println("Activated Fiorano Applications Module");
        logger.trace("Started Application Controller bundle.");

    }

    public void stop(BundleContext context) {
        logger.info("Stopping Application Controller bundle");
        applicationController.Stop();
        System.out.println("De-activated Fiorano Applications Module");
        logger.trace("Stopped Application Controller bundle.");
    }

}