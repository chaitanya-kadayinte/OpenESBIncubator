/**
 * Copyright (c) 1999-2007, Fiorano Software Technologies Pvt. Ltd. and affiliates.
 * Copyright (c) 2008-2015, Fiorano Software Pte. Ltd. and affiliates.
 *
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Fiorano Software ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * enclosed with this product or entered into with Fiorano.
 */

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
