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
    ApplicationRepository applicationRepository;
    private MicroServiceLauncher microServiceLauncher;
    Map<String, ApplicationHandle> applicationHandleMap = new HashMap<String, ApplicationHandle>();
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
        System.out.println("launching application: " + appGuid + ":" + version);
        Application application = applicationRepository.readApplication(appGuid, version);
        ApplicationHandle appHandle = new ApplicationHandle(application, microServiceLauncher, routeService);
        appHandle.createMicroServiceHandles();
        appHandle.createRoutes();
        appHandle.launchComponents();
        System.out.println("launched application: "+appGuid+":"+version);
        return true;
    }

    public boolean stopApplication(String appGuid, String version) throws Exception {

        ApplicationHandle applicationHandle = applicationHandleMap.get(appGuid+ ""+ version );
        if(applicationHandle!=null){
            applicationHandle.stopApplication();
        }
        return true;
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
