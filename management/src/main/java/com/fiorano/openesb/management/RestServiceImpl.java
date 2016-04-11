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
import com.fiorano.openesb.application.aps.ApplicationStateDetails;
import com.fiorano.openesb.application.aps.ServiceInstanceStateDetails;
import com.fiorano.openesb.application.service.Execution;
import com.fiorano.openesb.applicationcontroller.ApplicationController;
import com.fiorano.openesb.applicationcontroller.ApplicationHandle;
import com.fiorano.openesb.applicationcontroller.MicroServiceLaunchConfiguration;
import com.fiorano.openesb.applicationcontroller.MicroServiceLaunchConfiguration.ConfigurationConversionHelper;
import com.fiorano.openesb.microservice.launch.LaunchConfiguration;
import com.fiorano.openesb.transport.impl.jms.TransportConfig;
import com.fiorano.openesb.utils.exception.FioranoException;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static com.fiorano.openesb.microservice.launch.LaunchConfiguration.LaunchMode.*;

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
        ServerManagerHelper serverManagerHelper = new ServerManagerHelper();
        overview.setServerDetails(serverManagerHelper.getServerDetails());
        return overview;
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/config/{configType}")
    public Object getConfig(@PathParam("configType") String configType) {
        if ("server".equalsIgnoreCase(configType)) {
            com.fiorano.openesb.application.ServerConfig serverConfig = com.fiorano.openesb.application.ServerConfig.getConfig();
            ServerConfig localServerConfig = new ServerConfig();
            localServerConfig.setApplicationStateRestoreWaitTime(serverConfig.getApplicationStateRestoreWaitTime());
            localServerConfig.setCCPTimeOut(serverConfig.getCCPTimeOut());
            localServerConfig.setRepositoryPath(serverConfig.getRepositoryPath());
            localServerConfig.setRuntimeDataPath(serverConfig.getRuntimeDataPath());
            return localServerConfig;
        } else if ("transport".equalsIgnoreCase(configType)) {
            TransportConfig transportConfig = TransportConfig.getInstance();
            com.fiorano.openesb.management.TransportConfig localTransportConfig = new com.fiorano.openesb.management.TransportConfig();
            localTransportConfig.setBrokerURL(transportConfig.getBrokerURL());
            localTransportConfig.setJmxURL(transportConfig.getJmxURL());
            localTransportConfig.setPassword(transportConfig.getPassword());
            localTransportConfig.setUserName(transportConfig.getUserName());
            localTransportConfig.setProviderURL(transportConfig.getProviderURL());
            return localTransportConfig;
        } else {
            Response response = new Response();
            response.setMessage("Configuration lookup failed");
            response.setStatus(false);
            return response;
        }

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/serverConfig")
    public Object updateServerConfig(ServerConfig localserverConfig) {
        Response response = new Response();
        try {
            com.fiorano.openesb.application.ServerConfig serverConfig = com.fiorano.openesb.application.ServerConfig.getConfig();
            serverConfig.setRepositoryPath(localserverConfig.getRepositoryPath());
            serverConfig.setApplicationStateRestoreWaitTime(String.valueOf(localserverConfig.getApplicationStateRestoreWaitTime()));
            serverConfig.setCCPTimeOut(String.valueOf(localserverConfig.getCCPTimeOut()));
            serverConfig.setRuntimeDataPath(localserverConfig.getRuntimeDataPath());
            serverConfig.saveConfig();
            response.setMessage("Server Config Updated");
            response.setStatus(true);
            return response;
        } catch (Exception e) {
            response.setMessage("Error while updating server config :- " + e.getMessage());
            response.setStatus(false);
            return response;
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/transportConfig")
    public Object updateTransportConfig(com.fiorano.openesb.management.TransportConfig localTransportConfig) {
        Response response = new Response();
        try {
            TransportConfig transportConfig = TransportConfig.getInstance();
            transportConfig.setbrokerURL(localTransportConfig.getBrokerURL());
            transportConfig.setjmxURL(localTransportConfig.getJmxURL());
            transportConfig.setuserName(localTransportConfig.getUserName());
            transportConfig.setpassword(localTransportConfig.getPassword());
            transportConfig.setproviderURL(localTransportConfig.getProviderURL());
            transportConfig.saveConfig();
            response.setMessage("Transport Config Updated");
            response.setStatus(true);
            return response;
        } catch (Exception e) {
            response.setMessage("Error while updating transport config :- " + e.getMessage());
            response.setStatus(false);
            return response;
        }
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/onfig")
    public void setConfig(ServerConfig config) {
    }

    @Path("/applications/{applicationName}/{applicationVersion}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Object getApplicationDetails(@PathParam("applicationName") String applicationName,
                                        @PathParam("applicationVersion") String applicationVersion) {
        ApplicationRepository applicationRepository = getApplicationRepository();
        try {
            ApplicationStateDetails stateOfApplication = getController().getCurrentStateOfApplication(applicationName, Float.parseFloat(applicationVersion), null);
            com.fiorano.openesb.management.Application application = new com.fiorano.openesb.management.Application();

            List<Microservice> services = new ArrayList<>();
            @SuppressWarnings("unchecked") Enumeration<String> serviceNames = stateOfApplication.getAllServiceNames();
            while (serviceNames.hasMoreElements()) {
                String service = serviceNames.nextElement();
                ServiceInstanceStateDetails serviceInstance = stateOfApplication.getServiceStatus(service);
                Microservice microservice = new Microservice();
                microservice.setGuid(serviceInstance.getServiceGUID());
                microservice.setName(serviceInstance.getServiceInstanceName());
                microservice.setVersion(String.valueOf(serviceInstance.getRunningVersion()));
                boolean microserviceRunning = getController().isMicroserviceRunning(applicationName, applicationVersion, serviceInstance.getServiceInstanceName(), null);
                microservice.setRunning(microserviceRunning);
                microservice.setLaunchMode(ConfigurationConversionHelper.convertLaunchMode(serviceInstance.getLaunchType()).name());
                services.add(microservice);
            }
            application.setServices(services);
            application.setId(stateOfApplication.getAppGUID());
            application.setName(stateOfApplication.getDisplayName());
            application.setVersion(applicationVersion);
            application.setIsRunning(getController().isApplicationRunning(applicationName, Float.parseFloat(applicationVersion), null));
            return application;
        } catch (Exception e) {
            Response response = new Response();
            response.setStatus(false);
            response.setMessage(e.getMessage());
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
                controller.synchronizeApplication(applicationName, applicationVersion, null);
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