package com.fiorano.openesb.applicationcontroller;

import com.fiorano.openesb.application.ApplicationRepository;
import com.fiorano.openesb.application.application.Application;
import com.fiorano.openesb.application.application.ApplicationParser;
import com.fiorano.openesb.microservice.launch.impl.MicroServiceLauncher;
import com.fiorano.openesb.route.RouteConfiguration;
import com.fiorano.openesb.route.RouteService;
import com.fiorano.openesb.utils.exception.FioranoException;
import com.fiorano.openesb.security.SecurityManager;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationController {
    private ApplicationRepository applicationRepository;
    private MicroServiceLauncher microServiceLauncher;
    private Map<String, ApplicationHandle> applicationHandleMap = new HashMap<>();
    private RouteService<RouteConfiguration> routeService;
    private SecurityManager securityManager;

    ApplicationController(ApplicationRepository applicationRepository, MicroServiceLauncher microServiceLauncher, RouteService<RouteConfiguration> routeService, SecurityManager securityManager){
        this.applicationRepository = applicationRepository;
        this.microServiceLauncher = microServiceLauncher;
        this.routeService = routeService;
        this.securityManager = securityManager;
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
        ApplicationHandle appHandle = new ApplicationHandle(application, microServiceLauncher, routeService);
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
