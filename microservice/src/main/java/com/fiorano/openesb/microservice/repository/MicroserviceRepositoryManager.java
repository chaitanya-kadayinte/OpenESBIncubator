/**
 * Copyright (c) 1999-2007, Fiorano Software Technologies Pvt. Ltd. and affiliates.
 * Copyright (c) 2008-2014, Fiorano Software Pte. Ltd. and affiliates.
 * <p>
 * All rights reserved.
 * <p>
 * This software is the confidential and proprietary information
 * of Fiorano Software ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * enclosed with this product or entered into with Fiorano.
 * <p>
 * Created by chaitanya on 23-01-2016.
 * <p>
 * Created by chaitanya on 23-01-2016.
 */

/**
 * Created by chaitanya on 23-01-2016.
 */
package com.fiorano.openesb.microservice.repository;

import com.fiorano.openesb.application.service.Service;
import com.fiorano.openesb.application.service.ServiceParser;
import com.fiorano.openesb.utils.exception.FioranoException;

import java.io.File;
//todo move to management related modules
public class MicroserviceRepositoryManager {

    private static final MicroserviceRepositoryManager microserviceRepositoryManager = new MicroserviceRepositoryManager();
    private MicroserviceRepositoryManager() {
    }

    public static MicroserviceRepositoryManager getInstance() {
        return microserviceRepositoryManager;
    }

    public String getRepositoryLocation() {
        File karafBase = new File(System.getProperty("karaf.base"));
        return karafBase  + File.separator + "data"
                + File.separator + "fiorano"+  File.separator + "repository" + File.separator + "microservices";
    }

    /**
     * Returns service property sheet for the parameter component.
     * Returns null if the component is not present in the  repository
     *
     * @param microServiceId specifies a microservice uniquely.
     * @param version specifies the version of microservice.
     * @return ServicePropertySheet
     * @exception FioranoException
     */
    public Service readMicroService(String microServiceId, String version)
            throws FioranoException {
        File file = new File(getMicroServiceBase(microServiceId, version)
                + File.separator + "ServiceDescriptor.xml");
        if(!file.exists()) {
            throw new FioranoException("Component " + microServiceId + ":" + version + " is not present in repository");
        }
        return ServiceParser.readService(file);
    }

    public String getMicroServiceBase(String microServiceId, String version) {
        return getRepositoryLocation() + File.separator + microServiceId + File.separator + version;
    }


}
