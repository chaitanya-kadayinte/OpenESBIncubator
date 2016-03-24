package com.fiorano.openesb.microservice.launch.impl;

import com.fiorano.openesb.application.service.Execution;
import com.fiorano.openesb.microservice.ccp.CCPEventManager;
import com.fiorano.openesb.microservice.launch.LaunchConfiguration;
import com.fiorano.openesb.microservice.launch.Launcher;
import com.fiorano.openesb.microservice.launch.MicroServiceRuntimeHandle;
import com.fiorano.openesb.microservice.repository.MicroServiceRepoManager;

import java.io.File;

public class SeparateProcessLauncher implements Launcher<SeparateProcessRuntimeHandle> {

    private CCPEventManager ccpEventManager;

    @SuppressWarnings("unchecked")
    public SeparateProcessLauncher(CCPEventManager ccpEventManager) throws Exception {
        this.ccpEventManager = ccpEventManager;
    }

    public MicroServiceRuntimeHandle launch(final LaunchConfiguration launchConfiguration, final String configuration) throws Exception {
        SeparateProcessRuntimeHandle separateProcessRuntimeHandle = new SeparateProcessRuntimeHandle(launchConfiguration,new CCPCommandHelper(ccpEventManager,launchConfiguration));
        Process process = startProcess(launchConfiguration);
        separateProcessRuntimeHandle.setProcess(process);
        return separateProcessRuntimeHandle;
    }

    @SuppressWarnings("unchecked")
    private Process startProcess(LaunchConfiguration launchConfiguration) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder();
        boolean isJava = MicroServiceRepoManager.getInstance().readMicroService(launchConfiguration.getMicroserviceId(), launchConfiguration.getMicroserviceVersion()).getExecution().getType() == Execution.TYPE_JAVA;
        CommandProvider commandProvider = isJava ? new JVMCommandProvider() : new NJCommandProvider();
        ProcessBuilder command = processBuilder.command(commandProvider.generateCommand(launchConfiguration));
        File directory = new File(MicroServiceRepoManager.getInstance().getMicroServiceBase(
                launchConfiguration.getMicroserviceId(), launchConfiguration.getMicroserviceVersion()));
        command.directory(directory);
        command.inheritIO();
        return command.start();
    }

}
