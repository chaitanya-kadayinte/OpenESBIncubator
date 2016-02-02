package com.fiorano.openesb.microservice.launch.impl;

import com.fiorano.openesb.microservice.launch.LaunchConfiguration;
import com.fiorano.openesb.microservice.launch.Launcher;
import com.fiorano.openesb.microservice.launch.MicroserviceRuntimeHandle;

public class SeparateProcessLauncher implements Launcher<SeparateProcessRuntimeHandle> {
    public MicroserviceRuntimeHandle launch(LaunchConfiguration launchConfiguration) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder();
        CommandProvider commandProvider = new CommandProvider();
        ProcessBuilder command = processBuilder.command(commandProvider.generateCommand(launchConfiguration));
        Process process = command.start();
        return new SeparateProcessRuntimeHandle(process);
    }

}
