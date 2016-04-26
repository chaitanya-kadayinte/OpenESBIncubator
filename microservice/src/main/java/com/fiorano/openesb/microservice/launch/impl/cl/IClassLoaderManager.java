package com.fiorano.openesb.microservice.launch.impl.cl;

import com.fiorano.openesb.application.service.Service;
import com.fiorano.openesb.microservice.launch.LaunchConfiguration;
import com.fiorano.openesb.utils.exception.FioranoException;


public interface IClassLoaderManager {
    ClassLoader getClassLoader(Service sps, LaunchConfiguration launchConfiguration) throws FioranoException;

    void unloadClassLoader(Service sps, LaunchConfiguration launchConfiguration) throws FioranoException;
}
