
package com.fiorano.openesb.management;

import java.util.List;

public class Application {
    private String id;
    private String name;
    private String version;
    private boolean isRunning;
    private List<Microservice> services;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean getIsRunning() {
        return isRunning;
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public List<Microservice> getServices() {
        return services;
    }

    public void setServices(List<Microservice> services) {
        this.services = services;
    }
}
