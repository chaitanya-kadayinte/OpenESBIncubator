package com.fiorano.openesb.application;

import com.fiorano.openesb.application.application.Application;
import com.fiorano.openesb.application.application.ApplicationParser;
import com.fiorano.openesb.utils.exception.FioranoException;

import java.io.File;
import java.util.List;

/**
 * Created by Janardhan on 1/6/2016.
 */
public class ApplicationRepository {

    public ApplicationRepository(){

    }

    public Application readApplication(String appGuid, String version){
        try {
           return ApplicationParser.readApplication(new File(getApplicationRepoPath()+ File.separator+appGuid+File.separator+version), false);
        } catch (FioranoException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> listApplications() {

        return null;
    }

    public String getApplicationRepoPath(){
        File karafBase = new File(System.getProperty("karaf.base"));
        return karafBase + File.separator + "repository" + File.separator + "applications";
    }
}
