package com.fiorano.openesb.applicationcontroller;

import com.fiorano.openesb.application.ApplicationRepository;
import com.fiorano.openesb.application.application.Application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Janardhan on 1/8/2016.
 */
public class ApplicationController {
    ApplicationRepository applicationRepository;
    Map<String, ApplicationHandle> applicationHandleMap = new HashMap<String, ApplicationHandle>();

    ApplicationController(ApplicationRepository applicationRepository){
        this.applicationRepository = applicationRepository;
    }

    public List<String> listApplications(){
        return applicationRepository.listApplications();
    }

    public boolean launchApplication(String appGuid, String version){
        System.out.println("launching application: " + appGuid + ":" + version);
        Application application = applicationRepository.readApplication(appGuid, version);
        ApplicationHandle appHandle = new ApplicationHandle(application);
        appHandle.createMicroServiceHandles();
        appHandle.createRoutes();
        appHandle.launchComponents();
        System.out.println("launched application: "+appGuid+":"+version);

        return true;
    }

    public boolean stopApplication(String appGuid, String version){

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
