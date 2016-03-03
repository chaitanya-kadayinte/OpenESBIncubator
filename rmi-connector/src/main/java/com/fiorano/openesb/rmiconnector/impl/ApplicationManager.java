package com.fiorano.openesb.rmiconnector.impl;

import com.fiorano.openesb.application.ApplicationRepository;
import com.fiorano.openesb.applicationcontroller.ApplicationController;
import com.fiorano.openesb.rmiconnector.api.*;
import com.fiorano.openesb.utils.Constants;
import com.fiorano.openesb.utils.FileUtil;
import com.fiorano.openesb.utils.ZipUtil;
import com.fiorano.openesb.utils.exception.FioranoException;

import java.io.*;
import java.rmi.RemoteException;
import java.util.*;
import java.util.zip.ZipOutputStream;

/**
 * Created by Janardhan on 1/22/2016.
 */
public class ApplicationManager extends AbstractRmiManager implements IApplicationManager  {
    private ApplicationController applicationController;

    private ApplicationRepository applicationRepository;

    private IApplicationManager clientProxyInstance;

    private HashMap<String, File> tempFileNameMap = new HashMap<String, File>(8);

    void setClientProxyInstance(IApplicationManager clientProxyInstance) {
        this.clientProxyInstance = clientProxyInstance;
    }

    IApplicationManager getClientProxyInstance() {
        return clientProxyInstance;
    }

    ApplicationManager(RmiManager rmiManager, InstanceHandler instanceHandler){
        super(rmiManager);
        this.applicationController = rmiManager.getApplicationController();
        this.applicationRepository = rmiManager.getApplicationRepository();
        this.handleId = instanceHandler.getHandleID();
    }

    @Override
    public String[] getApplicationIds() throws RemoteException, ServiceException {
        return new String[0];
    }

    @Override
    public boolean exists(String id, float version) throws RemoteException, ServiceException {
        return false;
    }

    @Override
    public float[] getVersions(String id) throws RemoteException, ServiceException {
        return new float[0];
    }

    public void saveApplication(byte[] zippedContents, boolean completed) throws RemoteException, ServiceException {
        //Check the validity of the connection
        validateHandleID(handleId, "Save Application");
        String key = handleId + "__SAVEAPP";
        File tempZipFile = null;
        FileOutputStream outstream = null;
        File appFileTempFolder = null;
        boolean successfulzip = true;

        //get the Application zip as byte array from server. keep writing to a zip file until client notifies completed
        try {
            tempZipFile = tempFileNameMap.get(key);
            if (tempZipFile == null) {
                tempZipFile = getTempFile("Application", "zip");
                tempFileNameMap.put(key, tempZipFile);
            }
            outstream = new FileOutputStream(tempZipFile, true);
            outstream.write(zippedContents);
        } catch (IOException ioe) {
            successfulzip = false;
            ioe.printStackTrace();
            throw new ServiceException(ioe.getMessage());
        }
        finally {
            try {
                if (outstream != null) {
                    outstream.close();
                }
                if (!successfulzip && tempZipFile != null) {
                    tempZipFile.delete();
                }
            } catch (IOException e) {
                //ignore
            }
        }
        if (!completed) {
            return;
        }

        //extract contents.
        boolean successfulextract = true;
        try {
            appFileTempFolder = getTempFile("application", "tmp");
            appFileTempFolder.mkdir();
            //extractZip(appFileTempFolder, tempZipFile);
            ZipUtil.unzip(tempZipFile, appFileTempFolder);
        } catch (Exception e) {
            successfulextract = false;
//            LogHelper.log("Unable to save event flow process::Error occured while extracting zipped contents.", e);
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }
        finally {
            //Removing the temporary zip entry in hashmap. and deleteing the file
            if (!successfulextract && appFileTempFolder != null) {
                FileUtil.deleteDir(appFileTempFolder);
                tempFileNameMap.remove(key);
                tempZipFile.delete();
            }
        }

        //save to fes repository.
        try {
            applicationController.saveApplication(appFileTempFolder, handleId, getBytesFromFile(tempZipFile));
        } catch (FioranoException e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }
        finally {
            // Temporaray application Folder is being deleted in Application Repository Code
//            FileUtil.deleteDir(appFileTempFolder);
            tempFileNameMap.remove(key);
            tempZipFile.delete();
        }
    }

    @Override
    public void changeRouteTransformation(String appGUID, float appVersion, String routeGUID, String transformerType, byte[] transformationProject, boolean completed, String scriptFile, String jmsScriptFile, String projectDir) throws RemoteException, ServiceException {

    }

    @Override
    public void changeRouteTransformationConfiguration(String appGUID, float appVersion, String routeGUID, String configurationName) throws RemoteException, ServiceException {

    }

    @Override
    public void clearRouteTransformation(String appGUID, float appVersion, String routeGUID) throws RemoteException, ServiceException {

    }

    @Override
    public void changePortAppContext(String appGUID, float appVersion, String serviceName, String portName, String transformerType, byte[] appContextBytes, boolean completed, String scriptFileName, String jmsScriptFileName, String projectDirName) throws RemoteException, ServiceException {

    }

    @Override
    public void changePortAppContextConfiguration(String appGUID, float appVersion, String serviceName, String portName, String configurationName) throws RemoteException, ServiceException {

    }

    @Override
    public void clearPortAppContext(String appGUID, float appVersion, String serviceName, String portName) throws RemoteException, ServiceException {

    }

    @Override
    public byte[] getApplication(String appGUID, float version, long index) throws RemoteException, ServiceException {
        byte[] contents = new byte[0];
        //if Version is -1, get Highest Version and convert to String
        String versionString = String.valueOf(version);
        String eventProcessKey = appGUID.toUpperCase() + "__" + versionString + "__GETEP";
        File tempZipFile = null;
        BufferedInputStream bis = null;
        boolean completed = false;

        if (tempFileNameMap.get(eventProcessKey) == null) {
            File tempdir = null;
            ZipOutputStream zipStream = null;

            try {
                //Create Temporary Directory
                tempdir = getTempFile("application", "tmp");
                tempdir.mkdir();
                //if the Application to be Zipped in APPID/Version/AppFiles.. We need to add the Required directories as follows.
//                File appDir = new File(tempdir, appGUID + File.separator + versionString);
//                appDir.mkdirs();
                //fetch the Application directory from Application directory and copy the dir into Temporary location
                File appFile = applicationRepository.getAppDir(appGUID, Float.parseFloat(versionString));
                if (appFile == null)
                    return null;
                copyDirectory(appFile, tempdir);

                //Create Temporary zip file for the Application directory.
                tempZipFile = getTempFile("application", "zip");
                zipStream = new ZipOutputStream(new FileOutputStream(tempZipFile));
                ZipUtil.zipDir(tempdir, tempdir, zipStream);
                tempFileNameMap.put(eventProcessKey, tempZipFile);
            } catch (Exception e) {
                completed = true;
                //rmiLogger.error(Bundle.class, Bundle.ERROR_FETCHING_EVENT_PROCESS_FROM_REPOSITORY, appGUID, version, e);
                throw new ServiceException("ERROR_FETCHING_EVENT_PROCESS_FROM_REPOSITORY");
            }
            finally {
                try {
                    if (zipStream != null)
                        zipStream.close();
                    if (tempdir != null)
                        FileUtil.deleteDir(tempdir);
                    if (completed) {
                        tempFileNameMap.remove(eventProcessKey);
                        if (tempZipFile != null) {
                            tempZipFile.delete();
                        }
                    }
                } catch (IOException e) {
                    //ignore
                }
            }

        } else
            tempZipFile = tempFileNameMap.get(eventProcessKey);

        //Now we have Application Zip file, Read the contents of the Zip file by skipping till index.
        try {
            bis = new BufferedInputStream(new FileInputStream(tempZipFile));
            bis.skip(index);
            byte[] tempContents = new byte[Constants.CHUNK_SIZE];
            int readCount;
            readCount = bis.read(tempContents);
            if (readCount < 0) {
                completed = true;
                return null;
            }
            contents = new byte[readCount];
            System.arraycopy(tempContents, 0, contents, 0, readCount);
        } catch (IOException e) {
            completed = true;
            //rmiLogger.error(Bundle.class, Bundle.ERROR_SENDING_CONTENTS_OF_APP_ZIPFILE, appGUID, version, e);
            throw new ServiceException("ERROR_SENDING_CONTENTS_OF_APP_ZIPFILE");
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (completed) {
                    tempFileNameMap.remove(eventProcessKey);
                    if (tempZipFile != null) {
                        tempZipFile.delete();
                    }
                }
            } catch (IOException e) {
                //ignore
            }
        }
        return contents;
    }

    public void deleteApplication(String appGUID, String version, String handleID) throws RemoteException, ServiceException {
        try {
            applicationController.deleteApplication(appGUID, version, handleID);
        } catch (FioranoException e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public boolean dependenciesExists(ServiceReference[] serviceRefs, ApplicationReference[] applicationRefs) throws RemoteException, ServiceException {
        return false;
    }

    @Override
    public void startApplication(String appGUID, String version, String handleID) throws RemoteException, ServiceException {
        try {
            applicationController.launchApplication(appGUID, version, handleID);
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(),e);
        }
    }

    @Override
    public void restartApplication(String appGUID, float appVersion, String handleID) throws RemoteException, ServiceException {

    }

    @Override
    public void stopApplication(String appGUID, String version, String handleID) throws RemoteException, ServiceException {
        try {
            applicationController.stopApplication(appGUID,version, handleID);
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(),e);
        }
    }

    @Override
    public Map<String, Boolean> getApplicationChainForShutdown(String appGUID, float version) throws RemoteException, ServiceException {
        return null;
    }

    @Override
    public Map<String, Boolean> getApplicationChainForLaunch(String appGUID, float appVersion) throws RemoteException, ServiceException {
        return null;
    }

    @Override
    public void startServiceInstance(String appGUID, float appVersion, String serviceInstanceName) throws RemoteException, ServiceException {

    }

    @Override
    public void stopServiceInstance(String appGUID, float appVersion, String serviceInstanceName) throws RemoteException, ServiceException {

    }

    @Override
    public void stopAllServiceInstances(String appGUID, float appVersion) throws RemoteException, ServiceException {

    }

    @Override
    public void deleteServiceInstance(String appGUID, float appVersion, String serviceInstanceName) throws RemoteException, ServiceException {

    }

    @Override
    public ApplicationReference[] getRunningApplications() throws RemoteException, ServiceException {
        return new ApplicationReference[0];
    }

    @Override
    public List<RouteMetaData> getRoutesOfApplications(String appGUID, float version) throws RemoteException, ServiceException {
        return null;
    }

    @Override
    public List<PortInstanceMetaData> getPortsForApplications(String appGUID, float version) throws RemoteException, ServiceException {
        return null;
    }

    @Override
    public List<PortInstanceMetaData> getPortsForService(String appGUID, float version, String serviceInstName) throws RemoteException, ServiceException {
        return null;
    }

    @Override
    public List<ServiceInstanceMetaData> getServiceInstancesOfApp(String appGUID, float version) throws RemoteException, ServiceException {
        return null;
    }

    @Override
    public void addApplicationListener(IApplicationManagerListener listener, String appGUID, float appVersion) throws RemoteException, ServiceException {

    }

    @Override
    public void removeApplicationListener(IApplicationManagerListener listener, String appGUID, float appVersion) throws RemoteException, ServiceException {

    }

    @Override
    public void addRepositoryEventListener(IMicroServiceRepoEventListener listener) throws RemoteException, ServiceException {

    }

    @Override
    public void removeRepositoryEventListener() throws RemoteException, ServiceException {

    }

    @Override
    public boolean isRunning(String appGUID, float appVersion) throws RemoteException, ServiceException {
        return false;
    }

    @Override
    public void synchronizeApplication(String appGUID, float version) throws RemoteException, ServiceException {

    }

    @Override
    public void startAllServices(String appGUID, float version) throws RemoteException, ServiceException {

    }

    @Override
    public void checkResourcesAndConnectivity(String appGUID, float version) throws RemoteException, ServiceException {

    }

    @Override
    public String viewWSDL(String appGUID, float appVersion, String servInstName) throws RemoteException, ServiceException {
        return null;
    }

    @Override
    public String getComponentStats(String appGUID, float appVersion, String servInstName) throws ServiceException {
        return null;
    }

    @Override
    public void flushMessages(String appGUID, float appVersion, String servInstName) throws ServiceException {

    }

    @Override
    public String viewHttpContext(String appGUID, float appVersion, String servInstName) throws RemoteException, ServiceException {
        return null;
    }

    @Override
    public ApplicationStateData getApplicationStateDetails(String appGUID, float appVersion) throws RemoteException, ServiceException {
        return null;
    }

    @Override
    public ApplicationReference[] getAllApplications() throws RemoteException, ServiceException {
        return new ApplicationReference[0];
    }

    @Override
    public ApplicationReference getApplication(String appGUID, float version) throws RemoteException, ServiceException {
        return null;
    }

    @Override
    public String getLastOutTrace(int numberOfLines, String serviceName, String appGUID, float appVersion) throws RemoteException, ServiceException {
        return null;
    }

    @Override
    public String getLastErrTrace(int numberOfLines, String serviceName, String appGUID, float appVersion) throws RemoteException, ServiceException {
        return null;
    }

    @Override
    public void clearServiceOutLogs(String serviceInst, String appGUID, float appVersion) throws RemoteException, ServiceException {

    }

    @Override
    public void clearServiceErrLogs(String serviceInst, String appGUID, float appVersion) throws RemoteException, ServiceException {

    }

    @Override
    public void clearApplicationLogs(String appGUID, float appVersion) throws RemoteException, ServiceException {

    }

    @Override
    public byte[] exportServiceLogs(String appGUID, float version, String serviceInst, long index) throws RemoteException, ServiceException {
        return new byte[0];
    }

    @Override
    public byte[] exportApplicationLogs(String appGUID, float version, long index) throws RemoteException, ServiceException {
        return new byte[0];
    }

    @Override
    public void setLogLevel(String appGUID, float appVersion, String serviceInstName, Hashtable modules) throws RemoteException, ServiceException {

    }

    @Override
    public void changeRouteSelector(String appGUID, float appVersion, String routeGUID, HashMap selectors) throws RemoteException, ServiceException {

    }

    @Override
    public void changeRouteSelectorConfiguration(String appGUID, float appVersion, String routeGUID, String configurationName) throws RemoteException, ServiceException {

    }

    @Override
    public void enableSBW(String servInstName, String appGUID, float appVersion, String portName, boolean isEndState, int trackingType) throws RemoteException, ServiceException {

    }

    @Override
    public void disableSBW(String servInstName, String appGUID, float appVersion, String portName) throws RemoteException, ServiceException {

    }

    @Override
    public void setTrackedDataType(String servInstName, String appGUID, float appVersion, String portName, int trackingType) throws RemoteException, ServiceException {

    }

    @Override
    public void changeSBWConfiguration(String servInstName, String appGUID, float appVersion, String portName, String configurationName) throws RemoteException, ServiceException {

    }

    @Override
    public String getWADLURL(String appGUID, float appVersion, String servInstName) throws RemoteException, ServiceException {
        return null;
    }

    @Override
    public Set<String> getReferringRunningApplications(String appGUID, float appVersion, String servInstName) throws RemoteException, ServiceException {
        return null;
    }

    @Override
    public List<String> getAllReferringApplications(String appGUID, float appVersion, String serviceInstName) throws RemoteException, ServiceException {
        return null;
    }

    @Override
    public boolean isApplicationReferred(String appGUID, float appVersion) throws RemoteException, ServiceException {
        return false;
    }

    @Override
    public HashMap getRunningCompUsingNamedConfigs(HashMap<Integer, HashMap<String, String>> configsToChange) throws RemoteException, ServiceException {
        return null;
    }

    @Override
    public String changeNamedConfigurations(HashMap<Integer, HashMap<String, String>> configsToChange) throws RemoteException, ServiceException {
        return null;
    }

    @Override
    public String synchronizeAllRunningEP() throws RemoteException, ServiceException {
        return null;
    }

    @Override
    public boolean isFESLevelRouteDurable() {
        return false;
    }

    @Override
    public boolean isAppLevelRouteDurable(String appGUID, float appVersion) throws RemoteException, ServiceException {
        return false;
    }

    @Override
    public boolean isRouteDurable(String appGUID, float appVersion, String routeID) throws RemoteException, ServiceException {
        return false;
    }

    @Override
    public boolean isDeleteDestinationSetAtApp(String appGUID, float appVersion) throws RemoteException, ServiceException {
        return false;
    }
}
