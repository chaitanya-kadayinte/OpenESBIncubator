package com.fiorano.openesb.applicationcontroller;

import com.fiorano.openesb.application.application.*;
import com.fiorano.openesb.application.application.Route;
import com.fiorano.openesb.jmsroute.impl.JMSRouteConfiguration;
import com.fiorano.openesb.microservice.launch.MicroServiceRuntimeHandle;
import com.fiorano.openesb.microservice.launch.impl.MicroServiceLauncher;
import com.fiorano.openesb.route.*;
import com.fiorano.openesb.transport.impl.jms.JMSPortConfiguration;

import java.util.HashMap;
import java.util.Map;

public class ApplicationHandle {

    private Application application;
    private MicroServiceLauncher service;
    private RouteService<RouteConfiguration> routeService;
    Map<String, MicroServiceRuntimeHandle> microServiceHandleList = new HashMap<>();
    private Map<String, com.fiorano.openesb.route.Route> routeMap = new HashMap<>();

    ApplicationHandle(Application application, MicroServiceLauncher service, RouteService<RouteConfiguration> routeService){
        this.application = application;
        this.service = service;
        this.routeService = routeService;

    }


    public void createRoutes() throws Exception {
        for(final Route route: application.getRoutes()) {

            String sourcePortInstance = route.getSourcePortInstance();
            JMSPortConfiguration sourceConfiguration = new JMSPortConfiguration();
            String appKey = application.getGUID() + "__" + application.getVersion() + "__";
            String sourceServiceInstance = route.getSourceServiceInstance();
            sourceConfiguration.setName(appKey + sourceServiceInstance + "__" + sourcePortInstance);
            OutputPortInstance outputPortInstance = application.getServiceInstance(sourceServiceInstance).getOutputPortInstance(sourcePortInstance);
            int type = outputPortInstance.getDestinationType();
            sourceConfiguration.setPortType(type == PortInstance.DESTINATION_TYPE_QUEUE ?
                    JMSPortConfiguration.PortType.QUEUE : JMSPortConfiguration.PortType.TOPIC);

            String destPortInstance = route.getTargetPortInstance();
            JMSPortConfiguration destinationConfiguration = new JMSPortConfiguration();
            String targetServiceInstance = route.getTargetServiceInstance();
            destinationConfiguration.setName(appKey + targetServiceInstance + "__" +destPortInstance);
            InputPortInstance inputPortInstance = application.getServiceInstance(targetServiceInstance).getInputPortInstance(destPortInstance);
            int inputPortInstanceDestinationType = inputPortInstance.getDestinationType();
            destinationConfiguration.setPortType(inputPortInstanceDestinationType == PortInstance.DESTINATION_TYPE_QUEUE ?
                    JMSPortConfiguration.PortType.QUEUE : JMSPortConfiguration.PortType.TOPIC);
            com.fiorano.openesb.route.Route route1 = routeService.createRoute(new JMSRouteConfiguration(sourceConfiguration, destinationConfiguration));
            route1.start();
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
        for(MicroServiceRuntimeHandle handle:microServiceHandleList.values()){
            handle.stop();
        }
        for(com.fiorano.openesb.route.Route route :routeMap.values()) {
            route.stop();
        }
    }
}
