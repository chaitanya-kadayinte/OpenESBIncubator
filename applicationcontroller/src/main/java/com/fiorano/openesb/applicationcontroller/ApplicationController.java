package com.fiorano.openesb.applicationcontroller;

import com.fiorano.openesb.application.ApplicationRepository;
import com.fiorano.openesb.application.application.Application;
import com.fiorano.openesb.microservice.launch.impl.MicroServiceLauncher;
import com.fiorano.openesb.route.RouteConfiguration;
import com.fiorano.openesb.route.RouteService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationController {
    private ApplicationRepository applicationRepository;
    private MicroServiceLauncher microServiceLauncher;
    private Map<String, ApplicationHandle> applicationHandleMap = new HashMap<>();
    private RouteService<RouteConfiguration> routeService;

    ApplicationController(ApplicationRepository applicationRepository, MicroServiceLauncher microServiceLauncher, RouteService<RouteConfiguration> routeService){
        this.applicationRepository = applicationRepository;
        this.microServiceLauncher = microServiceLauncher;
        this.routeService = routeService;
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




}
