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
 */
package com.fiorano.openesb.management;

import com.fiorano.openesb.application.ApplicationRepository;
import com.fiorano.openesb.application.application.Application;
import com.fiorano.openesb.application.application.ServiceInstance;
import com.fiorano.openesb.applicationcontroller.ApplicationController;
import com.fiorano.openesb.utils.exception.FioranoException;
import org.apache.activemq.usage.SystemUsage;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@CrossOriginResourceSharing(
        allowAllOrigins = true,
        allowCredentials = true,
        maxAge = 1
)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RestServiceImpl implements ApplicationsService {

    public RestServiceImpl() {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/applications")
    public Response getApplications() {
        BundleContext bundleContext = FrameworkUtil.getBundle(ApplicationsService.class).getBundleContext();
        ApplicationRepository controller = bundleContext.getService(bundleContext.getServiceReference(ApplicationRepository.class));
        Response response = new Response();
        response.setApplications(controller.getApplicationIdWithVersions());
        response.setStatus(true);
        return response;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/overview")
    public Overview getOverview() {
        Overview overview = new Overview();
        overview.setServerId("FioranoOpenESB");

        return overview;
    }
    @Path("/applications/{applicationName}/{applicationVersion}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public com.fiorano.openesb.management.Application getApplicationDetails(@PathParam("applicationName") String applicationName, @PathParam("applicationVersion") String applicationVersion) {
        ApplicationRepository controller = getApplicationRepository();
        com.fiorano.openesb.management.Application response = new com.fiorano.openesb.management.Application();
        try {
            Application application = controller.readApplication(applicationName, applicationVersion);
            List<String> services = new ArrayList<>();
            for (ServiceInstance serviceInstance : application.getServiceInstances()) {
                services.add(serviceInstance.getName());
            }
            response.setServices(services);
            response.setId(application.getGUID());
            response.setName(application.getDisplayName());
            response.setVersion(applicationVersion);
            response.setIsRunning(true);
            return response;
        } catch (Exception e) {
            //todo
            return response;
        }
    }

    @POST
    @Path("/applications/{applicationName}/{applicationVersion}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response performApplicationAction(@PathParam("applicationName") String applicationName, @PathParam("applicationVersion") String applicationVersion, Action action) {
        ApplicationController controller = getController();
        Response response = new Response();
        try {
            String actionStr = action.getAction();
            if (actionStr.equalsIgnoreCase("start") || actionStr.equalsIgnoreCase("launch")) {
                controller.launchApplication(applicationName, applicationVersion, null);
                response.setMessage("Application launched successfully");
            } else if (actionStr.equalsIgnoreCase("stop")) {
                controller.stopApplication(applicationName, applicationVersion, null);
                response.setMessage("Application stopped successfully");
            } else if (actionStr.equalsIgnoreCase("stop")) {
                controller.synchronizeApplication(applicationName,applicationVersion,null);
            }
            response.setStatus(true);
            return response;
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setStatus(false);
            return response;
        }

    }

    private ApplicationController getController() {
        BundleContext bundleContext = FrameworkUtil.getBundle(ApplicationController.class).getBundleContext();
        return bundleContext.getService(bundleContext.getServiceReference(ApplicationController.class));
    }

    private ApplicationRepository getApplicationRepository() {
        BundleContext bundleContext = FrameworkUtil.getBundle(ApplicationRepository.class).getBundleContext();
        return bundleContext.getService(bundleContext.getServiceReference(ApplicationRepository.class));
    }

    @POST
    @Path("/applications/{applicationName}/{applicationVersion}/{microServiceName}")
    public Response performMicroServiceAction(@PathParam("applicationName") String applicationName, @PathParam("applicationVersion") String applicationVersion, @PathParam("microServiceName") String microServiceName, Action action) {
        ApplicationController controller = getController();
        Response response = new Response();
        try {
            response.setStatus(controller.startMicroService(applicationName, applicationVersion, microServiceName, null));
            return response;
        } catch (FioranoException e) {
            response.setMessage(e.getMessage());
            response.setStatus(false);
            return response;
        }

    }

    @PUT
    @Path("/applications/{applicationName}/{applicationVersion}/{microServiceName}")
    public Response stopMicroService(@PathParam("applicationName") String applicationName, @PathParam("applicationVersion") String applicationVersion, @PathParam("microServiceName") String microServiceName) {
        ApplicationController controller = getController();
        Response response = new Response();
        try {
            response.setStatus(controller.stopMicroService(applicationName, applicationVersion, microServiceName, null));
            return response;
        } catch (FioranoException e) {
            response.setMessage(e.getMessage());
            response.setStatus(false);
            return response;
        }
    }


}
