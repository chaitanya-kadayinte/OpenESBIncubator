package com.fiorano.openesb.applicationcontroller;

import com.fiorano.openesb.application.ApplicationRepository;
import com.fiorano.openesb.application.application.*;
import com.fiorano.openesb.microservice.ccp.CCPEventManager;
import com.fiorano.openesb.microservice.ccp.IEventListener;
import com.fiorano.openesb.microservice.ccp.event.CCPEventType;
import com.fiorano.openesb.microservice.ccp.event.ComponentCCPEvent;
import com.fiorano.openesb.microservice.ccp.event.ControlEvent;
import com.fiorano.openesb.microservice.ccp.event.common.DataEvent;
import com.fiorano.openesb.microservice.ccp.event.common.DataRequestEvent;
import com.fiorano.openesb.microservice.ccp.event.common.data.Data;
import com.fiorano.openesb.microservice.ccp.event.common.data.MicroserviceConfiguration;
import com.fiorano.openesb.microservice.launch.impl.MicroServiceLauncher;
import com.fiorano.openesb.route.RouteConfiguration;
import com.fiorano.openesb.route.RouteService;
import com.fiorano.openesb.transport.TransportService;
import com.fiorano.openesb.utils.exception.FioranoException;
import com.fiorano.openesb.security.SecurityManager;
import org.osgi.framework.BundleContext;

import java.io.File;
import java.util.*;

public class ApplicationController {
    private TransportService transport;
    private ApplicationRepository applicationRepository;
    private MicroServiceLauncher microServiceLauncher;
    private Map<String, ApplicationHandle> applicationHandleMap = new HashMap<>();
    private RouteService routeService;
    private SecurityManager securityManager;

    public ApplicationController(ApplicationRepository applicationRepository, BundleContext context) throws Exception {
        this.applicationRepository = applicationRepository;
        routeService = context.getService(context.getServiceReference(RouteService.class));
        microServiceLauncher = context.getService(context.getServiceReference(MicroServiceLauncher.class));
        CCPEventManager ccpEventManager = context.getService(context.getServiceReference(CCPEventManager.class));
        registerConfigRequestListener(ccpEventManager);
        transport = context.getService(context.getServiceReference(TransportService.class));
        securityManager = context.getService(context.getServiceReference(SecurityManager.class));
    }

    private void registerConfigRequestListener(final CCPEventManager ccpEventManager) throws Exception {
        ccpEventManager.registerListener(new IEventListener() {
            @Override
            public void onEvent(ComponentCCPEvent event) throws Exception {
                ControlEvent controlEvent = event.getControlEvent();

                if(controlEvent instanceof DataRequestEvent && controlEvent.isReplyNeeded()) {
                    for(DataRequestEvent.DataIdentifier request: ((DataRequestEvent) controlEvent).getDataIdentifiers()) {
                        if(request == DataRequestEvent.DataIdentifier.COMPONENT_CONFIGURATION) {
                            DataEvent dataEvent = new DataEvent();
                            MicroserviceConfiguration microserviceConfiguration = new MicroserviceConfiguration();
                            Application application = applicationRepository.readApplication(getAppName(event), getAppVersion(event));
                            String configuration = application.getServiceInstance(getInstanceName(event)).getConfiguration();
                            microserviceConfiguration.setConfiguration(configuration);
                            Map<DataRequestEvent.DataIdentifier,Data> data = new HashMap<>();
                            data.put(request,microserviceConfiguration);
                            dataEvent.setData(data);
                            ccpEventManager.getCcpEventGenerator().sendEvent(dataEvent,event.getComponentId());
                            break;
                        }
                    }
                }
            }

            @Override
            public String getId() {
                return "Components' Requests Listener";
            }
        }, CCPEventType.DATA_REQUEST);
    }

    private String getAppName(ComponentCCPEvent event) {
        String componentId = event.getComponentId();
        return componentId.substring(0, componentId.indexOf("__"));
    }
    private String getAppVersion(ComponentCCPEvent event) {
        String componentId = event.getComponentId();
        return componentId.substring(componentId.indexOf("__") + 2,componentId.lastIndexOf("__")).replace("_", ".");
    }
    private String getInstanceName(ComponentCCPEvent event) {
        String componentId = event.getComponentId();
        return componentId.substring(componentId.lastIndexOf("__") + 2);
    }

    public void saveApplication(File appFileFolder, String handleID, byte[] zippedContents) throws FioranoException {
        String userName = securityManager.getUserName(handleID);
        System.out.println("saving Application");
        Application application = null;
        application = ApplicationParser.readApplication(appFileFolder, Application.Label.none.toString(), false, false);
        try {
            application.validate();
        } catch (FioranoException e3){
            //this would led some corrupted application to enter into the repository, which could be deleted
            e3.printStackTrace();
            //we can fail this step if we want
        }
        String appGuid = application.getGUID();
        float version = application.getVersion();
        // boolean applicationExists = applicationRepository.applicationExists(appGuid, version);
        applicationRepository.saveApplication(application, appFileFolder, userName, zippedContents, handleID);
    }

    public void saveApplication(Application application, boolean skipManagableProps, String handleID) throws FioranoException {
        String userName = securityManager.getUserName(handleID);
        boolean applicationExists = applicationRepository.applicationExists(application.getGUID(), application.getVersion());
        boolean applicationAnyVersionExists=applicationRepository.applicationExists(application.getGUID(),-1);

        Application oldApp = applicationRepository.readApplication(application.getGUID(), String.valueOf(application.getVersion()));

            try {
                applicationRepository.saveApplication(application, userName, handleID, skipManagableProps);
            } catch (FioranoException e) {
                throw e;
            }

        if (oldApp != null) {
            Vector<String> deletedComponents = new Vector<String>();
            Vector<String> deletedConfigComponents = new Vector<String>();
            HashMap<String, String> deletedPorts = new HashMap<String, String>();

            for (Object o1 : oldApp.getServiceInstances()) {
                boolean check = false;
                ServiceInstance oldInst = (ServiceInstance) o1;

                for (Object o : application.getServiceInstances()) {
                    ServiceInstance newInst = (ServiceInstance) o;
                    if (oldInst.getName().equals(newInst.getName())) {
                        check = false;

                        List<OutputPortInstance> oldOutputPortInstances = oldInst.getOutputPortInstances();
                        for (OutputPortInstance oldPort : oldOutputPortInstances) {
                            boolean deletePort = false;

                            List<OutputPortInstance> newOutputPortInstances = newInst.getOutputPortInstances();
                            for (OutputPortInstance newPort : newOutputPortInstances) {
                                if (oldPort.getName().equals(newPort.getName())) {
                                    deletePort = false;
                                    break;
                                } else {
                                    deletePort = true;
                                }
                            }

                            if (newInst.getOutputPortInstances().size() == 0)
                                deletePort = true;

                            if (deletePort)
                                deletedPorts.put(oldInst.getName(), oldPort.getName());
                        }

                        break;
                    } else {
                        check = true;
                    }
                }

                if (application.getServiceInstances().size() == 0) {
                    check = true;
                }

                if (check) {
                    deletedConfigComponents.add(oldInst.getName());
                    String[] nodes = oldInst.getNodes();
                    if (nodes != null)
                        deletedComponents.add(oldInst.getName());
                }
            }

            //Bug 16340
            if (!deletedConfigComponents.isEmpty())
                deleteConfigurations(oldApp, deletedConfigComponents);

            //Fix for bug 20434
            if (!deletedComponents.isEmpty()) {
                deleteInPort(oldApp, deletedConfigComponents);
            }

            if (!deletedPorts.isEmpty())
                deletePortTransformations(oldApp, deletedPorts);

            Vector<String> deletedRoutes = new Vector<String>();
            for (Route oldRoute : oldApp.getRoutes()) {
                boolean check = false;

                for (Route newRoute : application.getRoutes()) {
                    if (oldRoute.getName().equals(newRoute.getName())) {
                        check = false;
                        break;
                    } else {
                        check = true;
                    }
                }

                if (application.getRoutes().size() == 0)
                    check = true;

                if (check)
                    deletedRoutes.add(oldRoute.getName());
            }

            if (!deletedRoutes.isEmpty())
                deleteRouteConfigurations(oldApp, deletedRoutes);
        }
        ApplicationHandle appHandle = getApplicationHandle(application.getGUID(), application.getVersion(), handleID);
        if (appHandle != null) {
            appHandle.setApplication(application);
        }
    }

    /**
     * Deletes port transformations for specified ports. This method is used when some of the output ports of a service
     * instance are deleted at the time of orchestration. For example, deleting an output port of CBR component.
     *
     * @param oldApp       Application DMI
     * @param deletedPorts HashMap of (ServiceInstanceName, PortName)
     */
    private void deletePortTransformations(Application oldApp, HashMap<String, String> deletedPorts) {
        //todo
    }

    private void deleteConfigurations(Application oldApp, Vector<String> deletedConfigComponents) {
        //Todo
    }


    /**
     * delete InPort when running components are deleted
     *
     * @param oldApp oldApplication dmi
     * @param deletedComponent deleted components
     */
    private void deleteInPort(Application oldApp, Vector<String> deletedComponent) {
        //todo
    }


    private void deleteRouteConfigurations(Application oldApp, Vector<String> deletedRoutes) {
        //todo
    }

    private void deleteLogs(Application application, Vector<String> components) {
        //todo
    }

    public Set<String> getListOfRunningApplications(String handleId){
        return applicationHandleMap.keySet();
    }

    public boolean launchApplication(String appGuid, String version, String handleID) throws Exception {
        System.out.println("Launching application : " + appGuid + ":" + version);
        Application application = applicationRepository.readApplication(appGuid, version);
        ApplicationHandle appHandle = new ApplicationHandle(application, microServiceLauncher, routeService,transport);
        appHandle.createRoutes();

        appHandle.launchComponents();
        applicationHandleMap.put(getKey(appGuid,version),appHandle);
        System.out.println("Launched application: "+appGuid+":"+version);
        return true;
    }

    public boolean stopApplication(String appGuid, String version, String handleID) throws Exception {

        ApplicationHandle applicationHandle = applicationHandleMap.get(getKey(appGuid, version));
        if(applicationHandle!=null){
            applicationHandle.stopApplication();
        }
        return true;
    }

    private String getKey(String appGuid, String version) {
        return appGuid+ ":"+ version;
    }

    public boolean synchronizeApplication(String appGuid, String version, String handleID){
        return false;
    }

    public boolean startAllMicroServices(String appGuid, String version, String handleID){
        return false;
    }

    public boolean stopAllMicroServices(String appGuid, String version, String handleID){

        return false;
    }

    public boolean startMicroService(String appGuid, String version, String microServiceName, String handleID){

        return false;
    }

    public boolean stopMicroService(String appGuid, String version, String microServiceName, String handleID){
        return false;
    }

    public void deleteApplication(String appGUID, String version, String handleID) throws FioranoException {
        if(applicationHandleMap.containsKey(appGUID+"__"+version)){
            throw new FioranoException("Cannot delete running Application. Stop the Application and then delete");
        }
        applicationRepository.deleteApplication(appGUID, version);
    }

    public ApplicationHandle getApplicationHandle(String appGUID, float appVersion, String handleID) {
        return applicationHandleMap.get(appGUID+"__"+appVersion);
    }
    public boolean isApplicationRunning(String appGUID, float version, String handleID) throws FioranoException {
        return (getApplicationHandle(appGUID, version, handleID) != null);
    }

    public void changePortAppContext(String appGUID, float appVersion, String serviceName, String portName, String scriptContent, String jmsScriptContent, String transformerType, String projectContent, String handleId) throws FioranoException{


    }

    public void changePortAppContextConfiguration(String appGUID, float appVersion, String serviceName, String portName, String configurationName, String handleId) throws FioranoException{

    }

    public void changeRouteTransformation(String appGUID, float appVersion, String routeGUID, String scriptContent, String jmsScriptContent, String transformerType, String projectContent, String handleId) throws FioranoException{
    }

    public void changeRouteTransformationConfiguration(String appGUID, float appVersion, String routeGUID, String configurationName, String handleId) throws FioranoException{
    }

    public Enumeration<ApplicationReference> getHeadersOfRunningApplications(String handleId) throws FioranoException{
        Vector<ApplicationReference> toReturn = new Vector<ApplicationReference>();
        // get the running application handles and fetch the application info packet from the handles.
        for (ApplicationHandle appHandle:applicationHandleMap.values()) {

            try {
                toReturn.addElement(new ApplicationReference(appHandle.getApplication()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return toReturn.elements();
    }
}
