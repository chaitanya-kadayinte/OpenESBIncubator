package com.fiorano.openesb.microservice.launch.impl;

import com.fiorano.openesb.microservice.launch.LaunchConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NJCommandProvider extends CommandProvider {

    @Override
    protected List<String> generateCommand(LaunchConfiguration launchConfiguration) throws Exception {
        List<String> command = new ArrayList<>();
        command.add(getExecutionDir(launchConfiguration)+ File.separator +
                getComponentPS(launchConfiguration.getMicroserviceId(), launchConfiguration.getMicroserviceVersion()).getExecution().getExecutable());
        command.addAll(getCommandLineParams(launchConfiguration));
        return command;
    }


}
