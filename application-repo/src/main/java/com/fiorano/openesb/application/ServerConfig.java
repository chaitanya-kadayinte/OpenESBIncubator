package com.fiorano.openesb.application;

import com.fiorano.openesb.utils.ConfigReader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Janardhan on 3/27/2016.
 */
public class ServerConfig {
    private String repositoryPath="./esb/server/repository";
    private String runtimeDataPath="./data";
    private long CCPTimeOut=5000;
    private long applicationStateRestoreWaitTime=5000;

    public String getRepositoryPath() {
        File file = new File(repositoryPath);
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return repositoryPath;
    }

    public void setRepositoryPath(String repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

    public String getRuntimeDataPath() {
        File file = new File(runtimeDataPath);
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return runtimeDataPath;
    }

    public void setRuntimeDataPath(String runtimeDataPath) {
        this.runtimeDataPath = runtimeDataPath;
    }

    public long getCCPTimeOut() {
        return CCPTimeOut;
    }

    public void setCCPTimeOut(String CCPTimeOut) {
        this.CCPTimeOut = Long.parseLong(CCPTimeOut);
    }

    public long getApplicationStateRestoreWaitTime() {
        return applicationStateRestoreWaitTime;
    }

    public void setApplicationStateRestoreWaitTime(String applicationStateRestoreWaitTime) {
        this.applicationStateRestoreWaitTime = Long.parseLong(applicationStateRestoreWaitTime);
    }

    private static ServerConfig serverConfig = new ServerConfig();

    private ServerConfig() {
        File configFile = new File(System.getProperty("karaf.base") + File.separator
                + "etc" + File.separator + "com.fiorano.openesb.server.cfg");
        if (!configFile.exists()) {
            return;
        }
        try {
            ConfigReader.readConfigFromPropertiesFile(configFile, this);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static ServerConfig getConfig(){
        return serverConfig;
    }
}
