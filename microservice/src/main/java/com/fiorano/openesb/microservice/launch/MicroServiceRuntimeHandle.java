package com.fiorano.openesb.microservice.launch;

import com.fiorano.openesb.application.aps.ServiceInstanceStateDetails;
import com.fiorano.openesb.utils.exception.FioranoException;

import java.util.Map;

public abstract class MicroServiceRuntimeHandle {

    protected boolean isRunning;
    protected String serviceInstName;
    protected String strStatus;
    protected String exceptionTrace;
    protected long killTime;
    protected long launchTime;
    protected String serviceGUID;
    protected float runningVersion;
    protected boolean gracefulKill;
    protected LaunchConfiguration launchConfiguration;

    protected MicroServiceRuntimeHandle(LaunchConfiguration launchConfiguration){
        this.launchConfiguration = launchConfiguration;
        this.serviceInstName = launchConfiguration.getServiceName();
    }

    protected String getAppVersion() {
        return launchConfiguration.getApplicationVersion();
    }

    public String getServiceInstName() {
        return launchConfiguration.getServiceName();
    }

    public void setServiceInstName(String serviceInstName) {
        this.serviceInstName = serviceInstName;
    }

    public String getStrStatus() {
        return strStatus;
    }

    public void setStrStatus(String strStatus) {
        this.strStatus = strStatus;
    }

    public String getExceptionTrace() {
        return exceptionTrace;
    }

    public void setExceptionTrace(String exceptionTrace) {
        this.exceptionTrace = exceptionTrace;
    }

    public long getKillTime() {
        return killTime;
    }

    public void setKillTime(long killTime) {
        this.killTime = killTime;
    }

    public long getLaunchTime() {
        return launchTime;
    }

    public void setLaunchTime(long launchTime) {
        this.launchTime = launchTime;
    }

    public String getServiceGUID() {
        return serviceGUID;
    }

    public void setServiceGUID(String serviceGUID) {
        this.serviceGUID = serviceGUID;
    }

    public float getRunningVersion() {
        return runningVersion;
    }

    public void setRunningVersion(float runningVersion) {
        this.runningVersion = runningVersion;
    }

    public boolean isGracefulKill() {
        return gracefulKill;
    }

    public void setGracefulKill(boolean gracefulKill) {
        this.gracefulKill = gracefulKill;
    }

    public LaunchConfiguration getLaunchConfiguration() {
        return launchConfiguration;
    }

    public void setLaunchConfiguration(LaunchConfiguration launchConfiguration) {
        this.launchConfiguration = launchConfiguration;
    }

    public abstract boolean isRunning();

    public abstract void stop() throws Exception;

    protected abstract   void kill() throws Exception;

    protected abstract void setLogLevel(Map<String, String> modules) throws Exception;

    public abstract LaunchConfiguration.LaunchMode getLaunchMode();

    public ServiceInstanceStateDetails getServiceStateDetails(){
        ServiceInstanceStateDetails details = new ServiceInstanceStateDetails();

        details.setKillTime(killTime);
        details.setLaunchTime(launchTime);
        details.setServiceGUID(serviceGUID);
        details.setServiceInstanceName(serviceInstName);
        details.setStatusString(strStatus);
        details.setGracefulKill(gracefulKill);
        details.setRunningVersion(String.valueOf(runningVersion));

        return details;
    }



}

