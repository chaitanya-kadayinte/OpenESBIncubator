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
    String repoPath = "D:\\sources_dec22\\installer\\esb\\server\\repository\\applications";

    public ApplicationRepository(){
        System.setProperty("FIORANO_HOME", "D:\\sources_dec22\\installer" );
    }

    public Application readApplication(String appGuid, String version){
        try {
           return ApplicationParser.readApplication(new File(repoPath+ File.separator+appGuid+File.separator+version), false);
        } catch (FioranoException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> listApplications() {

        return null;
    }
}
