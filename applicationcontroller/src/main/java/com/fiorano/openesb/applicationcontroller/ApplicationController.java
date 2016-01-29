package com.fiorano.openesb.applicationcontroller;

import com.fiorano.openesb.application.ApplicationRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Janardhan on 1/8/2016.
 */
public class ApplicationController {
    ApplicationRepository applicationRepository;

    ApplicationController(ApplicationRepository applicationRepository){
        this.applicationRepository = applicationRepository;
    }

    public List<String> listApplications(){
        return applicationRepository.listApplications();
    }

    public boolean launchApplication(String appGuid, String version){
        System.out.println("launched application: "+appGuid+":"+version);
        return true;
    }

    public boolean stopApplication(String appGuid, String version){

        return false;
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
