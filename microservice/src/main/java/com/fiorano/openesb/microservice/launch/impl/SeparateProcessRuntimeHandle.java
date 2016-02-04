package com.fiorano.openesb.microservice.launch.impl;

import com.fiorano.openesb.microservice.launch.MicroserviceRuntimeHandle;

public class SeparateProcessRuntimeHandle implements MicroserviceRuntimeHandle {

    private Process osProcess;

    public SeparateProcessRuntimeHandle(Process osProcess) {
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
