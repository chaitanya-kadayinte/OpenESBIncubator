package com.fiorano.openesb.events;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    public void start(BundleContext context) {
        context.registerService(
                EventsManager.class.getName(), new EventsManager(), null);
    }

    public void stop(BundleContext context) {
    }

}