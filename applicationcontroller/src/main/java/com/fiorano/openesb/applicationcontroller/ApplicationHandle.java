package com.fiorano.openesb.applicationcontroller;

import com.fiorano.openesb.application.application.*;
import com.fiorano.openesb.application.application.Route;
import com.fiorano.openesb.jmsroute.impl.JMSRouteConfiguration;
import com.fiorano.openesb.microservice.launch.MicroserviceRuntimeHandle;
import com.fiorano.openesb.microservice.launch.impl.MicroServiceLauncher;
import com.fiorano.openesb.route.*;
import com.fiorano.openesb.transport.impl.jms.JMSPortConfiguration;

import java.util.HashMap;
import java.util.Map;

public class ApplicationHandle {

    Application application;
    private MicroServiceLauncher service;
    private RouteService<RouteConfiguration> routeService;
    Map<String, MicroserviceRuntimeHandle> microServiceHandleList = new HashMap<>();
    private Map<String, com.fiorano.openesb.route.Route> routeMap = new HashMap<>();

    ApplicationHandle(Application application, MicroServiceLauncher service, RouteService<RouteConfiguration> routeService){
        this.application = application;
        this.service = service;
        this.routeService = routeService;
    }

    public void createMicroServiceHandles() {


    }

    public void createRoutes() throws Exception {
        for(final Route route: application.getRoutes()) {
            String sourcePortInstance = route.getSourcePortInstance();
            JMSPortConfiguration sourceConfiguration = new JMSPortConfiguration();
            sourceConfiguration.setName(sourcePortInstance);
            OutputPortInstance outputPortInstance = application.getServiceInstance(route.getSourceServiceInstance()).getOutputPortInstance(sourcePortInstance);
            int type = outputPortInstance.getDestinationType();
            sourceConfiguration.setPortType(type == PortInstance.DESTINATION_TYPE_QUEUE ?
                    JMSPortConfiguration.PortType.QUEUE : JMSPortConfiguration.PortType.TOPIC);

            String destPortInstance = route.getTargetPortInstance();
            JMSPortConfiguration destinationConfiguration = new JMSPortConfiguration();
            destinationConfiguration.setName(destPortInstance);
            InputPortInstance inputPortInstance = application.getServiceInstance(route.getTargetServiceInstance()).getInputPortInstance(destPortInstance);
            int inputPortInstanceDestinationType = inputPortInstance.getDestinationType();
            destinationConfiguration.setPortType(inputPortInstanceDestinationType == PortInstance.DESTINATION_TYPE_QUEUE ?
                    JMSPortConfiguration.PortType.QUEUE : JMSPortConfiguration.PortType.TOPIC);

            com.fiorano.openesb.route.Route route1 = routeService.createRoute(new JMSRouteConfiguration(sourceConfiguration, destinationConfiguration));
            routeMap.put(route.getName(), route1);
        }
    }

    public void launchComponents() {
        for (Object obj : application.getServiceInstances()) {
            ServiceInstance instance = (ServiceInstance) obj;
            String instanceName = instance.getName();
            MicroServiceLaunchConfiguration mslc = new MicroServiceLaunchConfiguration(application.getGUID(), String.valueOf(application.getVersion()), "karaf", "karaf", instance);
            try {
                microServiceHandleList.put(instanceName, service.launch(mslc,instance.getConfiguration()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stopApplication() throws Exception {
        for(MicroserviceRuntimeHandle handle:microServiceHandleList.values()){
            handle.stop();
        }
        for(com.fiorano.openesb.route.Route route :routeMap.values()) {
            route.stop();
        }
    }
}
