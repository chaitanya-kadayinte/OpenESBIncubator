/**
 * Copyright (c) 1999-2007, Fiorano Software Technologies Pvt. Ltd. and affiliates.
 * Copyright (c) 2008-2014, Fiorano Software Pte. Ltd. and affiliates.
 * <p>
 * All rights reserved.
 * <p>
 * This software is the confidential and proprietary information
 * of Fiorano Software ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * enclosed with this product or entered into with Fiorano.
 * <p>
 * Created by chaitanya on 26-01-2016.
 */

/**
 * Created by chaitanya on 26-01-2016.
 */
package com.fiorano.openesb.microservice.launch.impl;

import com.fiorano.openesb.application.DmiObject;
import com.fiorano.openesb.application.service.RuntimeArgument;
import com.fiorano.openesb.application.service.Service;
import com.fiorano.openesb.microservice.launch.AdditionalConfiguration;
import com.fiorano.openesb.microservice.launch.LaunchConfiguration;
import com.fiorano.openesb.microservice.launch.LaunchConstants;
import com.fiorano.openesb.microservice.repository.MicroServiceRepoManager;
import com.fiorano.openesb.transport.impl.jms.TransportConfig;
import com.fiorano.openesb.utils.exception.FioranoException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class CommandProvider<J extends AdditionalConfiguration> {

    protected abstract List<String> generateCommand(LaunchConfiguration<J> launchConfiguration) throws  Exception;

    protected List<String> getCommandLineParams(LaunchConfiguration<J> launchConfiguration) {
        Map<String, String> commandLineArgs = new LinkedHashMap<String, String>();
        String connectURL = TransportConfig.getInstance().getValue("providerURL");
        commandLineArgs.put(LaunchConstants.URL, connectURL);
        commandLineArgs.put(LaunchConstants.BACKUP_URL, connectURL);
        commandLineArgs.put(LaunchConstants.FES_URL, connectURL);
        commandLineArgs.put(LaunchConstants.USERNAME, launchConfiguration.getUserName());
        commandLineArgs.put(LaunchConstants.PASSWORD, launchConfiguration.getPassword());
        commandLineArgs.put(LaunchConstants.CONN_FACTORY,"ConnectionFactory");
        commandLineArgs.put(LaunchConstants.CLIENT_ID, getServiceInstanceLookupName(launchConfiguration.getApplicationName(),
                launchConfiguration.getApplicationVersion(), launchConfiguration.getServiceName()));
        commandLineArgs.put(LaunchConstants.EVENT_PROC_NAME, launchConfiguration.getApplicationName());
        commandLineArgs.put(LaunchConstants.EVENT_PROC_VERSION, launchConfiguration.getApplicationVersion());
        commandLineArgs.put(LaunchConstants.COMP_INSTANCE_NAME, launchConfiguration.getServiceName());

        commandLineArgs.put(LaunchConstants.IS_IN_MEMORY, launchConfiguration.getLaunchMode() == LaunchConfiguration.
                LaunchMode.IN_MEMORY ? "true" : "false");
        commandLineArgs.put(LaunchConstants.CCP_ENABLED, "true");
        commandLineArgs.put(LaunchConstants.COMPONENT_REPO_PATH, MicroServiceRepoManager.getInstance().getRepositoryLocation());
        commandLineArgs.put(LaunchConstants.COMPONENT_GUID, launchConfiguration.getMicroserviceId());
        commandLineArgs.put(LaunchConstants.COMPONENT_VERSION, launchConfiguration.getMicroserviceVersion());

        RuntimeArgument arg = (RuntimeArgument) DmiObject.findNamedObject(launchConfiguration.getRuntimeArgs(), LaunchConstants.JCA_INTERACTION_SPEC);
        if (arg != null)
            commandLineArgs.put(LaunchConstants.JCA_INTERACTION_SPEC, arg.getValueAsString());

        for (RuntimeArgument runtimeArg : launchConfiguration.getRuntimeArgs()) {
            String argValue = runtimeArg.getValueAsString();
            if (!runtimeArg.getName().equalsIgnoreCase("JVM_PARAMS") && argValue!=null)
                commandLineArgs.put(runtimeArg.getName(), runtimeArg.getValueAsString());
        }

        List<String> commandLineParams = new ArrayList<>();
        for(Map.Entry<String, String> entry:commandLineArgs.entrySet()){
            commandLineParams.add(entry.getKey());
            commandLineParams.add(entry.getValue());
        }
        return commandLineParams;
    }

    private String getServiceInstanceLookupName(String applicationName, String applicationVersion, String name) {
        return applicationName + "__" + applicationVersion + "__" + name;
    }

    protected String getExecutionDir(LaunchConfiguration launchConfiguration) {
        return MicroServiceRepoManager.getInstance().getMicroServiceBase(launchConfiguration.getMicroserviceId(),
                launchConfiguration.getMicroserviceVersion());
    }

    protected Service getComponentPS(String componentGUID, String componentVersion) throws FioranoException {
        return MicroServiceRepoManager.getInstance().readMicroService(componentGUID, componentVersion);
    }
}
