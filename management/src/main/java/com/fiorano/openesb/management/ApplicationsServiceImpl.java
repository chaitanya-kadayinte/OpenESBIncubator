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

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;


@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ApplicationsServiceImpl implements ApplicationsService {

    public ApplicationsServiceImpl() {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/applications")
    public Response getApplications() {
        BundleContext bundleContext = FrameworkUtil.getBundle(ApplicationsService.class).getBundleContext();
        ApplicationController controller = bundleContext.getService(bundleContext.getServiceReference(ApplicationController.class));
        ArrayList<String> list = new ArrayList<>();
        list.addAll(controller.getListOfRunningApplications(null));
        Response response = new Response();
        response.setApplications(list);
        response.setStatus(true);
        return response;
    }

    @Path("/applications/{applicationName}/{applicationVersion}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response launchApplication(@PathParam("applicationName") String applicationName, @PathParam("applicationVersion") String applicationVersion) {
        ApplicationController controller = getController();
        Response response = new Response();
        try {
            controller.launchApplication(applicationName, applicationVersion, null);
            response.setMessage("Application launched successfully");
            response.setStatus(true);
            return response;
        } catch (Exception e) {
//            e.printStackTrace();
            response.setStatus(false);
            response.setMessage(e.getMessage());
            return response;
        }
    }

    @PUT
    @Path("/applications/{applicationName}/{applicationVersion}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response stopApplication(@PathParam("applicationName") String applicationName, @PathParam("applicationVersion") String applicationVersion) {
        ApplicationController controller = getController();
        Response response = new Response();
        try {
            controller.stopApplication(applicationName, applicationVersion, null);
            response.setMessage("Application stoped successfully");
            response.setStatus(true);
            return response;
        } catch (Exception e) {
           // e.printStackTrace();
            response.setMessage(e.getMessage());
            response.setStatus(false);
            return response;
        }

    }

    private ApplicationController getController() {
        BundleContext bundleContext = FrameworkUtil.getBundle(ApplicationsService.class).getBundleContext();
        ApplicationController controller = bundleContext.getService(bundleContext.getServiceReference(ApplicationController.class));
        return controller;
    }

    @POST
    @Path("/applications/{applicationName}/{applicationVersion}/{microServiceName}")
    public Response startMicroService(@PathParam("applicationName") String applicationName, @PathParam("applicationVersion") String applicationVersion, @PathParam("microServiceName") String microServiceName) {
        ApplicationController controller = getController();
        Response response = new Response();
        response.setStatus(controller.startMicroService(applicationName, applicationVersion, microServiceName, null));
        return response;
    }

    @PUT
    @Path("/applications/{applicationName}/{applicationVersion}/{microServiceName}")
    public Response stopMicroService(@PathParam("applicationName") String applicationName, @PathParam("applicationVersion") String applicationVersion, @PathParam("microServiceName") String microServiceName) {
        ApplicationController controller = getController();
        Response response = new Response();
        response.setStatus(controller.stopMicroService(applicationName, applicationVersion, microServiceName, null));
        return response;
    }

    @PUT
    @Path("/applications/{applicationName}/{applicationVersion}")
    public Response synchronizeApplication(@PathParam("applicationName") String applicationName, @PathParam("applicationVersion") String applicationVersion) {
        ApplicationController controller = getController();
        Response response = new Response();
        response.setStatus(controller.synchronizeApplication(applicationName, applicationVersion, null));
        return response;
    }
}
