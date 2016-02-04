package com.fiorano.openesb.applicationcontroller;

import com.fiorano.openesb.application.application.Application;
import com.fiorano.openesb.application.application.ServiceInstance;
import com.fiorano.openesb.microservice.launch.MicroserviceRuntimeHandle;
import com.fiorano.openesb.microservice.launch.impl.MicroserviceLauncher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationHandle {

    Application application;
    Map<String, MicroserviceRuntimeHandle> microServiceHandleList = new HashMap<String, MicroserviceRuntimeHandle>();

    ApplicationHandle(Application application){
        this.application = application;
    }

    public void createMicroServiceHandles() {


    }

    public void createRoutes() {

    }

    public void launchComponents() {
        for (Object obj : application.getServiceInstances()) {
            ServiceInstance instance = (ServiceInstance) obj;
            String instanceName = instance.getName();
            MicroServiceLaunchConfiguration mslc = new MicroServiceLaunchConfiguration(application.getGUID(), String.valueOf(application.getVersion()), "admin", "passwd", instance);
            try {
                microServiceHandleList.put(instanceName, new MicroserviceLauncher().launch(mslc));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stopApplication() {
        for(MicroserviceRuntimeHandle handle:microServiceHandleList.values()){
            handle.stop();
        }
    }
}
