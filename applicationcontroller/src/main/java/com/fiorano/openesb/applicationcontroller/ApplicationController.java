package com.fiorano.openesb.applicationcontroller;

import com.fiorano.openesb.application.ApplicationRepository;
import com.fiorano.openesb.application.application.Application;
import com.fiorano.openesb.application.application.ApplicationParser;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return componentId.substring(0,componentId.indexOf("__"));
    }
    private String getAppVersion(ComponentCCPEvent event) {
        String componentId = event.getComponentId();
        return componentId.substring(componentId.indexOf("__") + 2,componentId.lastIndexOf("__")).replace("_",".");
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

    public List<String> listApplications(){
        return applicationRepository.listApplications();
    }

    public boolean launchApplication(String appGuid, String version) throws Exception {
        System.out.println("Launching application : " + appGuid + ":" + version);
        Application application = applicationRepository.readApplication(appGuid, version);
        ApplicationHandle appHandle = new ApplicationHandle(application, microServiceLauncher, routeService,transport);
        appHandle.createRoutes();
        appHandle.launchComponents();
        applicationHandleMap.put(getKey(appGuid,version),appHandle);
        System.out.println("Launched application: "+appGuid+":"+version);
        return true;
    }

    public boolean stopApplication(String appGuid, String version) throws Exception {

        ApplicationHandle applicationHandle = applicationHandleMap.get(getKey(appGuid, version));
        if(applicationHandle!=null){
            applicationHandle.stopApplication();
        }
        return true;
    }

    private String getKey(String appGuid, String version) {
        return appGuid+ ":"+ version;
    }

    public boolean synchronizeApplication(String appGuid, String version){
        return false;
    }

    public boolean startAllMicroServices(String appGuid, String version){
        return false;
    }

    public boolean stopAllMicroServices(String appGuid, String version){

        return false;
    }

    public boolean startMicroService(String appGuid, String version, String microServiceName){

        return false;
    }

    public boolean stopMicroService(String appGuid, String version, String microServiceName){
        return false;
    }

    public void deleteApplication(String appGUID, String version) throws FioranoException {
        applicationRepository.deleteApplication(appGUID, version);
    }


}
