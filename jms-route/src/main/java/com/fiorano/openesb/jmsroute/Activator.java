
package com.fiorano.openesb.jmsroute;

import com.fiorano.openesb.route.RouteService;
import com.fiorano.openesb.jmsroute.impl.JMSRouteServiceImpl;
import com.fiorano.openesb.transport.TransportService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;

public class Activator implements BundleActivator {

    private Logger logger;

    @SuppressWarnings("unchecked")
    public void start(BundleContext context) {
        logger = LoggerFactory.getLogger(getClass());
        logger.trace("Starting JMS Route bundle.");
        TransportService service = context.getService(context.getServiceReference(TransportService.class));
        context.registerService(RouteService.class, new JMSRouteServiceImpl(service),new Hashtable<String, Object>());
        logger.debug("Started JMS Route bundle.");
    }

    public void stop(BundleContext context) {
        logger.trace("Stopping JMS Route bundle ");
    }
}