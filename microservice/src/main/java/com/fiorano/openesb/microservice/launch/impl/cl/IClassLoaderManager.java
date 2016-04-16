package com.fiorano.openesb.microservice.launch.impl.cl;

import com.fiorano.openesb.application.service.Service;
import com.fiorano.openesb.utils.exception.FioranoException;


public interface IClassLoaderManager
{
    /**
     * Returns class loader for object
     *
     * @param sps
     * @return
     * @exception FioranoException
     */
    public ClassLoader getClassLoader(Service sps) throws FioranoException;

    public void unloadClassLoader(Service sps) throws FioranoException;
}
