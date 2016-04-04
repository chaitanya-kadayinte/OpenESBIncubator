package com.fiorano.openesb.management;

/**
 * Created by root on 3/25/16.
 */
public class ServerConfig {

    private String repositoryPath;
    private String RuntimeDataPath;
    private long CCPTimeOut;
    private long ApplicationStateRestoreWaitTime;

    public String getRepositoryPath() {
        return repositoryPath;
    }

    public void setRepositoryPath(String repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

    public String getRuntimeDataPath() {
        return RuntimeDataPath;
    }

    public void setRuntimeDataPath(String runtimeDataPath) {
        RuntimeDataPath = runtimeDataPath;
    }

    public long getCCPTimeOut() {
        return CCPTimeOut;
    }

    public void setCCPTimeOut(long CCPTimeOut) {
        this.CCPTimeOut = CCPTimeOut;
    }

    public long getApplicationStateRestoreWaitTime() {
        return ApplicationStateRestoreWaitTime;
    }

    public void setApplicationStateRestoreWaitTime(long applicationStateRestoreWaitTime) {
        ApplicationStateRestoreWaitTime = applicationStateRestoreWaitTime;
    }
}
