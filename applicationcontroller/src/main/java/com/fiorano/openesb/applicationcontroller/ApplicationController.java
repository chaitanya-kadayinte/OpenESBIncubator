package com.fiorano.openesb.applicationcontroller;

import com.fiorano.openesb.application.ApplicationRepository;
import com.fiorano.openesb.application.application.*;
import com.fiorano.openesb.application.aps.ApplicationStateDetails;
import com.fiorano.openesb.events.ApplicationEvent;
import com.fiorano.openesb.events.Event;
import com.fiorano.openesb.events.EventsManager;
import com.fiorano.openesb.microservice.ccp.CCPEventManager;
import com.fiorano.openesb.microservice.ccp.IEventListener;
import com.fiorano.openesb.microservice.ccp.event.CCPEventType;
import com.fiorano.openesb.microservice.ccp.event.ComponentCCPEvent;
import com.fiorano.openesb.microservice.ccp.event.ControlEvent;
import com.fiorano.openesb.microservice.ccp.event.common.DataEvent;
import com.fiorano.openesb.microservice.ccp.event.common.DataRequestEvent;
import com.fiorano.openesb.microservice.ccp.event.common.data.Data;
import com.fiorano.openesb.microservice.ccp.event.common.data.MicroserviceConfiguration;
import com.fiorano.openesb.microservice.launch.impl.MicroServiceLauncher;
import com.fiorano.openesb.route.RouteConfiguration;
import com.fiorano.openesb.route.RouteService;
import com.fiorano.openesb.transport.TransportService;
import com.fiorano.openesb.utils.Constants;
import com.fiorano.openesb.utils.exception.FioranoException;
import com.fiorano.openesb.security.SecurityManager;
import org.osgi.framework.BundleContext;

import java.io.File;
import java.util.*;

public class ApplicationController {
    private TransportService transport;
    private ApplicationRepository applicationRepository;
    private MicroServiceLauncher microServiceLauncher;
    private Map<String, ApplicationHandle> applicationHandleMap = new HashMap<>();
    private Map<String, Application> savedApplicationMap = new HashMap<>();
    private RouteService routeService;
    private SecurityManager securityManager;
    private EventsManager eventsManager;

    //To store the list of applications referring particular component(key) and values(Event_Process)
    private HashMap<String, Set<String>> COMPONENTS_REFERRING_APPS;

    //To store the set of applications referring particular
    private HashMap<String,Set<String>> REFERRING_APPS_LIST;

    //To store the list of applications GUID__Versions which the 'key' application is depends on
    private HashMap<String, Set<String>> DEPEND_APP_LIST;

    public ApplicationController(ApplicationRepository applicationRepository, BundleContext context) throws Exception {
        this.applicationRepository = applicationRepository;
        routeService = context.getService(context.getServiceReference(RouteService.class));
        microServiceLauncher = context.getService(context.getServiceReference(MicroServiceLauncher.class));
        eventsManager = context.getService(context.getServiceReference(EventsManager.class));
        CCPEventManager ccpEventManager = context.getService(context.getServiceReference(CCPEventManager.class));
        registerConfigRequestListener(ccpEventManager);
        transport = context.getService(context.getServiceReference(TransportService.class));
        securityManager = context.getService(context.getServiceReference(SecurityManager.class));
        COMPONENTS_REFERRING_APPS = new HashMap<String, Set<String>>(Constants.INITIAL_CAPACITY);
        REFERRING_APPS_LIST = new HashMap<String, Set<String>>(Constants.INITIAL_CAPACITY);
        DEPEND_APP_LIST = new HashMap<String, Set<String>>(Constants.INITIAL_CAPACITY);
        String [] appIds = applicationRepository.getApplicationIds();
        for(String appid:appIds){
           float[] appVersions = applicationRepository.getAppVersions(appid);
            for(float ver : appVersions){
                Application application = applicationRepository.readApplication(appid, String.valueOf(ver));
               savedApplicationMap.put(appid + "__" + ver, application);
                if (cyclicDependencyExists(application)) {//for app that are already in the repo before fix 25838
                   // logger.error(Bundle.class, Bundle.ERROR_CYCLIC_DEPENDENCY_REFERRED_APPS, application.getGUID(), String.valueOf(application.getVersion()));
                } else {
                    updateChainLaunchDS(application);
                }
            }
        }
    }

    private void registerConfigRequestListener(final CCPEventManager ccpEventManager) throws Exception {
        ccpEventManager.registerListener(new IEventListener() {
            @Override
            public void onEvent(ComponentCCPEvent event) throws Exception {
                ControlEvent controlEvent = event.getControlEvent();

                if(controlEvent instanceof DataRequestEvent && controlEvent.isReplyNeeded()) {
                    for(DataRequestEvent.DataIdentifier request: ((DataRequestEvent) controlEvent).getDataIdentifiers()) {
                        if(request == DataRequestEvent.DataIdentifier.COMPONENT_CONFIGURATION) {
                            DataEvent dataEvent = new DataEvent();
                            MicroserviceConfiguration microserviceConfiguration = new MicroserviceConfiguration();
                            Application application = applicationRepository.readApplication(getAppName(event), getAppVersion(event));
                            String configuration = application.getServiceInstance(getInstanceName(event)).getConfiguration();
                            microserviceConfiguration.setConfiguration(configuration);
                            Map<DataRequestEvent.DataIdentifier,Data> data = new HashMap<>();
                            data.put(request,microserviceConfiguration);
                            dataEvent.setData(data);
                            ccpEventManager.getCcpEventGenerator().sendEvent(dataEvent,event.getComponentId());
                            break;
                        }
                    }
                }
            }

            @Override
            public String getId() {
                return "Components' Requests Listener";
            }
        }, CCPEventType.DATA_REQUEST);
    }

    private String getAppName(ComponentCCPEvent event) {
        String componentId = event.getComponentId();
        return componentId.substring(0, componentId.indexOf("__"));
    }
    private String getAppVersion(ComponentCCPEvent event) {
        String componentId = event.getComponentId();
        return componentId.substring(componentId.indexOf("__") + 2,componentId.lastIndexOf("__")).replace("_", ".");
    }
    private String getInstanceName(ComponentCCPEvent event) {
        String componentId = event.getComponentId();
        return componentId.substring(componentId.lastIndexOf("__") + 2);
    }

    public void saveApplication(File appFileFolder, String handleID, byte[] zippedContents) throws FioranoException {
        String userName = securityManager.getUserName(handleID);
        System.out.println("saving Application");
        Application application = null;
        application = ApplicationParser.readApplication(appFileFolder, Application.Label.none.toString(), false, false);
        try {
            application.validate();
        } catch (FioranoException e3){
            //this would led some corrupted application to enter into the repository, which could be deleted
            e3.printStackTrace();
            //we can fail this step if we want
        }
        String appGuid = application.getGUID();
        float version = application.getVersion();
        // boolean applicationExists = applicationRepository.applicationExists(appGuid, version);
        applicationRepository.saveApplication(application, appFileFolder, userName, zippedContents, handleID);
        savedApplicationMap.put(application.getGUID() + Constants.NAME_DELIMITER + application.getVersion(), application);
        ApplicationEventRaiser.generateApplicationEvent(ApplicationEvent.ApplicationEventType.APPLICATION_SAVED, Event.EventCategory.INFORMATION,
                appGuid, application.getDisplayName(),String.valueOf(version), "Application saved Successfully");
    }

    public void saveApplication(Application application, boolean skipManagableProps, String handleID) throws FioranoException {
        String userName = securityManager.getUserName(handleID);
        boolean applicationExists = applicationRepository.applicationExists(application.getGUID(), application.getVersion());
        boolean applicationAnyVersionExists=applicationRepository.applicationExists(application.getGUID(),-1);

        Application oldApp = applicationRepository.readApplication(application.getGUID(), String.valueOf(application.getVersion()));

            try {
                applicationRepository.saveApplication(application, userName, handleID, skipManagableProps);
            } catch (FioranoException e) {
                throw e;
            }

        if (oldApp != null) {
            Vector<String> deletedComponents = new Vector<String>();
            Vector<String> deletedConfigComponents = new Vector<String>();
            HashMap<String, String> deletedPorts = new HashMap<String, String>();

            for (Object o1 : oldApp.getServiceInstances()) {
                boolean check = false;
                ServiceInstance oldInst = (ServiceInstance) o1;

                for (Object o : application.getServiceInstances()) {
                    ServiceInstance newInst = (ServiceInstance) o;
                    if (oldInst.getName().equals(newInst.getName())) {
                        check = false;

                        List<OutputPortInstance> oldOutputPortInstances = oldInst.getOutputPortInstances();
                        for (OutputPortInstance oldPort : oldOutputPortInstances) {
                            boolean deletePort = false;

                            List<OutputPortInstance> newOutputPortInstances = newInst.getOutputPortInstances();
                            for (OutputPortInstance newPort : newOutputPortInstances) {
                                if (oldPort.getName().equals(newPort.getName())) {
                                    deletePort = false;
                                    break;
                                } else {
                                    deletePort = true;
                                }
                            }

                            if (newInst.getOutputPortInstances().size() == 0)
                                deletePort = true;

                            if (deletePort)
                                deletedPorts.put(oldInst.getName(), oldPort.getName());
                        }

                        break;
                    } else {
                        check = true;
                    }
                }

                if (application.getServiceInstances().size() == 0) {
                    check = true;
                }

                if (check) {
                    deletedConfigComponents.add(oldInst.getName());
                    String[] nodes = oldInst.getNodes();
                    if (nodes != null)
                        deletedComponents.add(oldInst.getName());
                }
            }

            //Bug 16340
            if (!deletedConfigComponents.isEmpty())
                deleteConfigurations(oldApp, deletedConfigComponents);

            //Fix for bug 20434
            if (!deletedComponents.isEmpty()) {
                deleteInPort(oldApp, deletedConfigComponents);
            }

            if (!deletedPorts.isEmpty())
                deletePortTransformations(oldApp, deletedPorts);

            Vector<String> deletedRoutes = new Vector<String>();
            for (Route oldRoute : oldApp.getRoutes()) {
                boolean check = false;

                for (Route newRoute : application.getRoutes()) {
                    if (oldRoute.getName().equals(newRoute.getName())) {
                        check = false;
                        break;
                    } else {
                        check = true;
                    }
                }

                if (application.getRoutes().size() == 0)
                    check = true;

                if (check)
                    deletedRoutes.add(oldRoute.getName());
            }

            if (!deletedRoutes.isEmpty())
                deleteRouteConfigurations(oldApp, deletedRoutes);
        }
        ApplicationHandle appHandle = getApplicationHandle(application.getGUID(), application.getVersion(), handleID);
        if (appHandle != null) {
            appHandle.setApplication(application);
        }
        savedApplicationMap.put(application.getGUID() + Constants.NAME_DELIMITER + application.getVersion(), application);
        ApplicationEventRaiser.generateApplicationEvent(ApplicationEvent.ApplicationEventType.APPLICATION_SAVED, Event.EventCategory.INFORMATION,
                application.getGUID(), application.getDisplayName(), String.valueOf(application.getVersion()), "Application saved Successfully");
    }

    /**
     * Deletes port transformations for specified ports. This method is used when some of the output ports of a service
     * instance are deleted at the time of orchestration. For example, deleting an output port of CBR component.
     *
     * @param oldApp       Application DMI
     * @param deletedPorts HashMap of (ServiceInstanceName, PortName)
     */
    private void deletePortTransformations(Application oldApp, HashMap<String, String> deletedPorts) {
        //todo
    }

    private void deleteConfigurations(Application oldApp, Vector<String> deletedConfigComponents) {
        //Todo
    }


    /**
     * delete InPort when running components are deleted
     *
     * @param oldApp oldApplication dmi
     * @param deletedComponent deleted components
     */
    private void deleteInPort(Application oldApp, Vector<String> deletedComponent) {
        //todo
    }


    private void deleteRouteConfigurations(Application oldApp, Vector<String> deletedRoutes) {
        //todo
    }

    private void deleteLogs(Application application, Vector<String> components) {
        //todo
    }

    public Set<String> getListOfRunningApplications(String handleId){
        return applicationHandleMap.keySet();
    }

    public boolean launchApplication(String appGuid, String version, String handleID) throws Exception {
        System.out.println("Launching application : " + appGuid + ":" + version);

        Map<String, Boolean> orderedListOfApplications = getApplicationChainForLaunch(appGuid, Float.parseFloat(version), handleID);
        for (String app_version: orderedListOfApplications.keySet()) {
            String[] current_AppGUIDAndVersion = returnAppGUIDAndVersion(app_version);
            String currentGUID = current_AppGUIDAndVersion[0];
            Float currentVersion = Float.valueOf(current_AppGUIDAndVersion[1]);
            Application currentApplication = applicationRepository.readApplication(currentGUID, String.valueOf(currentVersion));
            if (!isApplicationRunning(currentGUID, currentVersion, handleID)) {
                    ApplicationHandle appHandle = new ApplicationHandle(this, currentApplication, microServiceLauncher, routeService,transport);
                    appHandle.createRoutes();
                ApplicationEventRaiser.generateApplicationEvent(ApplicationEvent.ApplicationEventType.APPLICATION_LAUNCHED, Event.EventCategory.INFORMATION,
                        currentGUID, currentApplication.getDisplayName(), current_AppGUIDAndVersion[1], "Application launched Successfully");

                appHandle.launchComponents();
                    applicationHandleMap.put(app_version,appHandle);
                    System.out.println("Launched application: "+currentGUID+":"+current_AppGUIDAndVersion[1]);
            }
        }
        return true;
    }

    public boolean stopApplication(String appGuid, String version, String handleID) throws Exception {
        System.out.println("Stopping application: "+appGuid+":"+version);
        Map<String, Boolean> orderedListOfApplications = getApplicationChainForShutdown(appGuid, Float.parseFloat(version), handleID);
        orderedListOfApplications.put( appGuid +  Constants.NAME_DELIMITER + version, isApplicationRunning(appGuid, Float.parseFloat(version), handleID));
        for (String app_version: orderedListOfApplications.keySet()) {
            String[] appGUIDAndVersion = returnAppGUIDAndVersion(app_version);
            String currentGUID = appGUIDAndVersion[0];
            Float currentVersion = Float.valueOf(appGUIDAndVersion[1]);
            if (isApplicationRunning(currentGUID, currentVersion, handleID)) {
                ApplicationStateDetails asd;
                ApplicationHandle applicationHandle = getApplicationHandle(currentGUID, currentVersion, handleID);
                applicationHandle.stopApplication();
                applicationHandleMap.remove(app_version);
                ApplicationEventRaiser.generateApplicationEvent(ApplicationEvent.ApplicationEventType.APPLICATION_STOPPED, Event.EventCategory.INFORMATION,
                        currentGUID, applicationHandle.getApplication().getDisplayName(), String.valueOf(currentVersion), "Application stopped Successfully");
            }
        }
        System.out.println("Stopped application: "+appGuid+":"+version);
        return true;
    }

    private String getKey(String appGuid, String version) {
        return appGuid+ ":"+ version;
    }

    public boolean synchronizeApplication(String appGuid, String version, String handleID) throws FioranoException{
        return false;
    }

    public boolean startAllMicroServices(String appGuid, String version, String handleID) throws FioranoException{
        String key = appGuid+Constants.NAME_DELIMITER+version;
        if(applicationHandleMap.containsKey(key)){
            ApplicationHandle appHandle = applicationHandleMap.get(key);
            appHandle.launchComponents();
        }
        return true;
    }

    public boolean stopAllMicroServices(String appGuid, String version, String handleID) throws FioranoException{
        String key = appGuid+Constants.NAME_DELIMITER+version;
        if(applicationHandleMap.containsKey(key)){
            ApplicationHandle appHandle = applicationHandleMap.get(key);
            appHandle.stopAllMicroServices();
            return true;
        }
        return false;
    }

    public boolean startMicroService(String appGuid, String version, String microServiceName, String handleID) throws FioranoException{
        String key = appGuid+Constants.NAME_DELIMITER+version;
        if(applicationHandleMap.containsKey(key)){
            ApplicationHandle appHandle = applicationHandleMap.get(key);
            appHandle.startMicroService(microServiceName);
            return true;
        }
        return false;
    }

    public boolean isMicroserviceRunning(String appGuid, String version, String microServiceName, String handleID) throws FioranoException{
        String key = appGuid+Constants.NAME_DELIMITER+version;
        if(applicationHandleMap.containsKey(key)){
            ApplicationHandle appHandle = applicationHandleMap.get(key);
            return appHandle.isMicroserviceRunning(microServiceName);
        }
        return false;
    }

    public boolean stopMicroService(String appGuid, String version, String microServiceName, String handleID) throws FioranoException{
        String key = appGuid+Constants.NAME_DELIMITER+version;
        if(applicationHandleMap.containsKey(key)){
            ApplicationHandle appHandle = applicationHandleMap.get(key);
            appHandle.stopMicroService(microServiceName);
            return true;
        }
        return false;
    }

    public void deleteApplication(String appGUID, String version, String handleID) throws FioranoException {
        if(applicationHandleMap.containsKey(appGUID+"__"+version)){
            throw new FioranoException("Cannot delete running Application. Stop the Application and then delete");
        }
        applicationRepository.deleteApplication(appGUID, version);
        ApplicationEventRaiser.generateApplicationEvent(ApplicationEvent.ApplicationEventType.APPLICATION_DELETED, Event.EventCategory.INFORMATION,
                appGUID, null, version, "Application Deleted Successfully");

    }

    public ApplicationHandle getApplicationHandle(String appGUID, float appVersion, String handleID) {
        return applicationHandleMap.get(appGUID+"__"+appVersion);
    }
    public boolean isApplicationRunning(String appGUID, float version, String handleID) throws FioranoException {
        return (getApplicationHandle(appGUID, version, handleID) != null);
    }

    public void changePortAppContext(String appGUID, float appVersion, String serviceName, String portName, String scriptContent, String jmsScriptContent, String transformerType, String projectContent, String handleId) throws FioranoException{


    }

    public void changePortAppContextConfiguration(String appGUID, float appVersion, String serviceName, String portName, String configurationName, String handleId) throws FioranoException{

    }

    public void changeRouteTransformation(String appGUID, float appVersion, String routeGUID, String scriptContent, String jmsScriptContent, String transformerType, String projectContent, String handleId) throws FioranoException{
    }

    public void changeRouteTransformationConfiguration(String appGUID, float appVersion, String routeGUID, String configurationName, String handleId) throws FioranoException{
    }

    public Enumeration<ApplicationReference> getHeadersOfRunningApplications(String handleId) throws FioranoException{
        Vector<ApplicationReference> toReturn = new Vector<ApplicationReference>();
        // get the running application handles and fetch the application info packet from the handles.
        for (ApplicationHandle appHandle:applicationHandleMap.values()) {

            try {
                toReturn.addElement(new ApplicationReference(appHandle.getApplication()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return toReturn.elements();
    }

    public Enumeration<ApplicationReference> getHeadersOfSavedApplications(String handleId) throws FioranoException{
        Vector<ApplicationReference> toReturn = new Vector<ApplicationReference>();
        // get the running application handles and fetch the application info packet from the handles.
        for (Application app:savedApplicationMap.values()) {

            try {
                toReturn.addElement(new ApplicationReference(app));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return toReturn.elements();
    }

    public void deleteMicroService(String appGUID, float appVersion, String serviceInstanceName, String handleId)  throws FioranoException{

    }

    public Map<String, Boolean> getApplicationChainForShutdown(String appGUID, float version, String handleId) throws FioranoException{
        Set<String> applicationChain = new LinkedHashSet<String>();
        String app_version = appGUID + Constants.NAME_DELIMITER + version;
        populateReferringList(app_version, applicationChain);
        applicationChain.add(app_version);
        Map<String, Boolean> result = new LinkedHashMap<String, Boolean>();
        for (String appDetails : applicationChain){
            String[] details = returnAppGUIDAndVersion(appDetails);
            String GUID = details[0];
            result.put(appDetails, isApplicationRunning(GUID, Float.valueOf(details[1]), handleId));
        }
        return result;
    }

    public Map<String, Boolean> getApplicationChainForLaunch(String appGUID, float appVersion, String handleId) throws FioranoException{
        Set<String> orderedListOfApplicationsForLaunch = new  LinkedHashSet<String>();
        String appGUID_version = appGUID + Constants.NAME_DELIMITER + appVersion;
        orderedListOfApplicationsForLaunch = populateDependencyList(appGUID_version, orderedListOfApplicationsForLaunch, appGUID_version);
        orderedListOfApplicationsForLaunch.add(appGUID_version);
        Map<String, Boolean> result = new LinkedHashMap<String, Boolean>();
        for (String app_version : orderedListOfApplicationsForLaunch){
            String[] appDetails = returnAppGUIDAndVersion(app_version);
            result.put(app_version, isApplicationRunning(appDetails[0], Float.valueOf(appDetails[1]), handleId));
        }
        return result;
    }

    public void checkResourceAndConnectivity(String appGUID, float version, String handleId) throws FioranoException{

    }

    public ApplicationStateDetails getCurrentStateOfApplication(String appGUID, float appVersion, String handleId) throws FioranoException{
        ApplicationHandle appHandle = getApplicationHandle(appGUID, appVersion, handleId);
        if (appHandle == null) {
            return new ApplicationStateDetails();
        }
        return appHandle.getApplicationDetails(handleId);

    }

    public ApplicationReference getHeaderOfSavedApplication(String appGUID, float version, String handleId) {
        return savedApplicationMap.get(appGUID+"__"+version);
    }

    public Set<String> getReferringRunningApplications(String appGUID, float appVersion, String servInstName) throws FioranoException{
        Set<String> appGUIDSreferring = new HashSet<String>();

        for (ApplicationHandle handle: applicationHandleMap.values()) {

            Application application = handle.getApplication();
            for (Object o : application.getRemoteServiceInstances()) {
                RemoteServiceInstance extInstance = (RemoteServiceInstance) o;
                if (extInstance.getApplicationGUID().equalsIgnoreCase(appGUID) &&
                        extInstance.getApplicationVersion() == appVersion &&
                        extInstance.getRemoteName().equalsIgnoreCase(servInstName))
                    appGUIDSreferring.add(application.getGUID() + Constants.NAME_DELIMITER + application.getVersion());
            }
        }
        return appGUIDSreferring;
    }

    public Set<String> getAllReferringApplications(String appGUID, float appVersion, String serviceInstName) throws FioranoException{
        String searchKey = appGUID + Constants.NAME_DELIMITER + appVersion  + Constants.NAME_DELIMITER + serviceInstName;
        if (COMPONENTS_REFERRING_APPS.containsKey(searchKey)){
            return COMPONENTS_REFERRING_APPS.get(searchKey);
        }
        return COMPONENTS_REFERRING_APPS.get(searchKey);
    }

    public boolean isApplicationReferred(String appGUID, float appVersion) throws FioranoException{
        boolean isAppReferred = false;
        //check if any application depends on this appGUID
        Set<String> setOfApplicationDependsOnThisAppGUID = REFERRING_APPS_LIST.get(appGUID + Constants.NAME_DELIMITER + appVersion);
        if (setOfApplicationDependsOnThisAppGUID != null && !setOfApplicationDependsOnThisAppGUID.isEmpty()){
            isAppReferred = true;
        }
        return isAppReferred;
    }

    /**
     * update chain launch data structures .
     * @param application application dmi
     */
    private void updateChainLaunchDS(Application application) {
       // logger.debug(Bundle.class, Bundle.EXECUTING_CALL, "updateApplicationReferringApps(" + application.getGUID() + ")");
        List remoteServiceInstances = application.getRemoteServiceInstances();
        if (!remoteServiceInstances.isEmpty()) {
            String app_version = application.getGUID() + Constants.NAME_DELIMITER + application.getVersion();
            for (Object obj : remoteServiceInstances) {
                RemoteServiceInstance oldRemote = (RemoteServiceInstance) obj;
                String key = oldRemote.getApplicationGUID() + Constants.NAME_DELIMITER + oldRemote.getApplicationVersion();
                String referredComponent = key + Constants.NAME_DELIMITER + oldRemote.getRemoteName() ;
                if (REFERRING_APPS_LIST.containsKey(key)) {
                    REFERRING_APPS_LIST.get(key).add(app_version);
                } else {
                    Set<String> referringApps = new LinkedHashSet<String>();
                    referringApps.add(app_version);
                    REFERRING_APPS_LIST.put(key, referringApps);
                }

                if (DEPEND_APP_LIST.containsKey(app_version)) {
                    DEPEND_APP_LIST.get(app_version).add(key);
                } else {
                    Set<String> dependsList = new LinkedHashSet<String>();
                    dependsList.add(key);
                    DEPEND_APP_LIST.put(app_version, dependsList);
                }
                //COMP_REF DETAILS CAN BE ADDED HERE instead of Save()
                if (COMPONENTS_REFERRING_APPS.containsKey(referredComponent)) {
                    COMPONENTS_REFERRING_APPS.get(referredComponent).add(app_version);
                } else {
                    LinkedHashSet<String> referringApps = new LinkedHashSet<String>();
                    referringApps.add(app_version);
                    COMPONENTS_REFERRING_APPS.put(referredComponent, referringApps);
                }
            }
        }
    }

    /**
     * cleanup chain launch and chain shutdown data structures
     * @param oldApplication oldApplication
     */
    private void removeChainLaunchDS(Application oldApplication) {
        removeChainLaunchDS(oldApplication.getGUID()+ Constants.NAME_DELIMITER+oldApplication.getVersion());
    }
    /**
     * cleanup chain launch and chain shutdown data structures
     * @param oldAppVersion oldAppVersion
     */
    private void removeChainLaunchDS(String oldAppVersion){
        //Remove form  depend list
        DEPEND_APP_LIST.remove(oldAppVersion );

        //remove application in referring apps list
        if(REFERRING_APPS_LIST.size() >0){
            Set<String> allReferredApps = REFERRING_APPS_LIST.keySet() ;
            for( String eachApp : allReferredApps ){
                REFERRING_APPS_LIST.get(eachApp).remove(oldAppVersion);
            }
        }
        //Remove application from component referring list
        if(COMPONENTS_REFERRING_APPS.size() >0){
            Set<String> allReferredApps = COMPONENTS_REFERRING_APPS.keySet() ;
            for(String eachApp : allReferredApps )   {
                COMPONENTS_REFERRING_APPS.get(eachApp ).remove(oldAppVersion);
            }
        }
    }

    public boolean cyclicDependencyExists(Application application) throws FioranoException {
        boolean cyclicDependency = false;
        String actualAppGUID = application.getGUID();
        Set<String> remoteList = new LinkedHashSet<String>();
        if (application.getRemoteServiceInstances().isEmpty())
            return cyclicDependency;
        String app_version = actualAppGUID + Constants.NAME_DELIMITER + application.getVersion();
        for (Object eachRemoteInstance : application.getRemoteServiceInstances()) {
            RemoteServiceInstance oldRemote = (RemoteServiceInstance) eachRemoteInstance;
            String key = oldRemote.getApplicationGUID() + Constants.NAME_DELIMITER + oldRemote.getApplicationVersion();
            remoteList.add(key);
            populateDependencyList(key, remoteList, app_version);
        }
        if (remoteList.contains(app_version))
            cyclicDependency = true;
        return cyclicDependency;
    }

    private Set<String> populateDependencyList(String appGUID_Version, Set<String> remoteList, String originalApp){
        if (DEPEND_APP_LIST.containsKey(appGUID_Version)){
            for(String currentApp_Version : DEPEND_APP_LIST.get(appGUID_Version) ){
                if(! (currentApp_Version.equalsIgnoreCase(originalApp) || (currentApp_Version.equalsIgnoreCase(appGUID_Version))) ) {
                    populateDependencyList(currentApp_Version, remoteList, originalApp);
                }
                remoteList.add(currentApp_Version);
            }
        }
        return remoteList;
    }

    private Set<String> populateReferringList(String app_version, Set<String> remoteList){
        if(REFERRING_APPS_LIST .containsKey(app_version) ){
            Set<String> referringList = REFERRING_APPS_LIST.get(app_version);
            for (String currentAppGUID_Version: referringList){
                populateReferringList(currentAppGUID_Version, remoteList);
                if (!remoteList.contains(currentAppGUID_Version))
                    remoteList.add(currentAppGUID_Version);
            }
        }
        return remoteList;
    }

    public static String[] returnAppGUIDAndVersion(String app_version){
        if (app_version == null)
            return  null;
        String[] result = new String[2];
        int lastIndexOfDelim = app_version.lastIndexOf(Constants.NAME_DELIMITER);
        result[0] = app_version.substring(0, lastIndexOfDelim);
        result[1] = app_version.substring(lastIndexOfDelim+2);
        return result;
    }

}
