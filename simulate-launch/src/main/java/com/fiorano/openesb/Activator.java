
package com.fiorano.openesb;

//import com.fiorano.openesb.applicationcontroller.ApplicationController;
import com.fiorano.openesb.route.Route;
import com.fiorano.openesb.route.RouteService;
import com.fiorano.openesb.jmsroute.impl.JMSRouteConfiguration;
import com.fiorano.openesb.transport.impl.jms.JMSPortConfiguration;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class Activator implements BundleActivator {

    public void start(BundleContext context) throws Exception {
        System.out.println("Starting the bundle " + context.getBundle().getSymbolicName());
//        ServiceReference<RouteService> routeServiceReference = context.getServiceReference(RouteService.class);
//        JMSPortConfiguration sourceConfiguration = new JMSPortConfiguration();
//        sourceConfiguration.setName("SourceQueue");
//        sourceConfiguration.setPortType(JMSPortConfiguration.PortType.QUEUE);
//
//        JMSPortConfiguration destinationConfiguration = new JMSPortConfiguration();
//        destinationConfiguration.setName("DestinationQueue");
//        destinationConfiguration.setPortType(JMSPortConfiguration.PortType.QUEUE);
//
//        JMSRouteConfiguration routeConfiguration = new JMSRouteConfiguration(sourceConfiguration, destinationConfiguration);
//
//        Route route = context.getService(routeServiceReference).createRoute(routeConfiguration);
//        route.start();
//        ServiceReference<ApplicationController> reference = context.getServiceReference(ApplicationController.class);
//        context.getService(reference).launchApplication("OS_TEST", "1.0");
//        System.out.println("Started the bundle " + context.getBundle().getSymbolicName());

    }

    public void stop(BundleContext context) {
        System.out.println("Stopping the bundle"+ context.getBundle().getSymbolicName());
    }

}