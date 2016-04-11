package com.fiorano.openesb.applicationcontroller;

import com.fiorano.openesb.application.application.ServiceInstance;
import com.fiorano.openesb.application.service.Execution;
import com.fiorano.openesb.application.service.RuntimeArgument;
import com.fiorano.openesb.application.service.ServiceRef;
import com.fiorano.openesb.microservice.launch.AdditionalConfiguration;
import com.fiorano.openesb.microservice.launch.JavaLaunchConfiguration;
import com.fiorano.openesb.microservice.launch.LaunchConfiguration;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import static com.fiorano.openesb.microservice.launch.LaunchConfiguration.LaunchMode.*;
import static com.fiorano.openesb.microservice.launch.LaunchConfiguration.LaunchMode.NONE;

public class MicroServiceLaunchConfiguration implements LaunchConfiguration {

    private String userName;
    private String password;
    private List<RuntimeArgument> runtimeArgs;
    private long stopRetryInterval;
    private int numberOfStopAttempts;
    private String microserviceId;
    private String microserviceVersion;
    private String serviceName;
    private String applicationName;
    private String applicationVersion;
    private AdditionalConfiguration additionalConfiguration;
    private LaunchMode launchMode;
    private List logModules;
    private Vector<ServiceRef> runtimeDependencies;

    MicroServiceLaunchConfiguration(String appGuid, String appVersion, String userName, String password, final ServiceInstance si) {
        this.userName = userName;
        this.password = password;
        this.runtimeArgs = si.getRuntimeArguments();
        this.microserviceId = si.getGUID();
        this.serviceName = si.getName();
        this.microserviceVersion = String.valueOf(si.getVersion());
        this.applicationName = appGuid;
        this.applicationVersion = appVersion;
        this.launchMode = ConfigurationConversionHelper.convertLaunchMode(si.getLaunchType());

        this.logModules = si.getLogModules();
        for (ServiceRef runtimeDependency : (si.getServiceRefs())) {
            addRuntimeDependency(runtimeDependency);
        }

        additionalConfiguration = new JavaLaunchConfiguration() {
            @Override
            public boolean isDebugMode() {
                return si.isDebugMode();
            }

            @Override
            public int getDebugPort() {
                return si.getDebugPort();
            }
        };

    }

    public void addRuntimeDependency(ServiceRef servDependencyInfo) {
        if (runtimeDependencies == null) {
            runtimeDependencies = new Vector<>();
        }
        if (!runtimeDependencies.contains(servDependencyInfo))
            runtimeDependencies.add(servDependencyInfo);
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

    public List getLogModules() {
        return logModules;
    }

    public Enumeration<ServiceRef> getRuntimeDependencies() {
        if (runtimeDependencies != null) {
            return runtimeDependencies.elements();
        }
        return null;
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

    public String getServiceName() {
        return serviceName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getApplicationVersion() {
        return applicationVersion;
    }

    public AdditionalConfiguration getAdditionalConfiguration() {
        return additionalConfiguration;
    }

    public static class ConfigurationConversionHelper {
        public static LaunchConfiguration.LaunchMode convertLaunchMode(int launchType) {
            switch (launchType) {
                case Execution.LAUNCH_TYPE_SEPARATE_PROCESS:
                    return SEPARATE_PROCESS;
                case Execution.LAUNCH_TYPE_IN_MEMORY:
                    return IN_MEMORY;
                case Execution.LAUNCH_TYPE_NONE:
                    return NONE;
                case Execution.LAUNCH_TYPE_MANUAL:
                    return MANUAL;
            }
            return NONE;
        }

    }
}
