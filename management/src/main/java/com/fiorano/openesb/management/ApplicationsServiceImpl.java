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
 * Created by chaitanya on 05-03-2016.
 */

/**
 * Created by chaitanya on 05-03-2016.
 */
package com.fiorano.openesb.management;

import com.fiorano.openesb.applicationcontroller.ApplicationController;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ApplicationsServiceImpl implements ApplicationsService {

    public ApplicationsServiceImpl() {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getApplications() {
        BundleContext bundleContext = FrameworkUtil.getBundle(ApplicationsService.class).getBundleContext();
        ApplicationController controller = bundleContext.getService(bundleContext.getServiceReference(ApplicationController.class));
        ArrayList<String> list = new ArrayList<>();
        list.addAll(controller.getListOfRunningApplications(null));
        return list;
    }
}
