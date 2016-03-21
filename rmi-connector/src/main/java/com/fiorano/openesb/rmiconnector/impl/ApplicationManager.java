package com.fiorano.openesb.rmiconnector.impl;

import com.fiorano.openesb.application.ApplicationRepository;
import com.fiorano.openesb.application.DmiObject;
import com.fiorano.openesb.application.application.*;
import com.fiorano.openesb.applicationcontroller.ApplicationController;
import com.fiorano.openesb.rmiconnector.api.*;
import com.fiorano.openesb.utils.Constants;
import com.fiorano.openesb.utils.FileUtil;
import com.fiorano.openesb.utils.LookUpUtil;
import com.fiorano.openesb.utils.ZipUtil;
import com.fiorano.openesb.utils.exception.FioranoException;

import javax.xml.stream.XMLStreamException;
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

    private InstanceHandler handler;

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
        this.handler = instanceHandler;
        setHandleID(instanceHandler.getHandleID());
    }

    @Override
    public String[] getApplicationIds() throws RemoteException, ServiceException {
        return applicationRepository.getApplicationIds();
    }

    @Override
    public boolean exists(String id, float version) throws RemoteException, ServiceException {
        boolean exists = false;
        try {
           applicationRepository.applicationExists(id, version);
        } catch (FioranoException e) {
            throw new ServiceException( e.getMessage());
        }
        return exists;
    }

    @Override
    public float[] getVersions(String id) throws RemoteException, ServiceException {
        try {
            return applicationRepository.getAppVersions(id);
        } catch (FioranoException e) {
            e.printStackTrace();
            throw new ServiceException();
        }
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

    public void changePortAppContext(String appGUID, float appVersion, String serviceName, String portName, String transformerType, byte[] appContextBytes,
                                     boolean completed, String scriptFileName, String jmsScriptFileName, String projectDirName) throws ServiceException, RemoteException {
        if (!isRunning(appGUID, appVersion)) {
            throw new ServiceException("EVENT_PROCESS_NOT_IN_RUNNING_STATE");
        }
        String key;
        File tempZipFile;
        File appContextFileTempFolder = null;
        key = handleId + "__CHANGE_PORT_APPCONTEXT";
        tempZipFile = null;
        FileOutputStream outstream = null;
        boolean successfulzip = true;
        //get the route transformation zip as byte array from server. keep writing to a zip file until client notifies completed
        try {
            tempZipFile = tempFileNameMap.get(key);
            if (tempZipFile == null) {
                tempZipFile = getTempFile("PortAppContext", "zip");
                tempFileNameMap.put(key, tempZipFile);
            }
            outstream = new FileOutputStream(tempZipFile, true);
            outstream.write(appContextBytes);
        } catch (IOException ioe) {
            successfulzip = false;
            throw new ServiceException("UNABLE_TO_CREATE_APPCONTEXT_ZIPFILE");
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

        boolean successfulextract = true;
        try {
            //File objects
            appContextFileTempFolder = getTempFile("PortAppContext", "tmp");
            File scriptFile = null;
            File jmsScriptFile = null;
            File projDirFile = null;
            //extract contents.
            appContextFileTempFolder.mkdir();
            ZipUtil.unzip(tempZipFile, appContextFileTempFolder);

            if (projectDirName != null)
                projDirFile = new File(appContextFileTempFolder.getCanonicalPath() + File.separator + projectDirName);
            if (scriptFileName != null)
                scriptFile = new File(appContextFileTempFolder.getCanonicalPath() + File.separator + scriptFileName);
            if (jmsScriptFileName != null)
                jmsScriptFile = new File(appContextFileTempFolder.getCanonicalPath() + File.separator + jmsScriptFileName);

            //checks for existence of file resources.
            if (projectDirName != null && !projDirFile.exists())
                throw new ServiceException("ERROR_CHANGE_APPCONTEXT_TRANSFORMATION2");
            if ((scriptFile != null && !scriptFile.exists()) || (jmsScriptFile != null && !jmsScriptFile.exists()))
                throw new ServiceException("ERROR_CHANGE_APPCONTEXT_TRANSFORMATION2");

            //read the temp folder for the project content
            String projectContent = null;
            String scriptContent = null;
            String jmsScriptContent = null;

            if (projDirFile != null)
                projectContent = ApplicationParser.toXML(projDirFile);

            FileInputStream fis = null;
            if (scriptFile != null) {
                try {
                    fis = new FileInputStream(scriptFile);
                    scriptContent = DmiObject.getContents(fis);
                } finally {
                    try {
                        if (fis != null)
                            fis.close();
                    } catch (IOException ignore) {
                    }
                }
            }

            if (jmsScriptFile != null) {
                try {
                    fis = new FileInputStream(jmsScriptFile);
                    jmsScriptContent = DmiObject.getContents(fis);
                } finally {
                    try {
                        if (fis != null)
                            fis.close();
                    } catch (IOException ignore) {
                    }
                }
            }

            Application application = null;
            try {
                application = applicationController.getApplicationHandle(appGUID, appVersion, handleId).getApplication();
            } catch (Exception e) {
                throw new ServiceException("ERROR_APP_HANDLE");
            }
            for (ServiceInstance serviceInstance : application.getServiceInstances()) {
                if (!serviceInstance.getName().equals(serviceName))
                    continue;
                Transformation transformation = serviceInstance.getOutputPortInstance(portName).getApplicationContextTransformation();
                if (transformation == null && (scriptFileName != null || projectDirName != null)) {
                    if (jmsScriptFileName!=null) {
                        transformation = new MessageTransformation();
                        ((MessageTransformation)transformation).setJMSScriptFile(jmsScriptFileName);
                    } else {
                        transformation = new Transformation();
                    }
                    transformation.setScriptFile(scriptFileName);
                    transformation.setProjectFile(projectDirName);

                } else if (transformation != null) {
                    if (transformation instanceof MessageTransformation) {
                        ((MessageTransformation) transformation).setJMSScriptFile(jmsScriptFileName);
                    }
                    transformation.setScriptFile(scriptFileName);
                    transformation.setProjectFile(projectDirName);
                }
            }

            try {
                applicationController.changePortAppContext(appGUID, appVersion, serviceName, portName, scriptContent, jmsScriptContent, transformerType, projectContent, handleId);
            } catch (FioranoException e) {
                //rmiLogger.error(Bundle.class, Bundle.ERROR_CHANGE_APPCONTEXT_TRANSFORMATION3, portName, serviceName, appGUID, appVersion, e);
                throw new ServiceException(e.getMessage());
            } finally {
                if (key != null)
                    tempFileNameMap.remove(key);
                if (tempZipFile != null)
                    tempZipFile.delete();
                if (appContextFileTempFolder != null)
                    FileUtil.deleteDir(appContextFileTempFolder);
            }

        } catch (IOException e) {
            successfulextract = false;
           // rmiLogger.error(Bundle.class, Bundle.ERROR_EXTRACTING_ZIPFILE_UNABLE_TO_SAVE_APPCONTEXT_TRANSFORMATION, portName, serviceName, appGUID, appVersion, e);
            throw new ServiceException("ERROR_EXTRACTING_ZIPFILE_UNABLE_TO_SAVE_APPCONTEXT_TRANSFORMATION");
        } catch (XMLStreamException e) {
            successfulextract = false;
           // rmiLogger.error(Bundle.class, Bundle.ERROR_EXTRACTING_ZIPFILE_UNABLE_TO_SAVE_APPCONTEXT_TRANSFORMATION, portName, serviceName, appGUID, appVersion, e);
            throw new ServiceException("ERROR_EXTRACTING_ZIPFILE_UNABLE_TO_SAVE_APPCONTEXT_TRANSFORMATION");
        }
        finally {
            //Removing the temporary zip entry in hashmap. and deleteing the file
            if (!successfulextract && appContextFileTempFolder != null) {
                FileUtil.deleteDir(appContextFileTempFolder);
                tempFileNameMap.remove(key);
                tempZipFile.delete();
            }
        }
    }

    public void changePortAppContextConfiguration(String appGUID, float appVersion, String serviceName, String portName, String configurationName) throws RemoteException, ServiceException {
        try {
            applicationController.changePortAppContextConfiguration(appGUID, appVersion, serviceName, portName, configurationName, handleId);
        } catch (FioranoException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public void clearPortAppContext(String appGUID, float appVersion, String serviceName, String portName) throws RemoteException, ServiceException {
        try {
            applicationController.changePortAppContext(appGUID, appVersion, serviceName, portName, null, null, null, null, handleId);
        } catch (FioranoException e) {
           // rmiLogger.error(Bundle.class, Bundle.ERROR_CLEAR_APPCONTEXT_TRANS, portName, serviceName, appGUID, appVersion, "", e);
            throw new ServiceException("ERROR_CLEAR_APPCONTEXT_TRANS");
        }
    }

    public void changeRouteTransformation(String appGUID, float appVersion, String routeGUID,String transformerType,byte[] transformationProjectBytes, boolean completed,String scriptFileName,String jmsScriptFileName,String projectDirName)
            throws ServiceException, RemoteException {

        if (!isRunning(appGUID, appVersion)) {
            throw new ServiceException("EVENT_PROCESS_NOT_IN_RUNNING_STATE");
        }
        String key;
        File tempZipFile;
        File routeFileTempFolder = null;
        key = handleId + "__CHANGE_ROUTE_TRANSFORMATION";
        tempZipFile = null;
        FileOutputStream outstream = null;
        boolean successfulzip = true;
        //get the route transformation zip as byte array from server. keep writing to a zip file until client notifies completed
        try {
            tempZipFile = tempFileNameMap.get(key);
            if (tempZipFile == null) {
                tempZipFile = getTempFile("RouteTransformation", "zip");
                tempFileNameMap.put(key, tempZipFile);
            }
            outstream = new FileOutputStream(tempZipFile, true);
            outstream.write(transformationProjectBytes);
        } catch (IOException ioe) {
            successfulzip = false;
           // rmiLogger.error(Bundle.class, Bundle.UNABLE_TO_CREATE_TRANS_ZIPFILE, routeGUID, appGUID, appVersion, ioe);
            throw new ServiceException("UNABLE_TO_CREATE_TRANS_ZIPFILE");
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

        boolean successfulextract = true;
        try {

            //File objects
            routeFileTempFolder = getTempFile("RouteTransformation", "tmp");
            File scriptFile = null;
            File jmsScriptFile = null;
            File projDirFile = null;
            //extract contents.
            routeFileTempFolder.mkdir();
            ZipUtil.unzip(tempZipFile, routeFileTempFolder);

            if (projectDirName != null)
                projDirFile = new File(routeFileTempFolder.getCanonicalPath() + File.separator + projectDirName);
            if (scriptFileName != null)
                scriptFile = new File(routeFileTempFolder.getCanonicalPath() + File.separator + scriptFileName);
            if (jmsScriptFileName != null)
                jmsScriptFile = new File(routeFileTempFolder.getCanonicalPath() + File.separator + jmsScriptFileName);

            //checks for existence of file resources.
            if (projectDirName != null && !projDirFile.exists())
                throw new ServiceException("ERROR_CHANGE_ROUTE_TRANS3");
            if ((scriptFile != null && !scriptFile.exists()) || (jmsScriptFile != null && !jmsScriptFile.exists()))
                throw new ServiceException("ERROR_CHANGE_ROUTE_TRANS3");

            //read the temp folder for the project content
            String projectContent = null;
            String scriptContent = null;
            String jmsScriptContent = null;

            List<Route> routes = null;
            try {
                //expecting app handle to not be null. check for 'is application running' is at method begining.
                routes = applicationController.getApplicationHandle(appGUID, appVersion, handleId).getApplication().getRoutes();
            } catch (Exception willNeverBeThrown) {

            }

            if (projDirFile != null)
                projectContent = ApplicationParser.toXML(projDirFile);

            FileInputStream fis = null;
            if (scriptFile != null) {
                try {
                    fis = new FileInputStream(scriptFile);
                    scriptContent = DmiObject.getContents(fis);
                } finally {
                    try {
                        if (fis != null)
                            fis.close();
                    } catch (IOException ignore) {
                        ;
                    }
                }

            }

            if (jmsScriptFile != null) {
                try {
                    fis = new FileInputStream(jmsScriptFile);
                    jmsScriptContent = DmiObject.getContents(fis);
                } finally {
                    try {
                        if (fis != null)
                            fis.close();
                    } catch (IOException ignore) {
                        ;
                    }
                }
            }

            for (Route route : routes) {
                if (route.getName().equals(routeGUID)) {
                    MessageTransformation transformation = route.getMessageTransformation();
                    if (transformation == null && (jmsScriptFileName != null || scriptFileName != null || projectDirName != null)) {
                        transformation = new MessageTransformation();
                        transformation.setJMSScriptFile(jmsScriptFileName);
                        transformation.setScriptFile(scriptFileName);
                        transformation.setProjectFile(projectDirName);
                        route.setMessageTransformation(transformation);
                        break;
                    }else if(transformation!=null){
                        transformation.setJMSScriptFile(jmsScriptFileName);
                        transformation.setScriptFile(scriptFileName);
                        transformation.setProjectFile(projectDirName);
                        break;
                    }
                }
            }

            try {
                applicationController.changeRouteTransformation(appGUID, appVersion, routeGUID, scriptContent, jmsScriptContent, transformerType, projectContent, handleId);
            } catch (FioranoException e) {
              //  rmiLogger.error(Bundle.class, Bundle.ERROR_CHANGE_ROUTE_TRANS, routeGUID, appGUID, appVersion, "", e);
                throw new ServiceException( e.getMessage());
            }finally {
                if(key!=null)
                    tempFileNameMap.remove(key);
                if(tempZipFile!=null)
                    tempZipFile.delete();
                if(routeFileTempFolder!=null)
                    FileUtil.deleteDir(routeFileTempFolder);
            }

        } catch (IOException e) {
            successfulextract = false;
          //  rmiLogger.error(Bundle.class, Bundle.ERROR_EXTRACTING_ZIPFILE_UNABLE_TO_SAVE_TRANSFORMATION, routeGUID, appGUID, appVersion, e);
            throw new ServiceException("ERROR_EXTRACTING_ZIPFILE_UNABLE_TO_SAVE_TRANSFORMATION");
        } catch (XMLStreamException e) {
            successfulextract = false;
            //rmiLogger.error(Bundle.class, Bundle.ERROR_EXTRACTING_ZIPFILE_UNABLE_TO_SAVE_TRANSFORMATION, routeGUID, appGUID, appVersion, e);
            throw new ServiceException("ERROR_EXTRACTING_ZIPFILE_UNABLE_TO_SAVE_TRANSFORMATION");
        }
        finally {
            //Removing the temporary zip entry in hashmap. and deleteing the file
            if (!successfulextract && routeFileTempFolder != null) {
                FileUtil.deleteDir(routeFileTempFolder);
                tempFileNameMap.remove(key);
                tempZipFile.delete();
            }
        }
    }

    public void changeRouteTransformationConfiguration(String appGUID, float appVersion, String routeGUID, String configurationName) throws RemoteException, ServiceException {
        try {
            applicationController.changeRouteTransformationConfiguration(appGUID, appVersion, routeGUID, configurationName, handleId);
        } catch (FioranoException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public void clearRouteTransformation(String appGUID, float appVersion, String routeGUID) throws ServiceException {
        try {
            applicationController.changeRouteTransformation(appGUID, appVersion, routeGUID, null, null, null, null, handleId);
        } catch (FioranoException e) {
          //  rmiLogger.error(Bundle.class,Bundle.ERROR_CLEAR_ROUTE_TRANS,routeGUID,appGUID,appVersion,"",e);
            throw new ServiceException("ERROR_CLEAR_ROUTE_TRANS");
        }

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

    public void deleteApplication(String appGUID, String version) throws RemoteException, ServiceException {
        try {
            applicationController.deleteApplication(appGUID, version, handleId);
        } catch (FioranoException e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public boolean dependenciesExists(ServiceReference[] serviceRefs, ApplicationMetadata[] applicationRefs) throws RemoteException, ServiceException {
        return false;
    }

    @Override
    public void startApplication(String appGUID, String version) throws RemoteException, ServiceException {
        try {
            applicationController.launchApplication(appGUID, version, handleId);
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(),e);
        }
    }

    @Override
    public void restartApplication(String appGUID, float appVersion) throws RemoteException, ServiceException {

    }

    @Override
    public void stopApplication(String appGUID, String version) throws RemoteException, ServiceException {
        try {
            applicationController.stopApplication(appGUID,version, handleId);
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
    public ApplicationMetadata[] getRunningApplications() throws RemoteException, ServiceException {
        List<ApplicationMetadata> runningEventProcesses = new ArrayList<ApplicationMetadata>();
        try {
            @SuppressWarnings("unchecked")
            Enumeration<ApplicationReference> runningApplications = applicationController.getHeadersOfRunningApplications(handleId);
            while (runningApplications.hasMoreElements()) {
                ApplicationReference appreference = runningApplications.nextElement();
                ApplicationMetadata epRefernce = new ApplicationMetadata(appreference.getGUID(), appreference.getVersion());
                epRefernce.setCategories(appreference.getCategories());
                epRefernce.setDisplayName(appreference.getDisplayName());
                epRefernce.setSchemaVersion(appreference.getSchemaVersion());
                epRefernce.setShortDescription(appreference.getShortDescription());
                epRefernce.setLongDescription(appreference.getLongDescription());
                runningEventProcesses.add(epRefernce);
            }
        } catch (FioranoException e) {
           // rmiLogger.error(Bundle.class, Bundle.ERROR_GET_RUNNING_EVENTPROCESSES, e);
            throw new ServiceException("ERROR_GET_RUNNING_EVENTPROCESSES");
        }
        return runningEventProcesses.toArray(new ApplicationMetadata[runningEventProcesses.size()]);
    }

    private List<PortInstanceMetaData> getPortsForService(Application application, String serviceInstName) throws ServiceException {
        List<PortInstanceMetaData> portInstMetaData = new ArrayList<PortInstanceMetaData>();
        if (application != null) {
            ServiceInstance serInst = application.getServiceInstance(serviceInstName);
            if (serInst != null) {
                List<PortInstance> portInst = new ArrayList<PortInstance>();
                portInst.addAll(serInst.getInputPortInstances());
                portInst.addAll(serInst.getOutputPortInstances());

                for (PortInstance portInstance : portInst) {
                    String destination = (LookUpUtil.getServiceInstanceLookupName(application.getGUID(), application.getVersion(), serInst.getName()) + "__" + portInstance.getName()).toUpperCase();
                    if (portInstance.getDestinationType() == PortInstance.DESTINATION_TYPE_QUEUE) {
                        if (portInstance.isSpecifiedDestinationUsed())
                            portInstMetaData.add(new PortInstanceMetaData(serInst.getName(), portInstance.getName(), portInstance.getDestination(), PortInstanceMetaData.DestinationType.QUEUE, portInstance.isDestinationEncrypted(), portInstance.getEncryptionKey(), portInstance.getEncryptionAlgorithm(), portInstance.isAllowPaddingToKey(), portInstance.getInitializationVector()));
                        else
                            portInstMetaData.add(new PortInstanceMetaData(serInst.getName(), portInstance.getName(), destination, PortInstanceMetaData.DestinationType.QUEUE, portInstance.isDestinationEncrypted(), portInstance.getEncryptionKey(), portInstance.getEncryptionAlgorithm(), portInstance.isAllowPaddingToKey(), portInstance.getInitializationVector()));
                    } else {
                        if (portInstance.isSpecifiedDestinationUsed())
                            portInstMetaData.add(new PortInstanceMetaData(serInst.getName(), portInstance.getName(), portInstance.getDestination(), PortInstanceMetaData.DestinationType.TOPIC, portInstance.isDestinationEncrypted(), portInstance.getEncryptionKey(), portInstance.getEncryptionAlgorithm(), portInstance.isAllowPaddingToKey(), portInstance.getInitializationVector()));
                        else
                            portInstMetaData.add(new PortInstanceMetaData(serInst.getName(), portInstance.getName(), destination, PortInstanceMetaData.DestinationType.TOPIC, portInstance.isDestinationEncrypted(), portInstance.getEncryptionKey(), portInstance.getEncryptionAlgorithm(), portInstance.isAllowPaddingToKey(), portInstance.getInitializationVector()));
                    }
                }
            } else {
                RemoteServiceInstance remoteSerInst = application.getRemoteServiceInstance(serviceInstName);
                if (remoteSerInst != null) {
                    String remoteAppGUID = remoteSerInst.getApplicationGUID();
                    String remoteInstanceName = remoteSerInst.getRemoteName();
                    float remoteAppVersion = remoteSerInst.getApplicationVersion();
                    try {
                            application = applicationRepository.readApplication(remoteAppGUID, String.valueOf(remoteAppVersion));
                            serInst = application.getServiceInstance(remoteInstanceName);
                            if (serInst != null) {
                                List<PortInstance> portInst = new ArrayList<PortInstance>();
                                portInst.addAll(serInst.getInputPortInstances());
                                portInst.addAll(serInst.getOutputPortInstances());

                                for (PortInstance portInstance : portInst) {
                                    String destination = (LookUpUtil.getServiceInstanceLookupName(application.getGUID(), application.getVersion(), serInst.getName()) + "__" + portInstance.getName()).toUpperCase();
                                    if (portInstance.getDestinationType() == PortInstance.DESTINATION_TYPE_QUEUE) {
                                        if (portInstance.isSpecifiedDestinationUsed())
                                            portInstMetaData.add(new PortInstanceMetaData(serInst.getName(), portInstance.getName(), portInstance.getDestination(), PortInstanceMetaData.DestinationType.QUEUE, portInstance.isDestinationEncrypted(), portInstance.getEncryptionKey(), portInstance.getEncryptionAlgorithm(), portInstance.isAllowPaddingToKey(), portInstance.getInitializationVector()));
                                        else
                                            portInstMetaData.add(new PortInstanceMetaData(serInst.getName(), portInstance.getName(), destination, PortInstanceMetaData.DestinationType.QUEUE, portInstance.isDestinationEncrypted(), portInstance.getEncryptionKey(), portInstance.getEncryptionAlgorithm(), portInstance.isAllowPaddingToKey(), portInstance.getInitializationVector()));
                                    } else {
                                        if (portInstance.isSpecifiedDestinationUsed())
                                            portInstMetaData.add(new PortInstanceMetaData(serInst.getName(), portInstance.getName(), portInstance.getDestination(), PortInstanceMetaData.DestinationType.TOPIC, portInstance.isDestinationEncrypted(), portInstance.getEncryptionKey(), portInstance.getEncryptionAlgorithm(), portInstance.isAllowPaddingToKey(), portInstance.getInitializationVector()));
                                        else
                                            portInstMetaData.add(new PortInstanceMetaData(serInst.getName(), portInstance.getName(), destination, PortInstanceMetaData.DestinationType.TOPIC, portInstance.isDestinationEncrypted(), portInstance.getEncryptionKey(), portInstance.getEncryptionAlgorithm(), portInstance.isAllowPaddingToKey(), portInstance.getInitializationVector()));
                                    }
                                }
                            }

                        }
                    catch (Exception e) {
                       // rmiLogger.error(Bundle.class, Bundle.UNABLE_TO_GET_PORTS_FOR_SERVICE_INSTANCE, serviceInstName, remoteAppGUID, remoteAppVersion, e.getMessage(), e);
                        throw new ServiceException(e.getMessage());
                    }
                }
            }
        }
        return portInstMetaData;
    }

    @Override
    public List<RouteMetaData> getRoutesOfApplications(String appGUID, float version) throws RemoteException, ServiceException {
        List<RouteMetaData> routes = new ArrayList<RouteMetaData>();
        try {
                ApplicationController applicationControllerManager = (applicationController);
                Application application = applicationRepository.readApplication(appGUID, String.valueOf(version));
                List<Route> dmiRoutes = application.getRoutes();

                for (Route dmiRoute : dmiRoutes) {
                    PortInstanceMetaData sourcePortMetaData = null;
                    PortInstanceMetaData targetPortMetaData = null;

                    List<PortInstanceMetaData> sourcePorts = getPortsForService(application, dmiRoute.getSourceServiceInstance());
                    List<PortInstanceMetaData> targetPorts = getPortsForService(application, dmiRoute.getTargetServiceInstance());

                    for (PortInstanceMetaData portInstanceMetaData : sourcePorts) {
                        if (portInstanceMetaData.getDisplayName().equals(dmiRoute.getSourcePortInstance())) {
                            sourcePortMetaData = portInstanceMetaData;
                            break;
                        }
                    }

                    for (PortInstanceMetaData portInstanceMetaData : targetPorts) {
                        if (portInstanceMetaData.getDisplayName().equals(dmiRoute.getTargetPortInstance())) {
                            targetPortMetaData = portInstanceMetaData;
                            break;
                        }
                    }
                    routes.add(new RouteMetaData(dmiRoute.getName(), dmiRoute.getSourceServiceInstance(), dmiRoute.getTargetServiceInstance(), sourcePortMetaData, targetPortMetaData));
                }
        } catch (Exception e) {
            //rmiLogger.error(Bundle.class, Bundle.UNABLE_TO_GET_ROUTES_FOR_APPLICATION, appGUID, version, e.getMessage(), e);
            throw new ServiceException(e.getMessage());
        }
        return routes;
    }

    @Override
    public List<PortInstanceMetaData> getPortsForApplications(String appGUID, float version) throws RemoteException, ServiceException {

        List<PortInstanceMetaData> portInstMetaData = new ArrayList<PortInstanceMetaData>();
        try {
                ApplicationController applicationControllerManager = (applicationController);
                Application application = applicationRepository.readApplication(appGUID, String.valueOf(version));
                if (application != null) {
                    for (ServiceInstance serInst : application.getServiceInstances()) {
                        if (serInst != null) {
                            portInstMetaData.addAll(getPortsForService(application, serInst.getName()));
                        }
                    }
                    for (Object o : application.getRemoteServiceInstances()) {
                        RemoteServiceInstance remoteserInst = (RemoteServiceInstance) o;
                        if (remoteserInst != null) {
                            portInstMetaData.addAll(getPortsForService(application, remoteserInst.getName()));
                        }
                    }
                }
        } catch (Exception e) {
           // rmiLogger.error(Bundle.class, Bundle.UNABLE_TO_GET_PORTS_FOR_APPLICATION, appGUID, version, e);
            throw new ServiceException(e.getMessage());
        }
        return portInstMetaData;
    }

    public List<PortInstanceMetaData> getPortsForService(String appGUID, float version, String serviceInstName) throws RemoteException, ServiceException {
        List<PortInstanceMetaData> portInstMetaData = new ArrayList<PortInstanceMetaData>();
        try {
                ApplicationController applicationControllerManager = (applicationController);
                Application application = applicationRepository.readApplication(appGUID, String.valueOf(version));
                ServiceInstance serviceInst = application.getServiceInstance(serviceInstName);
                if (serviceInst != null) {
                    portInstMetaData = getPortsForService(application, serviceInstName);
                } else {
                    RemoteServiceInstance remoteserInst = application.getRemoteServiceInstance(serviceInstName);
                    if (remoteserInst != null)
                        portInstMetaData = getPortsForService(application, remoteserInst.getName());
                }
        } catch (Exception e) {
           // rmiLogger.error(Bundle.class, Bundle.UNABLE_TO_GET_PORTS_FOR_SERVICE_INSTANCE, serviceInstName, appGUID, version, e.getMessage(), e);
            throw new ServiceException(e.getMessage());
        }
        return portInstMetaData;
    }


    @Override
    public List<ServiceInstanceMetaData> getServiceInstancesOfApp(String appGUID, float version) throws RemoteException, ServiceException {
        List<ServiceInstanceMetaData> serviceInstances = new ArrayList<ServiceInstanceMetaData>();
        try {
                ApplicationController applicationControllerManager =  (applicationController);
                Application application = applicationRepository.readApplication(appGUID, String.valueOf(version));
                if (application != null) {
                    List<ServiceInstance> services = application.getServiceInstances();
                    for (ServiceInstance serviceInstance : services) {
                        serviceInstances.add(new ServiceInstanceMetaData(serviceInstance.getName(), serviceInstance.getGUID(), serviceInstance.getVersion(), serviceInstance.getShortDescription(), serviceInstance.getLongDescription(), serviceInstance.getNodes(), serviceInstance.getLaunchType()));
                    }

                    @SuppressWarnings("unchecked")
                    List<ServiceInstance> remoteServices = application.getRemoteServiceInstances();
                    if (remoteServices != null) {
                        for (Object o : application.getRemoteServiceInstances()) {
                            RemoteServiceInstance remoteserInst = (RemoteServiceInstance) o;
                            if (remoteserInst != null) {
                                String remoteAppGUID = remoteserInst.getApplicationGUID();
                                String remoteInstanceName = remoteserInst.getRemoteName();
                                application = applicationRepository.readApplication(remoteAppGUID, String.valueOf(remoteserInst.getApplicationVersion()));
                                if(application == null) {
                                    throw new FioranoException("ERROR_APP_DOESNT_EXIST");
                                }
                                ServiceInstance actualservice = application.getServiceInstance(remoteInstanceName);
                                serviceInstances.add(new RemoteServiceInstanceMetaData(appGUID, remoteAppGUID, remoteInstanceName, actualservice.getName(), actualservice.getGUID(), actualservice.getVersion(), actualservice.getShortDescription(), actualservice.getLongDescription(), actualservice.getNodes(), actualservice.getLaunchType()));
                            }
                        }
                    }
                }
        } catch (FioranoException e) {
            //rmiLogger.error(Bundle.class, Bundle.UNABLE_TO_GET_SERVICE_INSTANCES_FOR_APPLICATION, appGUID, version, e.getMessage(), e);
            throw new ServiceException( e.getMessage());
        }
        return serviceInstances;
    }

    @Override
    public void addApplicationListener(IApplicationManagerListener listener, String appGUID, float appVersion) throws RemoteException, ServiceException {
        dapiEventManager.registerApplicationEventListener(listener,appGUID, appVersion, handleId);
    }

    @Override
    public void removeApplicationListener(IApplicationManagerListener listener, String appGUID, float appVersion) throws RemoteException, ServiceException {
        dapiEventManager.unRegisterApplicationEventListener(listener, appGUID, appVersion, handleId);
    }

    @Override
    public void addRepositoryEventListener(IRepoEventListener listener) throws RemoteException, ServiceException {
        dapiEventManager.registerMicroServiceRepoEventListener(listener, handleId);
    }

    @Override
    public void removeRepositoryEventListener() throws RemoteException, ServiceException {
        dapiEventManager.unRegisterMicroServiceRepoEventListener(handleId);
    }

    @Override
    public boolean isRunning(String appGUID, float appVersion) throws RemoteException, ServiceException {
        boolean running = false;
        ApplicationMetadata[] runningEventProcesses = getRunningApplications();
        for (ApplicationMetadata eventProcessReference : runningEventProcesses) {
            if (eventProcessReference.getId().equals(appGUID) && eventProcessReference.getVersion() == appVersion) {
                running = true;
                break;
            }
        }
        return running;
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
    public ApplicationMetadata[] getAllApplications() throws RemoteException, ServiceException {
        List<ApplicationMetadata> savedEventProcesses = new ArrayList<ApplicationMetadata>();
        try {
            @SuppressWarnings("unchecked")
            Enumeration<ApplicationReference> savedApplications = applicationController.getHeadersOfSavedApplications(handleId);
            while (savedApplications.hasMoreElements()) {
                ApplicationReference appReference = savedApplications.nextElement();
                ApplicationMetadata epReference = new ApplicationMetadata(appReference.getGUID(), appReference.getVersion());
                epReference.setCategories(appReference.getCategories());
                epReference.setDisplayName(appReference.getDisplayName());
                epReference.setSchemaVersion(appReference.getSchemaVersion());
                epReference.setShortDescription(appReference.getShortDescription());
                epReference.setLongDescription(appReference.getLongDescription());
                epReference.setTypeName(appReference.getTypeName());
                epReference.setSubType(appReference.getSubType());
                savedEventProcesses.add(epReference);
            }

        } catch (FioranoException e) {
            e.printStackTrace();
            throw new ServiceException( e.getMessage());
        }

        return savedEventProcesses.toArray(new ApplicationMetadata[savedEventProcesses.size()]);
    }

    @Override
    public ApplicationMetadata getApplication(String appGUID, float version) throws RemoteException, ServiceException {
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

    public void unreferenced() {
        handler.onUnReferenced(this.toString());
    }

    public String toString() {
        return Constants.APPLICATION_MANAGER;
    }
}
