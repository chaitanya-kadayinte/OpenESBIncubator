package com.fiorano.openesb.applicationcontroller;

import com.fiorano.openesb.application.BreakpointMetaData;
import com.fiorano.openesb.application.application.*;
import com.fiorano.openesb.application.aps.ServiceInstances;
import com.fiorano.openesb.events.ApplicationEvent;
import com.fiorano.openesb.events.Event;
import com.fiorano.openesb.microservice.launch.impl.EventStateConstants;
import com.fiorano.openesb.route.Route;
import com.fiorano.openesb.application.aps.ApplicationStateDetails;
import com.fiorano.openesb.application.aps.ServiceInstanceStateDetails;
import com.fiorano.openesb.jmsroute.impl.JMSRouteConfiguration;
import com.fiorano.openesb.microservice.launch.MicroServiceRuntimeHandle;
import com.fiorano.openesb.microservice.launch.impl.MicroServiceLauncher;
import com.fiorano.openesb.route.*;
import com.fiorano.openesb.route.impl.*;
import com.fiorano.openesb.transport.TransportService;
import com.fiorano.openesb.transport.impl.jms.JMSPortConfiguration;
import com.fiorano.openesb.transport.impl.jms.TransportConfig;
import com.fiorano.openesb.utils.Constants;
import com.fiorano.openesb.utils.LookUpUtil;
import com.fiorano.openesb.utils.exception.FioranoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationHandle {
    private Logger logger = LoggerFactory.getLogger(Activator.class);
    private Application application;
    private MicroServiceLauncher service;
    private RouteService<RouteConfiguration> routeService;
    private TransportService transport;
    Map<String, MicroServiceRuntimeHandle> microServiceHandleList = new ConcurrentHashMap<>();
    private Map<String, Route> routeMap = new HashMap<>();
    private Map<String, Route> breakPointRoutes = new HashMap<>();
    private Map<String, BreakpointMetaData> breakpoints = new HashMap<String, BreakpointMetaData>();
    private Map<String, BreakpointMetaData> pendingBreakpointsForClouser = new HashMap<String, BreakpointMetaData>();
    private ApplicationController applicationController;


    Vector<String> pendingQueueIDs;
    //hash table of handle IDs of routes on which debuggers are set.[key -routeID Vs object -handleID]
    private Hashtable debugHandleIDs = new Hashtable();

    //  Unique AppGUID.
    private String appGUID;

    //  version.
    private float version;

    private String environmentLabel;

    //  LaunchTime.
    private long launchTime;

    //  KillTime.
    private long killTime = -1;

    // List of RouteGUID's in the application
    //private Vector<String> debugrouteGUIDS;

    private String passwd;

    private String userName;

    public ApplicationHandle(ApplicationController applicationController, Application application, MicroServiceLauncher service, RouteService<RouteConfiguration> routeService, TransportService transport, String userName, String passwd){
        this.applicationController = applicationController;
        this.application = application;
        this.service = service;
        this.routeService = routeService;
        this.transport = transport;
        this.appGUID = application.getGUID();
        this.version = application.getVersion();
        this.launchTime = System.currentTimeMillis();
        this.environmentLabel = application.getLabel();
        this.userName = userName;
        this.passwd = passwd;
    }

    public Application getApplication(){
        return application;
    }

    public MicroServiceLauncher getService() {
        return service;
    }

    public void setService(MicroServiceLauncher service) {
        this.service = service;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public float getVersion() {
        return version;
    }

    public void setVersion(float version) {
        this.version = version;
    }

    public String getAppGUID() {
        return appGUID;
    }

    public void setAppGUID(String appGUID) {
        this.appGUID = appGUID;
    }

    public void createRoutes() throws Exception {
        for(final com.fiorano.openesb.application.application.Route route: application.getRoutes()) {
            if(routeMap.containsKey(route.getName())){
                continue;
            }
            String sourcePortInstance = route.getSourcePortInstance();
            JMSPortConfiguration sourceConfiguration = new JMSPortConfiguration();
            String sourceServiceInstance = route.getSourceServiceInstance();
            sourceConfiguration.setName(getPortName(sourcePortInstance, sourceServiceInstance));
            OutputPortInstance sourcePort = application.getServiceInstance(sourceServiceInstance).getOutputPortInstance(sourcePortInstance);

            int type = sourcePort.getDestinationType();
            sourceConfiguration.setPortType(type == PortInstance.DESTINATION_TYPE_QUEUE ?
                    JMSPortConfiguration.PortType.QUEUE : JMSPortConfiguration.PortType.TOPIC);

            String destPortInstance = route.getTargetPortInstance();
            JMSPortConfiguration destinationConfiguration = new JMSPortConfiguration();
            String targetServiceInstance = route.getTargetServiceInstance();
            destinationConfiguration.setName(getPortName(destPortInstance, targetServiceInstance));
            InputPortInstance targetPort = application.getServiceInstance(targetServiceInstance).getInputPortInstance(destPortInstance);
            int inputPortInstanceDestinationType = targetPort.getDestinationType();
            destinationConfiguration.setPortType(inputPortInstanceDestinationType == PortInstance.DESTINATION_TYPE_QUEUE ?
                    JMSPortConfiguration.PortType.QUEUE : JMSPortConfiguration.PortType.TOPIC);
            JMSRouteConfiguration routeConfiguration = new JMSRouteConfiguration(sourceConfiguration, destinationConfiguration, route.getJMSSelector());

            MessageCreationConfiguration messageCreationConfiguration = new MessageCreationConfiguration();
            messageCreationConfiguration.setTransportService(transport);
            messageCreationConfiguration.setRouteOperationType(RouteOperationType.MESSAGE_CREATE);
            routeConfiguration.getRouteOperationConfigurations().add(messageCreationConfiguration);

            CarryForwardContextConfiguration srcCFC = new CarryForwardContextConfiguration();
            srcCFC.setApplication(application);
            srcCFC.setPortInstance(sourcePort);
            srcCFC.setServiceInstanceName(sourceServiceInstance);
            srcCFC.setRouteOperationType(RouteOperationType.SRC_CARRY_FORWARD_CONTEXT);
            routeConfiguration.getRouteOperationConfigurations().add(srcCFC);

            Transformation applicationContextTransformation = sourcePort.getApplicationContextTransformation();
            if(applicationContextTransformation != null) {
                TransformationConfiguration transformationConfiguration = new TransformationConfiguration();
                transformationConfiguration.setXsl(applicationContextTransformation.getScript());
                transformationConfiguration.setTransformerType(applicationContextTransformation.getFactory());
                transformationConfiguration.setJmsXsl(applicationContextTransformation.getJMSScript());
                transformationConfiguration.setRouteOperationType(RouteOperationType.APP_CONTEXT_TRANSFORM);
                routeConfiguration.getRouteOperationConfigurations().add(transformationConfiguration);
            }

            if(route.getSenderSelector()!=null){
                SenderSelectorConfiguration senderSelectorConfiguration = new SenderSelectorConfiguration();
                senderSelectorConfiguration.setSourceName(route.getSenderSelector());
                senderSelectorConfiguration.setAppName_version(application.getGUID() + ":" + application.getVersion());
                senderSelectorConfiguration.setRouteOperationType(RouteOperationType.SENDER_SELECTOR);
                routeConfiguration.getRouteOperationConfigurations().add(senderSelectorConfiguration);
            }

            if(route.getApplicationContextSelector() != null) {
                XmlSelectorConfiguration appContextSelectorConfig = new XmlSelectorConfiguration("AppContext");
                appContextSelectorConfig.setXpath(route.getApplicationContextSelector().getXPath());
                appContextSelectorConfig.setNsPrefixMap(route.getApplicationContextSelector().getNamespaces());
                appContextSelectorConfig.setRouteOperationType(RouteOperationType.APP_CONTEXT_XML_SELECTOR);
                routeConfiguration.getRouteOperationConfigurations().add(appContextSelectorConfig);
            }

            if(route.getBodySelector() != null) {
                XmlSelectorConfiguration bodySelectorConfig = new XmlSelectorConfiguration("Body");
                bodySelectorConfig.setXpath(route.getBodySelector().getXPath());
                bodySelectorConfig.setNsPrefixMap(route.getBodySelector().getNamespaces());
                bodySelectorConfig.setRouteOperationType(RouteOperationType.BODY_XML_SELECTOR);
                routeConfiguration.getRouteOperationConfigurations().add(bodySelectorConfig);
            }

            if(route.getMessageTransformation()!=null) {
                TransformationConfiguration transformationConfiguration = new TransformationConfiguration();
                transformationConfiguration.setXsl(route.getMessageTransformation().getScript());
                transformationConfiguration.setTransformerType(route.getMessageTransformation().getFactory());
                transformationConfiguration.setJmsXsl(route.getMessageTransformation().getJMSScript());
                transformationConfiguration.setRouteOperationType(RouteOperationType.ROUTE_TRANSFORM);
                routeConfiguration.getRouteOperationConfigurations().add(transformationConfiguration);
            }

            CarryForwardContextConfiguration targetCFC = new CarryForwardContextConfiguration();
            targetCFC.setApplication(application);
            targetCFC.setPortInstance(targetPort);
            targetCFC.setServiceInstanceName(targetServiceInstance);
            targetCFC.setRouteOperationType(RouteOperationType.TGT_CARRY_FORWARD_CONTEXT);
            routeConfiguration.getRouteOperationConfigurations().add(targetCFC);

            com.fiorano.openesb.route.Route route1 = routeService.createRoute(routeConfiguration);
            route1.start();
            routeMap.put(route.getName(), route1);
        }
    }

    private String getPortName(String portInstance, String sourceServiceInstance) {
        return LookUpUtil.getServiceInstanceLookupName(appGUID, version, sourceServiceInstance) + Constants.NAME_DELIMITER + portInstance;
    }

    public void startAllMicroServices() {
        logger.info("Starting all micro services of the Application "+appGUID+":"+version);
        for (ServiceInstance instance : application.getServiceInstances()) {
            try {
                startMicroService(instance.getName());
            } catch (FioranoException e) {
                logger.error("Error occured while starting the Service: " + instance.getName()+" of Application: " +appGUID +":"+version, e);
            }
        }
        logger.info("Started all micro services of the Application "+appGUID+":"+version);
    }

    public void stopApplication() {
        for(ServiceInstance serviceInstance : application.getServiceInstances()) {
            for(PortInstance portInstance : serviceInstance.getInputPortInstances()) {
                JMSPortConfiguration portConfiguration = getPortConfiguration(serviceInstance, portInstance);
                try {
                    transport.disablePort(portConfiguration);
                } catch (Exception e) {
                    logger.error("Error occured while disabling the port: " + portConfiguration.getName()+" of Application: " +appGUID +":"+version, e);
                }
            }
            for(PortInstance portInstance : serviceInstance.getOutputPortInstances()) {
                JMSPortConfiguration portConfiguration = getPortConfiguration(serviceInstance, portInstance);
                try {
                    transport.disablePort(portConfiguration);
                } catch (Exception e) {
                    logger.error("Error occured while disabling the port: " + portConfiguration.getName() + " of Application: " + appGUID + ":" + version, e);
                }
            }
        }
        try {
            stopAllMicroServices();
        } catch (FioranoException e) {
            logger.error("Error occured while stopping microservices of the Application: "+appGUID +":"+version, e);
        }
        for(String routeName :routeMap.keySet()) {
            try {
                Route route = routeMap.get(routeName);
                route.stop();
            } catch (Exception e) {
                logger.error("Error occured while stoping the route: " + routeName+" of Application: " +appGUID +":"+version, e);
            }
        }
    }

    private JMSPortConfiguration getPortConfiguration(ServiceInstance serviceInstance, PortInstance portInstance) {
        JMSPortConfiguration portConfiguration = new JMSPortConfiguration();
        int type = portInstance.getDestinationType();
        portConfiguration.setPortType(type == PortInstance.DESTINATION_TYPE_QUEUE ?
                JMSPortConfiguration.PortType.QUEUE : JMSPortConfiguration.PortType.TOPIC);
        portConfiguration.setName(getPortName(portInstance.getName(), serviceInstance.getName()));
        return portConfiguration;
    }

    public BreakpointMetaData addBreakPoint(String routeName) throws Exception {
        com.fiorano.openesb.route.Route route = routeMap.get(routeName);
        if(route==null){
            throw new FioranoException("Route with name: "+routeName+" does not exist in the Application: " + application.getGUID());
        }
        String bpSourceDestName = application.getGUID() + "__" + application.getVersion() + routeName + "__C";
        String bpTargetdDestName = application.getGUID() + "__" + application.getVersion() + routeName + "__D";
        com.fiorano.openesb.application.application.Route routePS=null;
        for(final com.fiorano.openesb.application.application.Route rPS: application.getRoutes()) {
            if(rPS.getName().equals(routeName)){
                routePS = rPS;
                break;
            }
        }

        //create route from Outport to C and start
        JMSPortConfiguration outPortConfiguration = new JMSPortConfiguration();
        String outPortName = routePS.getSourcePortInstance();
        outPortConfiguration.setName((outPortName));
        OutputPortInstance outPortInstnace = application.getServiceInstance(routePS.getSourceServiceInstance()).getOutputPortInstance(outPortName);
        int portType = outPortInstnace.getDestinationType();
        outPortConfiguration.setPortType(portType == PortInstance.DESTINATION_TYPE_QUEUE ?
                JMSPortConfiguration.PortType.QUEUE : JMSPortConfiguration.PortType.TOPIC);

        JMSPortConfiguration tgtCConfiguration = new JMSPortConfiguration();
        tgtCConfiguration.setName(bpSourceDestName);
        tgtCConfiguration.setPortType(JMSPortConfiguration.PortType.QUEUE);

        JMSRouteConfiguration routeToCConfiguration = new JMSRouteConfiguration(outPortConfiguration, tgtCConfiguration, routePS.getJMSSelector());

        MessageCreationConfiguration messageCreationConfiguration = new MessageCreationConfiguration();
        messageCreationConfiguration.setTransportService(transport);
        messageCreationConfiguration.setRouteOperationType(RouteOperationType.MESSAGE_CREATE);
        routeToCConfiguration.getRouteOperationConfigurations().add(messageCreationConfiguration);

        CarryForwardContextConfiguration srcCFC = new CarryForwardContextConfiguration();
        srcCFC.setApplication(application);
        srcCFC.setPortInstance(outPortInstnace);
        srcCFC.setServiceInstanceName(outPortName);
        srcCFC.setRouteOperationType(RouteOperationType.SRC_CARRY_FORWARD_CONTEXT);
        routeToCConfiguration.getRouteOperationConfigurations().add(srcCFC);

        Transformation applicationContextTransformation = outPortInstnace.getApplicationContextTransformation();
        if(applicationContextTransformation != null) {
            TransformationConfiguration transformationConfiguration = new TransformationConfiguration();
            transformationConfiguration.setXsl(applicationContextTransformation.getScript());
            transformationConfiguration.setTransformerType(applicationContextTransformation.getFactory());
            transformationConfiguration.setJmsXsl(applicationContextTransformation.getJMSScript());
            transformationConfiguration.setRouteOperationType(RouteOperationType.APP_CONTEXT_TRANSFORM);
            routeToCConfiguration.getRouteOperationConfigurations().add(transformationConfiguration);
        }

        if(routePS.getSenderSelector()!=null){
            SenderSelectorConfiguration senderSelectorConfiguration = new SenderSelectorConfiguration();
            senderSelectorConfiguration.setSourceName(routePS.getSenderSelector());
            senderSelectorConfiguration.setAppName_version(application.getGUID() + ":" + application.getVersion());
            senderSelectorConfiguration.setRouteOperationType(RouteOperationType.SENDER_SELECTOR);
            routeToCConfiguration.getRouteOperationConfigurations().add(senderSelectorConfiguration);
        }

        if(routePS.getApplicationContextSelector() != null) {
            XmlSelectorConfiguration appContextSelectorConfig = new XmlSelectorConfiguration("AppContext");
            appContextSelectorConfig.setXpath(routePS.getApplicationContextSelector().getXPath());
            appContextSelectorConfig.setNsPrefixMap(routePS.getApplicationContextSelector().getNamespaces());
            appContextSelectorConfig.setRouteOperationType(RouteOperationType.APP_CONTEXT_XML_SELECTOR);
            routeToCConfiguration.getRouteOperationConfigurations().add(appContextSelectorConfig);
        }

        if(routePS.getBodySelector() != null) {
            XmlSelectorConfiguration bodySelectorConfig = new XmlSelectorConfiguration("Body");
            bodySelectorConfig.setXpath(routePS.getBodySelector().getXPath());
            bodySelectorConfig.setNsPrefixMap(routePS.getBodySelector().getNamespaces());
            bodySelectorConfig.setRouteOperationType(RouteOperationType.BODY_XML_SELECTOR);
            routeToCConfiguration.getRouteOperationConfigurations().add(bodySelectorConfig);
        }

        if(routePS.getMessageTransformation()!=null) {
            TransformationConfiguration transformationConfiguration = new TransformationConfiguration();
            transformationConfiguration.setXsl(routePS.getMessageTransformation().getScript());
            transformationConfiguration.setTransformerType(routePS.getMessageTransformation().getFactory());
            transformationConfiguration.setJmsXsl(routePS.getMessageTransformation().getJMSScript());
            transformationConfiguration.setRouteOperationType(RouteOperationType.ROUTE_TRANSFORM);
            routeToCConfiguration.getRouteOperationConfigurations().add(transformationConfiguration);
        }

        com.fiorano.openesb.route.Route routeToC = routeService.createRoute(routeToCConfiguration);
        routeToC.start();
        breakPointRoutes.put(bpSourceDestName, routeToC);
        //create route from D to inport and start
        JMSPortConfiguration inPortConfiguration = new JMSPortConfiguration();
        String inPortName = routePS.getTargetPortInstance();
        inPortConfiguration.setName(inPortName);
        InputPortInstance inPortInstnace = application.getServiceInstance(routePS.getTargetServiceInstance()).getInputPortInstance(inPortName);
        int inPortType = inPortInstnace.getDestinationType();
        inPortConfiguration.setPortType(inPortType == PortInstance.DESTINATION_TYPE_QUEUE ?
                JMSPortConfiguration.PortType.QUEUE : JMSPortConfiguration.PortType.TOPIC);

        JMSPortConfiguration srcDConfiguration = new JMSPortConfiguration();
        tgtCConfiguration.setName(bpTargetdDestName);
        tgtCConfiguration.setPortType(JMSPortConfiguration.PortType.QUEUE);

        JMSRouteConfiguration routeFromDConfiguration = new JMSRouteConfiguration(srcDConfiguration, inPortConfiguration, null);
        CarryForwardContextConfiguration targetCFC = new CarryForwardContextConfiguration();
        targetCFC.setApplication(application);
        targetCFC.setPortInstance(inPortInstnace);
        targetCFC.setServiceInstanceName(routePS.getTargetServiceInstance());
        targetCFC.setRouteOperationType(RouteOperationType.TGT_CARRY_FORWARD_CONTEXT);
        routeFromDConfiguration.getRouteOperationConfigurations().add(targetCFC);
        com.fiorano.openesb.route.Route routeFromD = routeService.createRoute(routeToCConfiguration);
        routeFromD.start();
        breakPointRoutes.put(bpTargetdDestName, routeFromD);
        //stop original route
        route.stop();

        BreakpointMetaData breakpointMetaData = new BreakpointMetaData();
        breakpointMetaData.setConnectionProperties(TransportConfig.getInstance().getConnectionProperties());
        breakpointMetaData.setSourceQName(bpSourceDestName);
        breakpointMetaData.setTargetQName(bpTargetdDestName);
        breakpoints.put(routeName, breakpointMetaData);
        ApplicationEventRaiser.generateRouteEvent(ApplicationEvent.ApplicationEventType.ROUTE_BP_ADDED, Event.EventCategory.INFORMATION, appGUID, application.getDisplayName(), String.valueOf(version), routeName, "Successfully added breakpoint to the Route");
        return breakpointMetaData;
    }

    public void removeBreakPoint(String routeName) throws Exception{
        com.fiorano.openesb.route.Route route = routeMap.get(routeName);
        route.start();
        //remove breakpoint routes C and D
        String bpSourceDestName = application.getGUID() + "__" + application.getVersion() + routeName + "__C";
        String bpTargetdDestName = application.getGUID() + "__" + application.getVersion() + routeName + "__D";
        Route routeToC = breakPointRoutes.remove(bpSourceDestName);
        routeToC.stop();
        Route routeFromD = breakPointRoutes.remove(bpTargetdDestName);
        routeFromD.stop();
        ApplicationEventRaiser.generateRouteEvent(ApplicationEvent.ApplicationEventType.ROUTE_BP_REMOVED, Event.EventCategory.INFORMATION, appGUID, application.getDisplayName(), String.valueOf(version), routeName, "Successfully removed breakpoint to the Route");
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public void stopAllMicroServices() throws FioranoException{
        logger.info("Stopping all micro services of the Application "+appGUID+":"+version);
        for(MicroServiceRuntimeHandle handle:microServiceHandleList.values()){
            stopMicroService(handle.getServiceInstName());
        }
        microServiceHandleList = new HashMap<>();
        logger.info("Stopped all micro services of the Application "+appGUID+":"+version);
    }

    public void startMicroService(String microServiceName) throws FioranoException {
        ServiceInstance instance = application.getServiceInstance(microServiceName);
        if(isMicroserviceRunning(microServiceName)){
            logger.info("MicroService: "+ microServiceName + " of Application " + appGUID +":"+version+" is already running");
            return;
        }
        logger.info("Starting MicroService: "+ microServiceName + " of Application " + appGUID +":"+version);
        MicroServiceLaunchConfiguration mslc = new MicroServiceLaunchConfiguration(application.getGUID(), String.valueOf(application.getVersion()), "karaf", "karaf", instance);
        try {
            microServiceHandleList.put(microServiceName, service.launch(mslc, instance.getConfiguration()));
        } catch (Exception e) {
            logger.error("Error occured while starting the Service: " + microServiceName+" of Application: " +appGUID +":"+version, e);
        }
        logger.info("Started MicroService: "+ microServiceName + " of Application " + appGUID +":"+version);
    }

    public void stopMicroService(String microServiceName) throws FioranoException {
        if(!isMicroserviceRunning(microServiceName)){
            logger.warn("Microservice: "+ microServiceName + " of Application " + appGUID +":"+version+" is not running");
            return;
        }
        try {
            logger.info("Stoping MicroService: "+ microServiceName + " of Application " + appGUID +":"+version);
            microServiceHandleList.get(microServiceName).stop();
            microServiceHandleList.remove(microServiceName);
            logger.info("Stopped MicroService: "+ microServiceName + " of Application " + appGUID +":"+version);
        } catch (Exception e) {
            logger.error("Error occured while stopping the Service: " + microServiceName+" of Application: " +appGUID +":"+version, e);
        }
    }

    public boolean isMicroserviceRunning(String microServiceName) {
        return microServiceHandleList.containsKey(microServiceName);
    }

    public String getLaunchMode(String name) {
        return isMicroserviceRunning(name) ? microServiceHandleList.get(name).getLaunchMode().name() : "Not Running";
    }


    public ApplicationStateDetails getApplicationDetails() throws FioranoException {


       // logger.debug(Bundle.class, Bundle.EXECUTING_CALL, "getApplicationDetails()");

        ApplicationStateDetails appDetails = new ApplicationStateDetails();

        appDetails.setAppGUID(appGUID);
        appDetails.setAppVersion(String.valueOf(application.getVersion()));
        appDetails.setKillTime(killTime);
        appDetails.setLaunchTime(launchTime);
        appDetails.setApplicationLabel(environmentLabel);

        List<ServiceInstance> serviceInstances = application.getServiceInstances();
            for (ServiceInstance serviceInstance: serviceInstances) {
                String serviceName = serviceInstance.getName();
                ServiceInstanceStateDetails stateDetails;
                MicroServiceRuntimeHandle serviceHandle = microServiceHandleList.get(serviceName);
                if (serviceHandle == null){
                    stateDetails = new ServiceInstanceStateDetails();
                    stateDetails.setServiceGUID(serviceInstance.getGUID());
                    stateDetails.setServiceInstanceName(serviceName);
                    stateDetails.setRunningVersion(String.valueOf(serviceInstance.getVersion()));
                    stateDetails.setStatusString(EventStateConstants.SERVICE_HANDLE_UNBOUND);
                }else{
                    stateDetails = serviceHandle.getServiceStateDetails();
                    String exceptionTrace = serviceHandle.getExceptionTrace();
                    if (exceptionTrace != null)
                        appDetails.addServiceExceptionTrace(serviceName, exceptionTrace);
                }
                appDetails.addServiceStatus(serviceName, stateDetails);
            }

        //  Get the details of External Services too.

        for (Object o : application.getRemoteServiceInstances()) {
            RemoteServiceInstance extInstance = (RemoteServiceInstance) o;
            String extAppGUID = extInstance.getApplicationGUID();
            ApplicationHandle extAppHandle = applicationController.getApplicationHandle(extAppGUID, extInstance.getApplicationVersion());

            if (extAppHandle == null) {
               // logger.error(Bundle.class, Bundle.APPHANDLE_NOT_PRESENT, appGUID+ITifosiConstants.APP_VERSION_DELIM+Float.toString(application.getVersion()));
                continue;
            }

            // If the External service is actually configured to a application of version different of that
            // of the one which is running. then do not proceed.
            if (extAppHandle.getApplication().getVersion() != extInstance.getApplicationVersion())
                continue;

            String extInstName = extInstance.getRemoteName();
            MicroServiceRuntimeHandle extServiceHandle = extAppHandle.getMicroServiceHandle(extInstName);

            if (extServiceHandle == null)
                continue;

            String localInstName = extInstance.getName();
            ServiceInstanceStateDetails stateDetails = extServiceHandle.getServiceStateDetails();

            appDetails.addServiceStatus(localInstName, stateDetails);

            String exceptionTrace = extServiceHandle.getExceptionTrace();

            if (exceptionTrace != null)
                appDetails.addServiceExceptionTrace(localInstName, exceptionTrace);
        }

        // add the routes with breakpoint to app state details

        if (breakpoints != null && breakpoints.size() > 0) {
            for (String aDebugrouteGUIDS : breakpoints.keySet()) {
                appDetails.addDebugRoute(aDebugrouteGUIDS);
            }
        }

        if (pendingBreakpointsForClouser != null && pendingBreakpointsForClouser.size() > 0) {
            for (String pendingDebugRouteGUID : pendingBreakpointsForClouser.keySet()) {
                appDetails.addPendingDebugRoutesForClosure(pendingDebugRouteGUID);
            }
        }
        return appDetails;
    }

    private MicroServiceRuntimeHandle getMicroServiceHandle(String serviceName){
        return microServiceHandleList.get(serviceName);
    }

    public void synchronizeApplication(Application newApplication) throws FioranoException {
        Application oldApplication = this.application;

        // kill service which no longer remain as part of the ep
        killDiscontinuedServices(newApplication);

        //launch or modify the rest of the services
        logger.debug("launching the applicaiton with new properties");
        this.application = newApplication;
        try {
            createRoutes();
        } catch (Exception e) {
            throw new FioranoException(e);
        }
        startAllMicroServices();

        //  Update routes for all remaining services. This should remove extra routes and add new routes and UPDATE existing route configuration.
        logger.debug("syncing routes");
        try {
            synchronizeRoutes();
        } catch (Exception e) {
            throw new FioranoException(e);
        }


       /* // remove the debug routes for components that no longer exist
        if (oldApplication != null) {
            appHandle.removeNonExistingRoutes(oldApplication, handleID);
            // All the debug routes will be updated
            debugHandler.synchronizeDebugRoutes(appHandle);
            Map<String, BreakpointMetaData> allPendingBreakpoints = appHandle.getPendingBreakpointsForClouser();
            List<String> routes = new ArrayList<String>(allPendingBreakpoints.keySet());
            for (String routeGUID : routes) {
                String handleId = appHandle.getHandleID(routeGUID);
                removeBreakpoint(routeGUID, handleId);
            }
        }*/
    }

    /**
     * kill all the extra services that are running on this TPS but which are
     * not part of the new ApplicationLaunchPacket
     *
     * @param alp new application launch packet
     * @throws FioranoException If an exception occurs
     */
    private void killDiscontinuedServices(Application alp) throws FioranoException {
        // set this to all running components initially
        Set<String> toBeKilledComponents = new HashSet<String>();
        for (String serviecName:microServiceHandleList.keySet()) {
            toBeKilledComponents.add(serviecName);
        }

        Set<String> tobeRunningComponents = new HashSet<String>();
        for (ServiceInstance serv : alp.getServiceInstances()) {
            tobeRunningComponents.add(serv.getName());
        }

        toBeKilledComponents.removeAll(tobeRunningComponents);
        for (String killcomp : toBeKilledComponents) {
            MicroServiceRuntimeHandle handle=null;
            try {
                handle = microServiceHandleList.get(killcomp);
                if (handle != null) {
                    handle.stop();  /*  Bugzilla � Bug 18550 , making call to killComponent() ,which will take care of deleting the route first and then kill component.  */
                }
            } catch (Exception e) {
                logger.error("error occured while stopping the component " + handle.getServiceInstName());
            }
        }
    }

    public void synchronizeRoutes() throws Exception {
        Collection<Route> toDelete = new ArrayList<Route>();
        for (Route route : routeMap.values())
            if (!checkForRouteExistanceAndUpdateRoute(route))
                toDelete.add(route);
        for (Route route : toDelete) {
            route.stop();
            route.delete();
        }
    }

    private boolean checkForRouteExistanceAndUpdateRoute(Route rInfo) {
        boolean found = false;
        String srcPortName = rInfo.getSourceDestinationName();
        String tgtPortName = rInfo.getTargetDestinationName();

        List<com.fiorano.openesb.application.application.Route> routes = application.getRoutes();
        for (com.fiorano.openesb.application.application.Route route : routes) {
            if(rInfo.getSourceDestinationName().equals(route.getSourcePortInstance())
                    && rInfo.getTargetDestinationName().equals(route.getTargetPortInstance())){
                found = true;
                break;
            }
        }
        return found;
    }

    public BreakpointMetaData getBreakpointMetaData(String routeName) {
        return breakpoints.get(routeName);
    }

    public String[] getRoutesWithDebugger() {
        return breakpoints.keySet().toArray(new String[breakpoints.size()]);
    }

    public void removeAllBreakpoints() throws Exception {
        Set<String> routesWithBreakPoint = breakpoints.keySet();
        for(String routeName: routesWithBreakPoint){
            removeBreakPoint(routeName);
        }
    }

    public void changeRouteOperationHandler(String routeGUID, RouteOperationConfiguration configuration) throws Exception {
        com.fiorano.openesb.route.Route route = routeMap.get(routeGUID);
        if(route==null){
            throw new FioranoException("route: "+routeGUID+" does not exists in the Application "+appGUID+":"+version);
        }
        route.modifyHandler(configuration);
    }
}
