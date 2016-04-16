package com.fiorano.openesb.microservice.launch.impl;

import com.fiorano.openesb.microservice.launch.LaunchConfiguration;
import com.fiorano.openesb.microservice.launch.MicroServiceRuntimeHandle;
import com.fiorano.openesb.utils.LoggerUtil;
import com.fiorano.openesb.utils.exception.FioranoException;
import com.fiorano.openesb.utils.logging.FioranoLogHandler;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InMemoryRuntimeHandle extends MicroServiceRuntimeHandle {

    private Object service;
    private Class serviceClass;

    public InMemoryRuntimeHandle(Object service, Class serviceClass, LaunchConfiguration launchConfiguration) {
        super(launchConfiguration);
        this.service = service;
        this.serviceClass = serviceClass;
        this.launchConfiguration = launchConfiguration;
        isRunning = true;
        strStatus = EventStateConstants.SERVICE_HANDLE_BOUND;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void stop() throws Exception {
        Method shutDownMethod = serviceClass.getMethod("shutdown", Object.class);
        shutDownMethod.invoke(service, "Shutdown Microservice");
        isRunning = false;
        gracefulKill = true;
        strStatus = EventStateConstants.SERVICE_HANDLE_UNBOUND;
    }

    public void kill() throws Exception {
        stop();
    }

    @Override
    public void setLogLevel(Map<String, String> modules) throws FioranoException {
        for(Map.Entry<String, String> modifiedLevel:modules.entrySet()){
            Logger logger = LoggerUtil.getServiceLogger(modifiedLevel.getKey(), launchConfiguration.getApplicationName(),
                    launchConfiguration.getApplicationVersion(), launchConfiguration.getMicroserviceId(), launchConfiguration.getServiceName());
            for(Handler handler:logger.getHandlers()){
                logger.removeHandler(handler);
                if(handler instanceof FioranoLogHandler)
                    ((FioranoLogHandler)handler).setLogLevel(Level.parse(modifiedLevel.getValue()));
                else
                    handler.setLevel(Level.parse(modifiedLevel.getValue()));
                logger.addHandler(handler);
            }
        }
    }

    @Override
    public LaunchConfiguration.LaunchMode getLaunchMode() {
        return LaunchConfiguration.LaunchMode.IN_MEMORY;
    }

}
