package com.fiorano.openesb.microservice.launch.impl;

import com.fiorano.openesb.microservice.launch.LaunchConfiguration;
import com.fiorano.openesb.microservice.launch.Launcher;
import com.fiorano.openesb.microservice.launch.MicroserviceLaunchHandle;

import java.io.IOException;

public class SeparateProcessLauncher implements Launcher<SeparateProcessLaunchHandle> {
    public MicroserviceLaunchHandle launch(LaunchConfiguration launchConfiguration) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder();
        ProcessBuilder command = processBuilder.command(CommandProvider.generateCommand(launchConfiguration));
        Process process = processBuilder.start();
        return new SeparateProcessLaunchHandle(process);
    }

    public void stop(SeparateProcessLaunchHandle microserviceLaunchHandle) {
        microserviceLaunchHandle.stop();
    }

}
