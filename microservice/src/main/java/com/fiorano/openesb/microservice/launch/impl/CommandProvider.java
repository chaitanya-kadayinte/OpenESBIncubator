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
 * Created by chaitanya on 26-01-2016.
 */

/**
 * Created by chaitanya on 26-01-2016.
 */
package com.fiorano.openesb.microservice.launch.impl;

import com.fiorano.openesb.application.service.Service;
import com.fiorano.openesb.application.service.ServiceParser;
import com.fiorano.openesb.microservice.launch.LaunchConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class CommandProvider {
    public static List<String> generateCommand(LaunchConfiguration launchConfiguration) throws  Exception {
        List<String> command = new ArrayList<String>();
        String microServiceDetails = launchConfiguration.getMicroserviceDetails();
        String guid = microServiceDetails.substring(0,microServiceDetails.indexOf(":"));
        String version = microServiceDetails.substring(microServiceDetails.indexOf(":")+1);
        String repositoryLocation = System.getProperty("REPO");
        Service service = ServiceParser.readService(new File(repositoryLocation + File.separator + guid
                + File.separator + version + File.separator + "ServiceDescriptor.xml"));
        service.getDeployment().getResources();
        return command;
    }
}
