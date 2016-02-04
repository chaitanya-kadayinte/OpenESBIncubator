package com.fiorano.openesb.applicationcontroller;

import com.fiorano.openesb.application.application.ServiceInstance;
import com.fiorano.openesb.microservice.launch.LaunchConfiguration;

import java.util.List;

public class MicroServiceLaunchConfiguration implements LaunchConfiguration {

    String userName;
    String password;
    List runtimeArgs;
    long stopRetryInterval;
    int numberOfStopAttempts;
    String microserviceId;
    String microserviceVersion;
    String name;
    String applicationName;
    String applicationVersion;
    LaunchConfiguration additionalConfiguration;
    LaunchMode launchMode;

    MicroServiceLaunchConfiguration(String appGuid, String appVersion, String userName, String password, ServiceInstance si){
        this.userName = userName;
        this.password = password;
        this.runtimeArgs = si.getRuntimeArguments();
        this.microserviceId = si.getGUID();
        this.microserviceVersion = String.valueOf(si.getVersion());
        this.applicationName = appGuid;
        this.applicationVersion = appVersion;
        int i = si.getLaunchType();
        if(i==1){
            this.launchMode = LaunchMode.SEPARATE_PROCESS;
        }else if (i==2){
            this.launchMode = LaunchMode.IN_MEMORY;
        }else if (i==3){
            this.launchMode = LaunchMode.DOCKER;
        }
    }
    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public List getRuntimeArgs() {
        return runtimeArgs;
    }

    public LaunchMode getLaunchMode() {
        return launchMode;
    }

    public long getStopRetryInterval() {
        return 0;
    }

    public int getNumberOfStopAttempts() {
        return 0;
    }

    public String getMicroserviceId() {
        return microserviceId;
    }

    public String getMicroserviceVersion() {
        return microserviceVersion;
    }

    public String getName() {
        return name;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getApplicationVersion() {
        return applicationVersion;
    }

    public Object getAdditionalConfiguration() {
        return additionalConfiguration;
    }
}
