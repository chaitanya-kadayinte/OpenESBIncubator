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
import com.fiorano.openesb.application.service.Execution;
import com.fiorano.openesb.application.service.RuntimeArgument;
import com.fiorano.openesb.application.service.Service;
import com.fiorano.openesb.microservice.launch.LaunchConfiguration;
import com.fiorano.openesb.microservice.launch.LaunchConstants;
import com.fiorano.openesb.microservice.repository.MicroserviceRepositoryManager;
import com.fiorano.openesb.utils.config.ConfigurationLookupHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class CommandProvider {

    public List<String> generateCommand(LaunchConfiguration launchConfiguration) throws  Exception {
        List<String> command = new ArrayList<String>();
        Service service = MicroserviceRepositoryManager.getInstance().readMicroService(
                launchConfiguration.getMicroserviceId(),launchConfiguration.getMicroserviceVersion());

        return command;
    }

    protected String[] getCommandLineParams(LaunchConfiguration launchConfiguration, String componentPath) {
        Map<String, String> commandLineArgs = new LinkedHashMap<String, String>();
        String serverIp = ConfigurationLookupHelper.getInstance().getValue("SERVER_IP");
        String connectURL = serverIp == null ?  "http://localhost:61616" : "http://"+ serverIp+":61616";
        commandLineArgs.put(LaunchConstants.URL, connectURL);
        commandLineArgs.put(LaunchConstants.BACKUP_URL, connectURL);
        commandLineArgs.put(LaunchConstants.FES_URL, connectURL);
        commandLineArgs.put(LaunchConstants.USERNAME, launchConfiguration.getUserName());
        commandLineArgs.put(LaunchConstants.PASSWORD, launchConfiguration.getPassword());
        commandLineArgs.put(LaunchConstants.CONN_FACTORY, getServiceInstanceLookupName(launchConfiguration.getApplicationName(),
                launchConfiguration.getApplicationVersion(), launchConfiguration.getName()));
        commandLineArgs.put(LaunchConstants.EVENT_PROC_NAME, launchConfiguration.getApplicationName());
        commandLineArgs.put(LaunchConstants.EVENT_PROC_VERSION, launchConfiguration.getApplicationVersion());
        commandLineArgs.put(LaunchConstants.COMP_INSTANCE_NAME, launchConfiguration.getName());
        commandLineArgs.put(LaunchConstants.EVENTS_TOPIC, ConfigurationLookupHelper.getInstance().getValue("EVENTS_TOPIC"));

        if (launchConfiguration.getLaunchMode() == LaunchConfiguration.LaunchMode.IN_MEMORY) {
            commandLineArgs.put(LaunchConstants.IS_IN_MEMORY, "true");
        } else {
            commandLineArgs.put(LaunchConstants.IS_IN_MEMORY, "false");
        }
        commandLineArgs.put(LaunchConstants.NODE_NAME, ConfigurationLookupHelper.getInstance().getValue("PROFILE_NAME"));
        commandLineArgs.put(LaunchConstants.CCP_ENABLED, "true");
        commandLineArgs.put(LaunchConstants.COMPONENT_REPO_PATH, componentPath);
        commandLineArgs.put(LaunchConstants.COMPONENT_GUID, launchConfiguration.getMicroserviceId());
        commandLineArgs.put(LaunchConstants.COMPONENT_VERSION, launchConfiguration.getMicroserviceVersion());


        RuntimeArgument arg = (RuntimeArgument) DmiObject.findNamedObject(launchConfiguration.getRuntimeArgs(), LaunchConstants.JCA_INTERACTION_SPEC);
        if (arg != null)
            commandLineArgs.put(LaunchConstants.JCA_INTERACTION_SPEC, arg.getValueAsString());

        //// TODO: 02-02-2016 remove this if not supported in initial release 
//        String securityProtocol = connectionManager.getSecurityProtocol();
//        if (securityProtocol != null){
//            commandLineArgs.put(LaunchConstants.SECURITY_PROTOCOL, securityProtocol);
//            if(securityProtocol.equalsIgnoreCase("SUN_SSL") )
//                commandLineArgs.put(LaunchConstants.SECURITY_MANAGER, "fiorano.jms.runtime.sm.JSSESecurityManager");
//        }

//        for(RouteInfo info:handle.getRouteManager().getRoutes().values()){
//            if(info.getTrgtSecurityManager() != null)
//                commandLineArgs.put(LaunchConstants.SECURITY_MANAGER, info.getTrgtSecurityManager());
//        }

        // get the runtime arguments and also look for the JVM params
        for (Object aTemp : launchConfiguration.getRuntimeArgs()) {
            RuntimeArgument runtimeArg = (RuntimeArgument) aTemp;
            String argValue = runtimeArg.getValueAsString();
            if (!runtimeArg.getName().equalsIgnoreCase("JVM_PARAMS") && argValue!=null)     /*Bugzilla – Bug 18542 , making null check for argValue*/
                commandLineArgs.put(runtimeArg.getName(), runtimeArg.getValueAsString());
        }

        String[] commandLineParams = new String[commandLineArgs.size() * 2];
        int i = 0;
        for(Map.Entry<String, String> entry:commandLineArgs.entrySet()){
            commandLineParams[i++] = entry.getKey();
            commandLineParams[i++] = entry.getValue();
        }
        return commandLineParams;
    }

    private String getServiceInstanceLookupName(String applicationName, String applicationVersion, String name) {
        return applicationName + "__" + applicationVersion + "__" + name;
    }
}
