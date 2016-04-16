
package com.fiorano.openesb.management;


public interface ApplicationsService {


    Response getApplications();

    Response performApplicationAction(String applicationName , String applicationVersion, Action action);

    Response performMicroServiceAction(String appGuid, String version, String microServiceName, Action action);

}
