package com.fiorano.openesb.microservice.launch.impl;

import com.fiorano.openesb.microservice.launch.MicroserviceLaunchHandle;

public class SeparateProcessLaunchHandle implements MicroserviceLaunchHandle {

    private Process osProcess;

    public SeparateProcessLaunchHandle(Process osProcess) {
        this.osProcess = osProcess;
    }

    public boolean isRunning() {
        return osProcess.isAlive();
    }

    public void stop() {
        //todo CCP
        osProcess.destroy();
    }

    public void kill() {
         osProcess.destroyForcibly();
    }
}
