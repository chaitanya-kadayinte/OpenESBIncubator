package com.fiorano.openesb.microservice.launch.impl;

import com.fiorano.openesb.application.service.Execution;
import com.fiorano.openesb.microservice.ccp.CCPEventManager;
import com.fiorano.openesb.microservice.ccp.ComponentWorkflowListener;
import com.fiorano.openesb.microservice.ccp.event.ComponentCCPEvent;
import com.fiorano.openesb.microservice.ccp.event.ControlEvent;
import com.fiorano.openesb.microservice.ccp.event.common.DataEvent;
import com.fiorano.openesb.microservice.ccp.event.common.DataRequestEvent;
import com.fiorano.openesb.microservice.ccp.event.common.data.Data;
import com.fiorano.openesb.microservice.ccp.event.common.data.MicroserviceConfiguration;
import com.fiorano.openesb.microservice.ccp.event.peer.ConfigEvent;
import com.fiorano.openesb.microservice.launch.LaunchConfiguration;
import com.fiorano.openesb.microservice.launch.Launcher;
import com.fiorano.openesb.microservice.launch.MicroserviceRuntimeHandle;
import com.fiorano.openesb.microservice.repository.MicroserviceRepositoryManager;
import com.fiorano.openesb.transport.TransportService;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SeparateProcessLauncher implements Launcher<SeparateProcessRuntimeHandle> {
    private TransportService service;

    public SeparateProcessLauncher(TransportService service) {
        this.service = service;
    }

    public MicroserviceRuntimeHandle launch(LaunchConfiguration launchConfiguration, final String configuration) throws Exception {
        String name = launchConfiguration.getName();
        String applicationName = launchConfiguration.getApplicationName();
        String applicationVersion = launchConfiguration.getApplicationVersion();

        ProcessBuilder processBuilder = new ProcessBuilder();
        boolean isJava = MicroserviceRepositoryManager.getInstance().readMicroService(launchConfiguration.getMicroserviceId(), launchConfiguration.getMicroserviceVersion()).getExecution().getType() == Execution.TYPE_JAVA;
        CommandProvider commandProvider = isJava ? new JVMCommandProvider() : new NJCommandProvider();
        ProcessBuilder command = processBuilder.command(commandProvider.generateCommand(launchConfiguration));
        File directory = new File(MicroserviceRepositoryManager.getInstance().getMicroServiceBase(
                launchConfiguration.getMicroserviceId(), launchConfiguration.getMicroserviceVersion()));
        command.directory(directory);
        command.inheritIO();
        Process process = command.start();

        final CCPEventManager ccpEventManager = new CCPEventManager(service);
        ccpEventManager.init();

        ccpEventManager.registerListener(new ComponentWorkflowListener(name, applicationName, applicationVersion) {
            @Override
            public void onEvent(ComponentCCPEvent event) throws Exception {
                ControlEvent controlEvent = event.getControlEvent();
                if(controlEvent instanceof DataRequestEvent && controlEvent.isReplyNeeded()) {
                    for(DataRequestEvent.DataIdentifier request: ((DataRequestEvent) controlEvent).getDataIdentifiers()) {
                        if(request == DataRequestEvent.DataIdentifier.COMPONENT_CONFIGURATION) {
                            DataEvent dataEvent = new DataEvent();
                            MicroserviceConfiguration microserviceConfiguration = new MicroserviceConfiguration();
                            microserviceConfiguration.setConfiguration(configuration);
                            Map<DataRequestEvent.DataIdentifier,Data> data = new HashMap<>();
                            data.put(request,microserviceConfiguration);
                            dataEvent.setData(data);
                            ccpEventManager.getCcpEventGenerator().sendEvent(dataEvent);
                            break;
                        }
                    }
                }
            }
        });
        return new SeparateProcessRuntimeHandle(process);
    }

}
