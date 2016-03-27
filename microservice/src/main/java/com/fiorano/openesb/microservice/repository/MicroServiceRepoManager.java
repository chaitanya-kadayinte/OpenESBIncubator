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

import com.fiorano.openesb.application.DmiObject;
import com.fiorano.openesb.application.DmiResourceData;
import com.fiorano.openesb.application.ServerConfig;
import com.fiorano.openesb.application.service.*;
import com.fiorano.openesb.application.sps.ServiceSearchContext;
import com.fiorano.openesb.events.Event;
import com.fiorano.openesb.events.EventsManager;
import com.fiorano.openesb.events.MicroServiceRepoUpdateEvent;
import com.fiorano.openesb.microservice.launch.MicroServiceRepoEventRaiser;
import com.fiorano.openesb.utils.exception.FioranoException;

import java.io.*;
import java.util.*;

//todo move to management related modules
public class MicroServiceRepoManager {

    private static final MicroServiceRepoManager MICRO_SERVICE_REPOSITORY_MANAGER = new MicroServiceRepoManager();
    private MicroServiceRepoManager() {
        waitObject = new Object();
        // Create new hashtables for the properties
        m_committedServiceVsProperties = new Hashtable<String, Service>();
        m_nonCommittedServiceVsProperties = new Hashtable<String, Service>();

        COMPONENTS_REPOSITORY_FOLDER = getRepositoryLocation();
        try {
            _switchToActiveMode();
        } catch (FioranoException e) {
            e.printStackTrace();
        }
    }

    public static MicroServiceRepoManager getInstance() {
        return MICRO_SERVICE_REPOSITORY_MANAGER;
    }

    public String getRepositoryLocation() {
        return ServerConfig.getConfig().getRepositoryPath() + File.separator + "microservices";
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

    // Temporary directory for storing the uncommitted services
    public static final String TEMP_DOWNLOAD_DIR = "tmp";

    // Hashtable storing the ServiceGUID_Version versus the
    // ServicePropertySheet object for that particular service for committed services
    private Hashtable<String, Service> m_committedServiceVsProperties;

    // Hashtable storing the ServiceGUID_Version versus the
    // ServicePropertySheet object for that particular service for committed services
    private Hashtable<String, Service> m_nonCommittedServiceVsProperties;

    // Events manager to notify the Security Events which might be raised.
    private EventsManager m_eventsManager;

    private static final Map favorites = Collections.singletonMap("FIORANO_HOME", System.getProperty("karaf.base"));

    private static String COMPONENTS_REPOSITORY_FOLDER;// = System.getProperty(IMQConstants.FIORANO_HOME)+"/esb/fes/repository/components";
    private boolean componentrepoinsync = false;
    private Object waitObject;

    /*********************************************************************************************************/
    //@START State Controller Methods
    /*********************************************************************************************************/

    /**
     * Switched the module to active mode. Reloads the reposiroty.
     *
     *
     */
    protected void _switchToActiveMode()
            throws FioranoException
    {
        componentrepoinsync = true;
        //Reload the compoenents
        reloadRepository();
        componentrepoinsync = false;
        synchronized (waitObject) {
            waitObject.notifyAll();
        }
        // Create default ACLS for all services registered and unregistered.
        ArrayList registeredServices = new ArrayList();
        ArrayList unRegisteredServices = new ArrayList();
        Enumeration _enum = getAllServicesInRepository();
        while(_enum.hasMoreElements())
            registeredServices.add(_enum.nextElement());
        _enum = getAllUnCommittedServices();
        while(_enum.hasMoreElements())
            unRegisteredServices.add(_enum.nextElement());
    }

    /**
     * gets the module name
     * @return String
     */
    public String getModuleName()
    {
        return "MicroServiceRepository";
    }

    /*********************************************************************************************************/
    //@END State Controller Methods
    /*********************************************************************************************************/


    /*********************************************************************************************************/
    //@START getters and Setters
    /*********************************************************************************************************/


    /**
     * sets the Events Manager
     * @param manager
     */
    public void setEventsManager(EventsManager manager)
    {
        this.m_eventsManager = manager;
    }


    /*********************************************************************************************************/
    //@END getters and Setters
    /*********************************************************************************************************/


    /*********************************************************************************************************/
    //@START Public interface
    /*********************************************************************************************************/


    /**
     * This API is used to get the resource in parts. Chunks of numBytes
     * and if the last chunk is not of the size and less than it then
     * create a appropriate sized byte array and return it inside DMIResourceData.
     * Else the chunk ofsize numBytes is returned as DMIResourceData.
     *
     * @param serviceGUID Description of the Parameter
     * @param version Description of the Parameter
     * @param resName Description of the Parameter
     * @param index Description of the Parameter
     * @param numBytes Description of the Parameter
     * @return The dataForResource value
     * @exception FioranoException Description of the Exception
     */
    public DmiResourceData getDataForResource(String serviceGUID, String version,
                                              String resName, long index, int numBytes)
            throws FioranoException
    {
        try {
            // check for the existance of the service. If it does not exist, throw Exception
            getServiceInfo(serviceGUID, version);
            File resFile = null;
            File serviceDescriptorFile = null;
            BufferedInputStream bis;
            long fileLength;
            // fetch the resource file
            if(resName.equals(MicroServiceConstants.SERVICE_DESCRIPTOR_FILE_NAME)){
                Service serv = getServicePropertySheet(serviceGUID,version);
                serviceDescriptorFile = File.createTempFile("Fiorano.ServiceDescriptor","xml");
                FileOutputStream outstream = new FileOutputStream(serviceDescriptorFile);
                serv.toXMLString(outstream);
                outstream.close();
                bis = new BufferedInputStream(new FileInputStream(serviceDescriptorFile));
                fileLength = serviceDescriptorFile.length();
            }
            else{
                resFile = fetchResourceFile(serviceGUID, version, resName);
                bis = new BufferedInputStream(new FileInputStream(resFile));
                fileLength = resFile.length();
            }

            // Random access reading of the data
            int numBytesRead;
            byte[] bytes;
            DmiResourceData dmiResData;

            try {
                bytes = new byte[numBytes];
                bis.skip(index);
                numBytesRead = bis.read(bytes);
            }
            finally{
                bis.close();
                if(serviceDescriptorFile!=null)
                    serviceDescriptorFile.delete();
            }

            dmiResData = new DmiResourceData();
            if (numBytesRead != numBytes)
            {
                if (numBytesRead < 0)
                    numBytesRead = 0;

                byte[] newByteArray = new byte[numBytesRead];

                System.arraycopy(bytes, 0, newByteArray, 0, numBytesRead);
                dmiResData.setData(newByteArray);
            }
            else
                dmiResData.setData(bytes);

            dmiResData.setStartOffset(index);
            dmiResData.setEndOffset(index + numBytesRead);
            dmiResData.setResourceLength(fileLength);
            dmiResData.setResourceName(resName);
            return dmiResData;
        } catch (Exception e) {
//            logger.error(Bundle.class,code,resName,serviceGUID,version,e);        //logging in FESStubManager
            throw new FioranoException( e);
        }
    }


    /**
     *  Gets list of all the resources for a service.
     *
     * @param serviceGUID The GUID of the service for which resource
     *      info is to be retrieved
     * @param version The version of the service for which resource
     *      info is to be retrieved
     * @return An enumeration containing Resource objects
     *      for all the resources of the service
     * @exception FioranoException Description of the Exception
     */
    public Enumeration getAllResourcesForService(String serviceGUID, String version)
            throws FioranoException
    {
        try
        {
            Service sps = getServicePropertySheet(serviceGUID,version);

            Deployment deployment = sps.getDeployment();

            return Collections.enumeration(deployment.getResources());
        }catch(Exception ex)
        {
//            logger.error(Bundle.class,Bundle.ERROR_FETCH_RESOURCE_LIST,serviceGUID,version,ex);       //logging in FESStubManager
            throw new FioranoException(ex);
        }

    }

    /**
     * Returns a string containing resourceName and lastUpdateDate for all resources of a service
     * E.g. {ResourceName1#LastUpdateDate1*ResourceName2#LastUpdateDate2*...}
     *
     * @param serviceGUID
     * @param version
     * @return String
     * @throws 
     */
    public String getResourcesWithUpdatedDate(String serviceGUID, String version)
            throws FioranoException
    {
        try {
            StringBuffer resources = new StringBuffer();
            Service sps;
            try{
                sps = getServicePropertySheet(serviceGUID,version);
            }catch(IOException ioe)
            {
                throw new FioranoException(ioe);
            }

            if (sps == null || sps.getDeployment() == null)
                return null;
            Deployment deploymentInfo = sps.getDeployment();
            Iterator enumResources = deploymentInfo.getResources().iterator();
            boolean isFirst = true;
            while (enumResources.hasNext())
            {
                Resource resource = (Resource) enumResources.next();
                String resName = resource.getName();

                String resFileName = getResourcePath(serviceGUID,version,resName);
                File resFile = new File(resFileName);
                if (resFile.exists())
                {
                    if(!isFirst)
                    {
                        resources.append("*");
                    }

                    resources.append(resName);
                    resources.append("#");
                    resources.append(resFile.lastModified());
                    isFirst = false;
                }
            }

            return resources.toString();
        } catch (Exception e) {

            throw new FioranoException(e);
            //throw new FioranoException("error getting resource",e);
        }
    }


    /**
     *  Gets info about all the service that are present in the service
     *  repository
     *
     * @return Enumeration of ServicePropertySheet objects
     */
    public Enumeration getAllServicesInRepository()
    {
        synchronized (waitObject) {
            try {
                if (componentrepoinsync)
                    waitObject.wait();
            } catch (InterruptedException e) {
                //Ignore
            }
        }
        return m_committedServiceVsProperties.elements();
    }

    /**
     * Returns a enumeration of unique ID for a component (Strings).
     * Unique ID(Component c1) = c1.GUID+__+c1.version
     *
     * @return enumeration of unique ID for a component (Strings)
     */
//    public Set getAllServiceNamesInRepository()
//    {
//        return m_committedServiceVsProperties.keySet();
//    }

    /**
     *  Gets all the services in the repository which are not committed
     *
     * @return Enumeration of ServicePropertySheet objects
     */
    public Enumeration getAllUnCommittedServices()
    {
        synchronized (waitObject) {
            try {
                if (componentrepoinsync)
                    waitObject.wait();
            } catch (InterruptedException e) {
                //Ignore
            }
        }
        return m_nonCommittedServiceVsProperties.elements();
    }


    /**
     *  Gets all the versions that are available for a particular service.
     *
     * @param serviceGUID The GUID of the service for which available
     *      versions is to be get
     * @param nonCommittedAlso Description of the Parameter
     * @return An anumeration of Strings containning the
     *      available versions for the service
     */
    public Enumeration getAllVersionsOfService(String serviceGUID, boolean nonCommittedAlso)
    {
        Vector allVersionsOfServices = new Vector();
        Service sps;

        if (nonCommittedAlso)
        {
            Enumeration nonCommittedServices = m_nonCommittedServiceVsProperties.keys();

            while (nonCommittedServices.hasMoreElements())
            {
                String key = (String) nonCommittedServices.nextElement();
                if (!getGUIDfromUniqueKey(key).equalsIgnoreCase(serviceGUID))
                    continue;
                sps = (Service) m_nonCommittedServiceVsProperties.get(key);
                allVersionsOfServices.addElement(new ServiceReference(sps));
            }
        }

        Enumeration committedServices = m_committedServiceVsProperties.keys();

        while (committedServices.hasMoreElements())
        {
            String key = (String) committedServices.nextElement();
            if (!getGUIDfromUniqueKey(key).equalsIgnoreCase(serviceGUID))
                continue;
            sps = (Service) m_committedServiceVsProperties.get(key);
            allVersionsOfServices.addElement(new ServiceReference(sps));

        }
        // sort
        return sortAccordingToVersionNumber(allVersionsOfServices).elements();
    }

    /**
     *  Returns the highest available version for a service
     *
     * @param serviceGUID Description of the Parameter
     * @return The highestVersionOfService value
     */
    public String getHighestVersionOfService(String serviceGUID)
    {
        String highestVersion = null;
        Enumeration _enum = getAllVersionsOfService(serviceGUID, false);

        while (_enum.hasMoreElements())
        {
            ServiceReference hdr = (ServiceReference) _enum.nextElement();

            if (highestVersion == null)
            {
                highestVersion = hdr.getVersion() + "";
                continue;
            }
            if (highestVersion.compareTo(hdr.getVersion() + "") < 0)
                highestVersion = hdr.getVersion() + "";
        }
        return highestVersion;
    }


    /**
     *  Gets information about a service from the service repository
     *
     * @param serviceGUID The GUID of the service for which properties
     *      are to be retrieved
     * @param version The version of the service for which
     *      properties are to be retrived
     * @return ServicePropertySheet object containing info
     *      about the service
     * @exception FioranoException Description of the Exception
     */
    public Service getServiceInfo(String serviceGUID, String version)
            throws FioranoException
    {
        Service sps;

        if(serviceGUID==null)
            throw new FioranoException("service guid null");

        if (version == null)
            version = getHighestVersionOfService(serviceGUID);
        synchronized (waitObject) {
            try {
                if (componentrepoinsync)
                    waitObject.wait();
            } catch (InterruptedException e) {
                //Ignore
            }
        }
        sps = (Service)
                m_nonCommittedServiceVsProperties.get(getUniqueKey(serviceGUID, version));
        if (sps == null)
            sps = (Service)
                    m_committedServiceVsProperties.get(getUniqueKey(serviceGUID, version));

        if (sps == null)
            throw new FioranoException("sps null");
            //LogHelper.getErrMessage(ILogModule.SERVICE_REPOSITORY, 14, serviceGUID, version));

        else
            return sps;
    }

    /**
     *  Gets all the non launchable services from the service repository
     *
     * @return An enumeration containing ServicePropertSheet objects for all
     *      the non launchable services
     */
    public Enumeration getAllNonLaunchableServices()
    {
        Vector allNonLaunchableServices = new Vector();
        Enumeration _enum = m_committedServiceVsProperties.elements();

        while (_enum.hasMoreElements())
        {
            Service sps = (Service) _enum.nextElement();

            if (sps != null && sps.getExecution()==null)
                allNonLaunchableServices.addElement(sps);
        }
        return allNonLaunchableServices.elements();
    }


    /**
     *  Returns a byte array consisting the icon for a particular service
     *
     * @param serviceGUID The GUID of the service for which icon is to
     *      be returned
     * @param version The version of the service for which icon is
     *      to be returned
     * @return The serviceIcon value
     * @exception FioranoException Description of the Exception
     */
    public byte[] getServiceIcon(String serviceGUID, String version)
            throws FioranoException
    {
        try {
            if (version == null || version.trim().equals(""))
                version = getHighestVersionOfService(serviceGUID);

            Service sps = getServiceInfo(serviceGUID, version);

            String iconName = sps.getIcon16();
            File icon = getIcon(serviceGUID,version,iconName);
            byte[] b = ComponentRepositoryUtil.getBytesFromFile(icon);
            if(b==null)
                System.out.println("file empty");
            return b;
        } catch (Exception e) {
//            logger.error(Bundle.class,Bundle.ERROR_FETCH_SERVICE_ICON,serviceGUID,version,e);          //logging in FESStubManager
            throw new FioranoException(e);
        }
    }


    /**
     *  Returns a byte array consisting the icon for a particular service
     *
     * @param serviceGUID The GUID of the service for which icon is to
     *      be returned
     * @param version The version of the service for which icon is
     *      to be returned
     * @return The serviceIcon value
     * @exception FioranoException Description of the Exception
     */
    public byte[] getServiceIcon32(String serviceGUID, String version)
            throws FioranoException
    {
        try {
            if (version == null || version.trim().equals(""))
                version = getHighestVersionOfService(serviceGUID);

            Service sps = getServiceInfo(serviceGUID, version);

            String iconName = sps.getIcon32();

            // Assign the small icon in case the icon is not found.
            if(iconName == null || iconName.equalsIgnoreCase(""))
            {
                //if(TifTrace.ServiceRepository > TraceLevels.Info)
                //    LogHelper.logErr(ILogModule.SERVICE_REPOSITORY,18 ,serviceGUID, version);
                //logger.info(Bundle.class,Bundle.SERVICE_32_ICON_NOT_FOUND,serviceGUID,version);
                iconName = sps.getIcon16();
            }

            File icon = getIcon(serviceGUID,version,iconName);
            byte[] b = ComponentRepositoryUtil.getBytesFromFile(icon);
            if(b==null)
                System.out.println("file found empty");
            return b;

        } catch (Exception e) {
//            logger.error(Bundle.class,Bundle.ERROR_FETCH_SERVICE_ICON,serviceGUID,version,e);        //logging in FESStubManager
            throw new FioranoException(e);
            //throw new FioranoException("error getting service icon32",e);
        }
    }


    /**
     *  Returns the boolean indiacting whether a service has been published or
     *  not
     *
     * @param serviceGUID The GUID of the service for which ispublished
     *      is to be checked
     * @param version The version of the service for which
     *      ispublished is to be checked
     * @return the boolean value indicating publish status
     */
    public boolean isServicePublished(String serviceGUID, String version)
    {
        try
        {
            return (m_committedServiceVsProperties.get(getUniqueKey(serviceGUID, version)) != null);
        }
        catch (FioranoException te)
        {
            //logger.info(Bundle.class,Bundle.SERVICE_NOT_PUBLISHED,serviceGUID,version);
            //LogHelper.logErr(ILogModule.SERVICE_REPOSITORY, 19, serviceGUID, version);
            return false;
        }
    }



    /**
     *  Updates the resources for a service. The bytes are added on to the file
     *  for which they are meant. As soon as the final packet is added into the
     *  file, the file is moved from the "partial" folder to the complete
     *  folder. If any packet for this file is received even after this, it is
     *  not updated.
     *
     * @param serviceGUID Description of the Parameter
     * @param version Description of the Parameter
     * @param resName Description of the Parameter
     * @param startByte Description of the Parameter
     * @param bytes Description of the Parameter
     * @param isLastPacket Description of the Parameter
     * @param handleID Description of the Parameter
     * @exception FioranoException Description of the Exception
     */
    public void updateServiceResources(String serviceGUID, String version,
                                       String resName, long startByte,
                                       byte[] bytes, boolean isLastPacket, String handleID)
            throws FioranoException
    {
        try
        {
            if (isLastPacket)
            {
               // handleSecurityCheck(handleID, PermissionImpl.getPermissionName(PermissionImpl.CONFIGURE_SERVICE));
            }

            Service sps = getServiceInfo(serviceGUID, version);


            Iterator resources = sps.getDeployment().getResources().iterator();
            boolean fileAlreadyCompleted = false;
            String servicePath = getServicePath(serviceGUID, version);
            String compFolderName = getCompletedResourceFolder(serviceGUID,version);
            boolean validResource = false;

            while (resources.hasNext())
            {
                Resource res = (Resource) resources.next();
                String resourceName = res.getName();

                if (resourceName == null || !resourceName.equalsIgnoreCase(resName))
                    continue;

                // resource name specified is valid
                validResource = true;

                // check for the existance of the file in the folder
                // which contains completed files
                String fileName = compFolderName + File.separator + resourceName;
                File file = new File(fileName);

                if (file.exists())
                    fileAlreadyCompleted = true;
                break;
            }

            // raise exception in case of invalid resource
            if (!validResource)
                throw new FioranoException("invalid resource name");
            //LogHelper.getErrMessage(ILogModule.SERVICE_REPOSITORY, 31, resName, serviceGUID));

            // if the file for which data is to be written is already completely
            // written, we need not write the data that we have received

            // Added another condition that if the file is complete and
            // the service has been committed, then we need to move the file
            // from "tmp" folder to the service folder
            if (fileAlreadyCompleted && m_committedServiceVsProperties.containsKey(getUniqueKey(serviceGUID, version)))
            {
                //LogHelper.logErr(ILogModule.SERVICE_REPOSITORY,21 ,serviceGUID, version, resName, servicePath);
                //logger.debug(Bundle.class,Bundle.MOVE_TEMP_SERVICE,serviceGUID, version, resName, servicePath);
                moveCompletedResourceFilesToRepository(serviceGUID,version);
                new File(servicePath+ File.separator+ TEMP_DOWNLOAD_DIR).delete();
            }

            updatePartialResourceData(serviceGUID, version, resName, startByte, bytes);

            // if this was the last packet for this file, then we need
            // to move this file from the "partial" folder to
            // "complete" folder
            if (isLastPacket)
            {
                moveResourceFromPartialToCompleted(serviceGUID,version,resName);
                // again, if the service is committed, then move the file from
                // tmp to the service folder
                if (m_committedServiceVsProperties.containsKey(getUniqueKey(serviceGUID, version)))
                {
                    moveCompletedResourceFilesToRepository(serviceGUID,version);
                    new File(servicePath+ File.separator+ TEMP_DOWNLOAD_DIR).delete();
                }
                //generateMicroServiceRepoUpdateEvent(serviceGUID, version, resName, MicroServiceRepoUpdateEvent.RESOURCE_UPLOADED);
                MicroServiceRepoEventRaiser.generateServiceRepositoryEvent(serviceGUID, version, resName,
                        MicroServiceRepoUpdateEvent.RESOURCE_UPLOADED, Event.EventCategory.INFORMATION, "");
            }
        }
        catch (Exception e)
        {
            throw new FioranoException(e);
        }
    }


    /**
     *  This method checks if a resource exists in a specified service in the
     *  TES repository.
     *
     * @param serviceGUID The GUID of the service from which the
     *      resource's existance is to be checked.
     * @param version The version of the service from which the
     *      resource's existance is to be checked.
     * @param resName The name of the resource to be checked.
     * @return The boolean value indicating if the resource
     *      file is present in the service in TES repository.
     * @exception FioranoException if the task of checking resource from service
     *      repository fails to succeed.
     * @since Tifosi2.0
     */
    public boolean hasResource(String serviceGUID, String version, String resName)
            throws FioranoException
    {
        try {
            // check the existance of the service
            try {
                getServiceInfo(serviceGUID, version);
            } catch (FioranoException e) {
                    return false;
            }
            String resFilePath = getResourcePath(serviceGUID,version,resName);

            File resFile = new File(resFilePath);
            if (resFile.exists())
                return true;
        } catch (Exception e) {
//            logger.error(Bundle.class,Bundle.ERROR_CHECK_IF_RES_PRESENT,serviceGUID,version,resName,e);          //logging in FESStubManager
            throw new FioranoException(e);
        }

        return false;
    }




    /**
     *  Saves a service using the service property sheet.
     *
     * @param sps The service property sheet that needs to
     *      be saved in the service repository sheet
     * @param deleteOldService delete old service if exists
     * @param killRunningInstances option considered only if above is true
     *                             All the running instances will be stopped
     * @param handleID HandleId holds the information about authentication
     * @return New header of the service that has been
     *      edited
     * @exception FioranoException Exception if, specified GUID and version
     *      already exist.
     */
    public ServiceReference saveService(Service sps, boolean deleteOldService, boolean killRunningInstances, String handleID)
            throws FioranoException
    {
        //SECURITY CHECK
        boolean serviceDeleted = false;
        try{
           // handleSecurityCheck(handleID, PermissionImpl.getPermissionName(PermissionImpl.CONFIGURE_SERVICE));

            // check for the existance of the service of this version. If its
            // already available, exception needs to be thrown
            String serviceGUID = sps.getGUID();

            if (sps.getVersion() <= 0)
                throw new FioranoException("component version invalid");

            //this is checked at RTL level and not allowed!!!
//        if (sps.getVersion() <= 0)
//        {
//            String highestVersion = getHighestVersionOfService(serviceGUID);
//            float version = MicroServiceConstants.DEFAULT_VERSION_NO;
//
//            if (highestVersion != null)
//            {
//                version = Float.parseFloat(highestVersion);
//                version += m_config.getDefaultVersionIncrement();
//            }
//            sps.setVersion(version);
//        }

            String version = String.valueOf(sps.getVersion());

            if (m_committedServiceVsProperties.get(getUniqueKey(sps.getGUID(), sps.getVersion() + "")) != null){
                if(!deleteOldService)
                    throw new FioranoException("service with same version already exists");
                //LogHelper.getErrMessage(ILogModule.SERVICE_REPOSITORY, 48, sps.getGUID(), "" + sps.getVersion()));
                //delete if service is present in the committed list
                File fileVersion = new File(getServicePath(serviceGUID, version));
                if (!fileVersion.exists())
                    throw new FioranoException("ERROR_SERVICE_VERSION_DOES_NOT_EXIST");

                removeServiceVersion(serviceGUID, version);

                serviceDeleted = true;
            }

            // Create the folder needed to store the service
            //try
            //{
            String servicePath = getServicePath(sps.getGUID(), version);
            new File(servicePath).mkdirs();

            // save the ServiceDescriptor.xml file
            saveServiceDescriptor(sps);

            // ServiceDescriptor.xml is successfully written

            // Now we create the directory structure in the partial and
            // complete folder in the tmp folder where the directory
            // structure for the component will be created.
            String partFileFolder = getPartialResourceFolder(serviceGUID, version);
            new File(partFileFolder).mkdirs();

            String compFileFolder = getCompletedResourceFolder(serviceGUID,String.valueOf(sps.getVersion()));
            new File(compFileFolder).mkdirs();

            // try to reopen the service. If there is exception in that, then
            // remove the service and throw an exception stating this
            try
            {
                getServicePropertySheet(serviceGUID, version);
            }
            catch (Exception e)
            {
//                logger.error(Bundle.class,Bundle.ERROR_GET_SERVICE_PROPERTY_SHEET_1,sps.getGUID(),String.valueOf(sps.getVersion()));            //logging in FESStubManager
                removeService(serviceGUID, version, true, null);
                throw new FioranoException(e);
            }

            // add the sps info into the in memory table
            m_nonCommittedServiceVsProperties.put(getUniqueKey(sps.getGUID(), sps.getVersion() + ""), sps);
            if(serviceDeleted){
                // Fix :: Bug ID :: 7300 :: Start
                // generate service updation event
                //generateMicroServiceRepoUpdateEvent(sps.getGUID(), "" + sps.getVersion(), MicroServiceRepoUpdateEvent.SERVICE_OVERWRITTEN);
                MicroServiceRepoEventRaiser.generateServiceRepositoryEvent(serviceGUID, version, null,
                        MicroServiceRepoUpdateEvent.SERVICE_OVERWRITTEN, Event.EventCategory.INFORMATION, "");
                // Fix :: Bug ID :: 7300 :: End

            } else{
                // Fix :: Bug ID :: 7300 :: Start
                // generate service updation event
                //generateMicroServiceRepoUpdateEvent(sps.getGUID(), "" + sps.getVersion(), MicroServiceRepoUpdateEvent.SERVICE_CREATED);
                MicroServiceRepoEventRaiser.generateServiceRepositoryEvent(serviceGUID, version, null,
                        MicroServiceRepoUpdateEvent.SERVICE_CREATED, Event.EventCategory.INFORMATION, "");
                // Fix :: Bug ID :: 7300 :: End

            }


        }
        catch (Exception ex)
        {
            throw new FioranoException(ex);
        }

        // All the place holders for the service have been created. Returning from
        // here. The updateResources API will be called now on to fill up the data
        // in the repository files.

        return new ServiceReference(sps);
    }


    /**
     *  Edits a saved but unpublished service with the new values from the
     *  service property sheet that is passed as parameter
     *
     * @param serviceGUID The GUID of the service that is to be edited
     * @param version The version of the service that is to be
     *      edited
     * @param sps The new service property sheet that needs to
     *      be saved in the service repository sheet
     * @param handleID HandleId holds the information about authentication
     * @return New header of the service that has been
     *      edited
     * @exception FioranoException Description of the Exception
     */
    public ServiceReference editService(String serviceGUID, String version, Service sps, String handleID) throws FioranoException
    {
        try {
            if (m_committedServiceVsProperties.get(getUniqueKey(serviceGUID, version)) != null)
                throw new FioranoException("ERROR_EDITING_SERVICE");
            ////LogHelper.getErrMessage(ILogModule.SERVICE_REPOSITORY, 38));

            if (m_nonCommittedServiceVsProperties.get(getUniqueKey(serviceGUID, version)) == null)
                throw new FioranoException("ERROR_EDITING_SERVICE");
            //LogHelper.getErrMessage(ILogModule.SERVICE_REPOSITORY, 39));

            if (!sps.getGUID().equalsIgnoreCase(serviceGUID) || sps.getVersion() != Float.parseFloat(version)) {
                throw new FioranoException("ERROR_EDITING_SERVICE");
                //LogHelper.getErrMessage(ILogModule.SERVICE_REPOSITORY, 25));
            }

            try {
                saveServiceDescriptor(sps);
            }
            catch (Exception e) {
                throw new FioranoException("ERROR_EDITING_SERVICE");
            }
            // change this info into the hashtable
            m_nonCommittedServiceVsProperties.put(getUniqueKey(serviceGUID, version), sps);

            // Fix :: Bug ID :: 7300 :: Start
            // generate service updation event
           // generateMicroServiceRepoUpdateEvent(serviceGUID, "" + version, MicroServiceRepoUpdateEvent.UNREGISTERED_SERVICE_EDITED);
            MicroServiceRepoEventRaiser.generateServiceRepositoryEvent(serviceGUID, version, null,
                    MicroServiceRepoUpdateEvent.UNREGISTERED_SERVICE_EDITED, Event.EventCategory.INFORMATION, "");
            return new ServiceReference(sps);
        } catch (FioranoException e) {
            throw e;
        }
    }

    /**
     *  This method edits a registered service identified by the
     *  <code>serviceGUID</code> and <code>version</code> arguments with
     *  new values from the <code>ServicePropertySheet</code> argument.
     *  It returns the modified <code>ServiceHeader</code>. This only edits
     *  those regsitered services which are in QA or Development Stage.
     *
     * @param serviceGUID The GUID of the service that is to be edited
     * @param version The version of the service that is to be
     *      edited
     * @param sps The new service property sheet that needs to
     *      be saved in the service repository sheet
     * @param handleID
     * @return New header of the service that has been
     *      edited
     * @exception FioranoException if the process of editing service fails to
     *      complete successfully.
     * @since Tifosi2.0
     */
    public ServiceReference editRegisteredService(String serviceGUID, String version,
                                                  Service sps, String handleID)
            throws FioranoException
    {
        //SECURITY CHECK
        try {

            if (m_nonCommittedServiceVsProperties.get(getUniqueKey(serviceGUID, version)) != null)
                throw new FioranoException("ERROR_EDITING_REGISTERED_SERVICE");
            //LogHelper.getErrMessage(ILogModule.SERVICE_REPOSITORY, 29, serviceGUID, version));

            if (m_committedServiceVsProperties.get(getUniqueKey(serviceGUID, version)) == null)
                throw new FioranoException("ERROR_EDITING_REGISTERED_SERVICE");
            //LogHelper.getErrMessage(ILogModule.SERVICE_REPOSITORY, 30, serviceGUID, version));

            if (!sps.getGUID().equalsIgnoreCase(serviceGUID)
                    || sps.getVersion() != Float.parseFloat(version)) {
                throw new FioranoException("ERROR_EDITING_REGISTERED_SERVICE");
                //LogHelper.getErrMessage(ILogModule.SERVICE_REPOSITORY, 31));
            }

            // get the current deployment stage of the service.
            //    o If it is Product, then modification is not allowed.
            //    o If it is QA, only service dependencies can be changed
            //    o If it is Development, all changes are allowed.
            Service oldSPS =
                    (Service) m_committedServiceVsProperties.get(getUniqueKey(serviceGUID, version));

            Service spsToWrite = sps;

            Deployment oldDeployment = oldSPS.getDeployment();
            Deployment newDeployment = sps.getDeployment();

            // if trying to move from QA to Development, then throw an exception
            if (oldDeployment != null && oldDeployment.getLabel() != null
                    && oldDeployment.getLabel().equalsIgnoreCase(Deployment.QA)
                    && newDeployment != null && newDeployment.getLabel() != null
                    && newDeployment.getLabel().equalsIgnoreCase(Deployment.DEVELOPMENT))
                throw new FioranoException("ERROR_EDITING_REGISTERED_SERVICE");
            //LogHelper.getErrMessage(ILogModule.SERVICE_REPOSITORY, 32, serviceGUID, version));

            if (oldDeployment != null && oldDeployment.getLabel() != null
                    && oldDeployment.getLabel().equalsIgnoreCase(Deployment.QA)) {
                spsToWrite = oldSPS;
                oldDeployment.getServiceRefs().clear();
                if (newDeployment != null) {
                    Iterator _enum = newDeployment.getServiceRefs().iterator();

                    while (_enum != null && _enum.hasNext()) {
                        ServiceRef serDep = (ServiceRef) _enum.next();
                        oldDeployment.addServiceRef(serDep);
                    }

                    if (newDeployment.getLabel() != null
                            && newDeployment.getLabel().equalsIgnoreCase(Deployment.PRODUCT))
                        oldDeployment.setLabel(Deployment.PRODUCT);
                }
                spsToWrite.setDeployment(oldDeployment);
            } else if (oldDeployment != null && oldDeployment.getLabel() != null
                    && oldDeployment.getLabel().equalsIgnoreCase(Deployment.DEVELOPMENT)) {
                spsToWrite = sps;
            }

            // finally change the "lastUpdated" time for the service since we are
            // updating the service
//        ServiceHeader serviceHeader = spsToWrite.getServiceHeader();
//
            spsToWrite.setLastModifiedDate(new Date(System.currentTimeMillis()));
//        spsToWrite.setServiceHeader(serviceHeader);

            try {

                Enumeration old_resources = getAllResourcesForService(serviceGUID, version);

                saveServiceDescriptor(sps);

                while (old_resources != null && old_resources.hasMoreElements()) {
                    Resource res = (Resource) old_resources.nextElement();

                    if (hasResource(serviceGUID, version, res.getName()) && !(isPresentInNewDeployment(res, newDeployment))) {
                        removeResource(serviceGUID, version, res, handleID);
                    }
                }


            }
            catch (Exception e) {
                //if (TifTrace.ServiceRepository > 5)
                //    LogHelper.log(ILogModule.SERVICE_REPOSITORY, e);
                //            logger.error(Bundle.class,Bundle.ERROR_EDIT_SERVICE_1,serviceGUID,version,e);            //logging in FESStubManager
                throw new FioranoException("ERROR_EDITING_REGISTERED_SERVICE",e);
                //LogHelper.getErrMessage(ILogModule.SERVICE_REPOSITORY,33, serviceGUID, version), e);
            }
            // change this info into the hashtable
            m_committedServiceVsProperties.put(getUniqueKey(serviceGUID, version), spsToWrite);

            //generateMicroServiceRepoUpdateEvent(serviceGUID, version, MicroServiceRepoUpdateEvent.REGISTERED_SERVICE_EDITED);
            MicroServiceRepoEventRaiser.generateServiceRepositoryEvent(serviceGUID, version, null,
                    MicroServiceRepoUpdateEvent.REGISTERED_SERVICE_EDITED, Event.EventCategory.INFORMATION, "");
            return new ServiceReference(spsToWrite);
        } catch (FioranoException e) {
            throw e;
        }
    }


    /**
     *  Overwrites on to an existing version.Adds the resource info to the
     *  service referenced by the ServiceHandle.
     *
     * @param serviceGUID The feature to be added to the Resource
     *      attribute
     * @param version The feature to be added to the Resource
     *      attribute
     * @param resrcInfo The feature to be added to the Resource
     *      attribute
     * @param handleID The feature to be added to the Resource
     *      attribute
     * @exception FioranoException Description of the Exception
     */
    public void addResource(String serviceGUID, String version, Resource resrcInfo, String handleID)
            throws FioranoException
    {

        try
        {   
            //Service sps = getServicePropertySheet(new File(getServiceDescriptorFilePath(serviceGUID,version)));
            Service sps = getServicePropertySheet(serviceGUID, version);

            // check if this resource is present already or not
            Deployment deployment = sps.getDeployment();

            if (deployment != null)
            {
                Iterator enu = deployment.getResources().iterator();

                while (enu.hasNext())
                {
                    Resource res = (Resource) enu.next();

                    if (!res.equals(resrcInfo))
                        continue;
                    // check if the fields other then the resource name
                    // are same or not
                    if (res.equals(resrcInfo))
                    {
                        //LogHelper.log(ILogModule.SERVICE_REPOSITORY, 0);
                        deployment.removeResource(res);
                        deployment.addResource(resrcInfo);
                        sps.setDeployment(deployment);//Bug Fix No.8393...Uday.K
                        return;
                        // not updating the XML as there is no need as the resouce information is already the same.
                    }

                    // The resources are not exactly the same only the names are same.
                    deployment.removeResource(res);
                    //
                    //These lower steps would be executed once it exits the loop..So no need of doing it twice..Uday.K
                    //
                    /* deployment.addResource(resrcInfo);
                    sps.setDeploymentInfo(deployment);

                    // we need to put the updated date also in service header.
                    ServiceHeader header = sps.getServiceHeader();

                    header.setUpdatedDate(new Date(System.currentTimeMillis()));
                    sps.setServiceHeader(header);

                    // Write back the modified xml file for the service
                    writeFileData(servicePath + File.separator + MicroServiceConstants.SERVICE_DESCRIPTOR_FILE_NAME,
                        sps.toXMLString(ITifConstants.VERSION_NO).getBytes());

                    // Added for staging. Now addResource can also be called for
                    // committed services.
                    if(isCommited(getUniqueKey(serviceGUID, version)))
                        m_committedServiceVsProperties.put(getUniqueKey(serviceGUID, version), sps);
                    else
                        m_nonCommittedServiceVsProperties.put(getUniqueKey(serviceGUID, version), sps);*/
                    break;
                }
            }

            if (deployment == null)
                deployment = new Deployment();
            deployment.addResource(resrcInfo);
            sps.setDeployment(deployment);

            // we need to put the updated date also in service header.
            sps.setLastModifiedDate(new Date(System.currentTimeMillis()));

            // Write back the modified xml file for the service
            saveServiceDescriptor(sps);

            // Added for staging. Now addResource can also be called for
            // committed services.
            if (m_committedServiceVsProperties.get(getUniqueKey(serviceGUID, version)) != null)
                m_committedServiceVsProperties.put(getUniqueKey(serviceGUID, version), sps);
            else
                m_nonCommittedServiceVsProperties.put(getUniqueKey(serviceGUID, version), sps);

            //generateMicroServiceRepoUpdateEvent(serviceGUID, version, resrcInfo.getName(), MicroServiceRepoUpdateEvent.RESOURCE_CREATED);
            MicroServiceRepoEventRaiser.generateServiceRepositoryEvent(serviceGUID, version, resrcInfo.getName(),
                    MicroServiceRepoUpdateEvent.RESOURCE_CREATED, Event.EventCategory.INFORMATION, "");
        }
        catch (Exception e)
        {
            throw new FioranoException("ERROR_ADDING_RESOURCE",e);
        }
    }


    /**
     *  Adds a feature to the Icon attribute of the IServiceRepository object
     *
     * @param serviceGUID The feature to be added to the Icon attribute
     * @param version The feature to be added to the Icon attribute
     * @param iconFileName The feature to be added to the Icon attribute
     * @param iconFileBytes The feature to be added to the Icon attribute
     * @param handleID The feature to be added to the Icon attribute
     * @exception FioranoException Description of the Exception
     */
    public void addIcon(String serviceGUID, String version,
                        String iconFileName, byte[] iconFileBytes, String handleID)
            throws FioranoException
    {

        try
        {   
            Service sps = getServicePropertySheet(serviceGUID, version);

            if(!iconFileName.equals(sps.getIcon16())){
                sps.setIcon16(iconFileName);

                // Write back the modified xml file for the service
                saveServiceDescriptor(sps);

                m_nonCommittedServiceVsProperties.put(getUniqueKey(serviceGUID, version), sps);
            }

            // Write the icon file
            File icon = getIcon(serviceGUID,version,iconFileName);
            ComponentRepositoryUtil.writeFileData(icon, iconFileBytes);

            // Generate an event.
            //generateMicroServiceRepoUpdateEvent(serviceGUID, version, iconFileName, MicroServiceRepoUpdateEvent.RESOURCE_CREATED);
            MicroServiceRepoEventRaiser.generateServiceRepositoryEvent(serviceGUID, version, iconFileName,
                    MicroServiceRepoUpdateEvent.RESOURCE_CREATED, Event.EventCategory.INFORMATION, "");
        }
        catch (Exception e)
        {
            throw new FioranoException("ERROR_ADD_ICON",e);
        }
    }

    /**
     *  Changes the Icon for the given service
     *
     * @param serviceGUID The Service name for which the icon is to be changed
     * @param version The version of the service
     * @param iconFileName The name of the icon to be added
     * @param iconFileBytes Icon file as byte array
     * @param handleID The security handle
     * @exception FioranoException If there is error in changing the icon
     */

    public void updateIcon32(String serviceGUID, String version,
                             String iconFileName, byte[] iconFileBytes, String handleID)
            throws FioranoException
    {
        String oldIcon = getServiceInfo(serviceGUID,version).getIcon32();

        try{
            File icon = getIcon(serviceGUID,version,oldIcon);
            icon.delete();
        }
        catch (Exception e)
        {
            throw new FioranoException("ERROR_RESOURCE_UPDATE_ERROR",e);
        }

        if(iconFileBytes!=null)
            addIcon32(serviceGUID, version, iconFileName,iconFileBytes, handleID);
        else{
            Service sps = null;
            try{
                sps = getServicePropertySheet(serviceGUID, version);
                sps.setIcon32(null);
                saveServiceDescriptor(sps);
            } catch(Exception e){
                throw new FioranoException("ERROR_RESOURCE_UPDATE_ERROR",e);
            }

            // Generate an event.
            //generateMicroServiceRepoUpdateEvent(serviceGUID, version, iconFileName, MicroServiceRepoUpdateEvent.RESOURCE_CREATED);
            MicroServiceRepoEventRaiser.generateServiceRepositoryEvent(serviceGUID, version, iconFileName,
                    MicroServiceRepoUpdateEvent.RESOURCE_CREATED, Event.EventCategory.INFORMATION, "");
        }
    }
    /**
     *  Changes the Icon for the given service
     *
     * @param serviceGUID The Service name for which the icon is to be changed
     * @param version The version of the service
     * @param iconFileName The name of the icon to be added
     * @param iconFileBytes Icon file as byte array
     * @param handleID The security handle
     * @exception FioranoException If there is error in changing the icon
     */

    public void updateIcon(String serviceGUID, String version,
                           String iconFileName, byte[] iconFileBytes, String handleID)
            throws FioranoException
    {

        String oldIcon = getServiceInfo(serviceGUID,version).getIcon16();

        try{
            File icon = getIcon(serviceGUID,version,oldIcon);
            icon.delete();
        }
        catch(Exception e){
            throw new FioranoException("ERROR_RESOURCE_UPDATE_ERROR",e);
        }

        if(iconFileBytes!=null)
            addIcon(serviceGUID, version, iconFileName,iconFileBytes, handleID);
        else{
            Service sps = null;
            try{
                sps = getServicePropertySheet(serviceGUID, version);
                sps.setIcon16(null);
                saveServiceDescriptor(sps);
            } catch(Exception e){
                throw new FioranoException("ERROR_RESOURCE_UPDATE_ERROR",e);
            }

            // Generate an event.
            //generateMicroServiceRepoUpdateEvent(serviceGUID, version, iconFileName, MicroServiceRepoUpdateEvent.RESOURCE_CREATED);
            MicroServiceRepoEventRaiser.generateServiceRepositoryEvent(serviceGUID, version, iconFileName,
                    MicroServiceRepoUpdateEvent.RESOURCE_CREATED, Event.EventCategory.INFORMATION, "");
        }

    }
    /**
     *  Adds a feature to the Icon attribute of the IServiceRepository object
     *
     * @param serviceGUID The feature to be added to the Icon attribute
     * @param version The feature to be added to the Icon attribute
     * @param iconFileName The feature to be added to the Icon attribute
     * @param iconFileBytes The feature to be added to the Icon attribute
     * @param handleID The feature to be added to the Icon attribute
     * @exception FioranoException Description of the Exception
     */
    public void addIcon32(String serviceGUID, String version,
                          String iconFileName, byte[] iconFileBytes, String handleID)
            throws FioranoException
    {

        try
        {  
            Service sps = getServicePropertySheet(serviceGUID, version);

            if(!iconFileName.equals(sps.getIcon32())){
                sps.setIcon32(iconFileName);

                // Write back the modified xml file for the service
                saveServiceDescriptor(sps);
                m_nonCommittedServiceVsProperties.put(getUniqueKey(serviceGUID, version), sps);
            }

            // Write the icon file
            File icon = getIcon(serviceGUID,version,iconFileName);
            ComponentRepositoryUtil.writeFileData(icon, iconFileBytes);

            // Generate an event.
            //generateMicroServiceRepoUpdateEvent(serviceGUID, version, iconFileName, MicroServiceRepoUpdateEvent.RESOURCE_CREATED);
            MicroServiceRepoEventRaiser.generateServiceRepositoryEvent(serviceGUID, version, iconFileName,
                    MicroServiceRepoUpdateEvent.RESOURCE_CREATED, Event.EventCategory.INFORMATION, "");
        }
        catch (Exception e)
        {
            throw new FioranoException("ERROR_ADD_ICON32",e);
        }
    }

    /**
     * Removes the Icon for the Service
     * @param serviceGUID GUIDName of the component
     * @param version   version of the component
     * @param iconFileName Name of the Icon image file
     * @param handleID holds the information about the authentication
     * @exception FioranoException
     */
//    public void removeIcon(String serviceGUID, String version,
//                           String iconFileName, String handleID)
//            throws FioranoException
//    {
//        //SECURITY CHECK
//        handleSecurityCheck(handleID, PermissionImpl.getPermissionName(PermissionImpl.CONFIGURE_SERVICE));
//
//        try
//        {
//            Service sps = getServicePropertySheet(serviceGUID, version);
//
//            sps.setIcon16(null);
//
//            // Write back the modified xml file for the service
//            saveServiceDescriptor(sps);
//
//            FileObject icon = getIcon(serviceGUID,version,iconFileName);
//            icon.close();
//            icon.delete();
//
//            // Generate an event.
//            generateMicroServiceRepoUpdateEvent(serviceGUID, version,
//                    MicroServiceRepoUpdateEvent.RESOURCE_CREATED);
//        }
//        catch (Exception e)
//        {
//            if (TifTrace.ServiceRepository > 5)
//                LogHelper.log(ILogModule.SERVICE_REPOSITORY, e);
//
//            throw new FioranoException(ComponentErrorCodes.ERROR_REMOVE_ICON, LogHelper.getErrMessage(ILogModule.SERVICE_REPOSITORY, 36, iconFileName, serviceGUID, version), e);
//        }
//    }

    /**
     * Removes the Icon for the Service
     * @param serviceGUID GUIDName of the component
     * @param version   version of the component
     * @param iconFileName Name of the Icon image file
     * @param handleID holds the information about the authentication
     * @exception FioranoException
     */
//    public void removeIcon32(String serviceGUID, String version,
//                             String iconFileName, String handleID)
//            throws FioranoException
//    {
//        //SECURITY CHECK
//        handleSecurityCheck(handleID, PermissionImpl.getPermissionName(PermissionImpl.CONFIGURE_SERVICE));
//
//        try
//        {
//            Service sps = getServicePropertySheet(serviceGUID, version);
//
//            sps.setIcon32(null);
//
//            // Write back the modified xml file for the service
//            FileObject sd = getServiceDescriptor(serviceGUID,version);
//            sps.toXMLString(sd.getContent().getOutputStream());
//            sd.close();
//
//            FileObject icon = getIcon(serviceGUID,version,iconFileName);
//            icon.close();
//            icon.delete();
//
//            // Generate an event.
//            generateMicroServiceRepoUpdateEvent(serviceGUID, version,
//                    MicroServiceRepoUpdateEvent.RESOURCE_CREATED);
//        }
//        catch (Exception e)
//        {
//            if (TifTrace.ServiceRepository > 5)
//                LogHelper.log(ILogModule.SERVICE_REPOSITORY, e);
//
//            throw new FioranoException(ComponentErrorCodes.ERROR_REMOVE_ICON, LogHelper.getErrMessage(ILogModule.SERVICE_REPOSITORY, 36, iconFileName, serviceGUID, version), e);
//        }
//    }


    /**
     *  Deletes the resource info from the service referenced by the
     *  ServiceHandle.
     *
     * @param resrcInfo ResourceInfo
     * @param serviceGUID Description of the Parameter
     * @param version Description of the Parameter
     * @param handleID Description of the Parameter
     * @exception FioranoException Exception
     */
    public void removeResource(String serviceGUID, String version, Resource resrcInfo, String handleID)
            throws FioranoException
    {
        // if the service have already been comitted, then the remove resource
        // should not be allowed
        // Added another condition that the Service is in PRODUCT Stage
        try{
            Service sps;
            boolean isCommittedService = false;
            Service existingSPS = (Service)m_committedServiceVsProperties.get(getUniqueKey(serviceGUID, version));
            if (existingSPS != null)
                isCommittedService = true;
            
            //getServicePropertySheet loads the updated sps from repository which will no longer have de resource to b deleted.
            sps = getServicePropertySheet(serviceGUID, version);

            Deployment deployment = existingSPS.getDeployment();//Bug 18643.Use sps got from committed services VS Properties data structure.=> its the old one which contains the resource.

            Resource actualResource = (Resource) DmiObject.findNamedObject(deployment.getResources(), resrcInfo.getName());
            if(actualResource==null)
                return;
            deployment.removeResource(actualResource);
            sps.setDeployment(deployment);

            String resPath = getResourcePath(serviceGUID,version,resrcInfo.getName());

            File toBeDeleted = new File(resPath);
            toBeDeleted.delete();

            // Write back the modified xml file for the service
            File fo = getServiceDescriptor(serviceGUID,version);
            sps.toXMLString(new FileOutputStream(fo));

            if (isCommittedService)
                m_committedServiceVsProperties.put(getUniqueKey(serviceGUID, version), sps);
            else
                m_nonCommittedServiceVsProperties.put(getUniqueKey(serviceGUID, version), sps);

           // generateMicroServiceRepoUpdateEvent(serviceGUID, version, resrcInfo.getName(), MicroServiceRepoUpdateEvent.RESOURCE_REMOVED);
            MicroServiceRepoEventRaiser.generateServiceRepositoryEvent(serviceGUID, version, resrcInfo.getName(),
                    MicroServiceRepoUpdateEvent.RESOURCE_REMOVED, Event.EventCategory.INFORMATION, "");

        }
        catch (Exception e)
        {
            throw new FioranoException("ERROR_RESOURCE_DELETE_FAILURE_ERROR",e);
        }
    }


    /**
     *  Copies a published service to another service/version in the service
     *  repository. This new service can be edited and published later
     *
     * @param srcServiceGUID The source service GUID
     * @param srcVersion The source version from which the service is
     *      to be copied
     * @param tgtServiceGUID The target service GUID to which the service
     *      is to be copied. If this is passed as null, the service is copied
     *      with the same service GUID as the source and the different version
     *      number
     * @param handleID Description of the Parameter
     * @param strTgtVersion
     * @return Service Header for the new version of the
     *      service
     * @exception FioranoException Description of the Exception
     */
    public ServiceReference copyServiceVersion(String srcServiceGUID, String srcVersion,
                                               String tgtServiceGUID, String strTgtVersion, String handleID)
            throws FioranoException
    {

        // FIX for bugID: 8735 :: START.
        float tgtVersionVal = 0;

        try
        {
            tgtVersionVal = Float.parseFloat(strTgtVersion);
        }
        catch (Exception ex)
        {
            //Do nothing.
        }

        Enumeration existingVersions = getAllVersionsOfService(tgtServiceGUID, false);

        while (existingVersions.hasMoreElements())
        {
            ServiceReference header = (ServiceReference) existingVersions.nextElement();
            float versionNumber = header.getVersion();

            if (versionNumber == tgtVersionVal)
                throw new FioranoException("ERROR_SERVICE_UPDATE_ERROR");
            //LogHelper.getErrMessage(ILogModule.SERVICE_REPOSITORY, 48, strTgtVersion, tgtServiceGUID));
        }

        // FIX for bugID: 8735 :: END.

        try
        {
            Service sps = getServicePropertySheet(srcServiceGUID, srcVersion);

            // now we have to create new temporary folders with this sps

            if (tgtServiceGUID != null)
                sps.setGUID(tgtServiceGUID);

            float tgtVersion;

            try
            {
                tgtVersion = Float.parseFloat(strTgtVersion);
            }
            catch (Exception e)
            {
                throw new FioranoException("INVALID_VERSION");
                //LogHelper.getErrMessage(ILogModule.SERVICE_REPOSITORY, 49, strTgtVersion, tgtServiceGUID));
            }
            sps.setVersion(tgtVersion);

            // FIX FOR BUG# 6826 : START
            if (srcServiceGUID.equalsIgnoreCase(sps.getGUID())
                    && srcVersion.equalsIgnoreCase(strTgtVersion))
                throw new FioranoException("ERROR_COPY_SERVICE_2");
            //LogHelper.getErrMessage(ILogModule.SERVICE_REPOSITORY, 50, srcServiceGUID, srcVersion, tgtServiceGUID, strTgtVersion));

            // FIX FOR BUG# 6826 : END


            String srcFolder = getServicePath(srcServiceGUID,srcVersion);
            File src = new File(srcFolder);
            String tgtFolder = getCompletedResourceFolder(tgtServiceGUID,""+tgtVersion);
            File tgt = new File(tgtFolder);
            ComponentRepositoryUtil.copyChildrenToTgtFolder(src,tgt);

            //this shudnt copy service descriptor!!!
            //todo fix the flow rather than doing this!!!
            new File(tgt, "ServiceDescriptor.xml").delete();


            //generateMicroServiceRepoUpdateEvent(tgtServiceGUID, strTgtVersion, MicroServiceRepoUpdateEvent.SERVICE_CREATED);
            MicroServiceRepoEventRaiser.generateServiceRepositoryEvent(tgtServiceGUID, strTgtVersion, null,
                    MicroServiceRepoUpdateEvent.SERVICE_CREATED, Event.EventCategory.INFORMATION, "");
            //bug 4193 fix

            // Bug# 10774
            // return saveService(sps, null);
            return saveService(sps, false,false, handleID);
        }
        catch (Exception e)
        {
            throw new FioranoException("ERROR_SERVICE_SAVE_ERROR",e);
        }
    }


    /**
     *  Publishes a the service becomes available to all the peers on the
     *  network
     *
     * @param serviceGUID The GUID of the service to be finalizes
     * @param version The version of the service to be finalized
     * @param handleID Description of the Parameter
     * @return The header of the service that has been
     *      published
     * @exception FioranoException Description of the Exception
     */
    public ServiceReference publishService(String serviceGUID, String version, String handleID)
            throws FioranoException
    {
        //SECURITY CHECK
        // handleSecurityCheck(handleID, PermissionImpl.getPermissionName(PermissionImpl.CONFIGURE_SERVICE));

        try {
            return commit(serviceGUID, version, Deployment.PRODUCT, handleID);
        } catch (FioranoException e) {
//            logger.error(Bundle.class,Bundle.ERROR_PUBLISH_SERVICE,serviceGUID,version,e);          //logging in FESStubManager
            throw e;
        }
    }

    /**
     *  Publishes a the service becomes available to all the peers on the
     *  network
     *
     * @param serviceGUID The GUID of the service to be finalizes
     * @param version The version of the service to be finalized
     * @param handleID Description of the Parameter
     * @param stage
     * @return The header of the service that has been
     *      published
     * @exception FioranoException Description of the Exception
     */
    public ServiceReference publishService(String serviceGUID, String version, String stage, String handleID)
            throws FioranoException
    {
        //SECURITY CHECK
        //handleSecurityCheck(handleID, PermissionImpl.getPermissionName(PermissionImpl.CONFIGURE_SERVICE));

        try {
            if (stage == null)
                return commit(serviceGUID, version, Deployment.PRODUCT, handleID);
            else
                return commit(serviceGUID, version, stage, handleID);
        } catch (FioranoException e) {
//            logger.error(Bundle.class,Bundle.ERROR_PUBLISH_SERVICE,serviceGUID,version,e);          //logging in FESStubManager
            throw e;
        }
    }

    /**
     *  Removes the specified service from the service repository
     *
     * @param serviceGUID The GUID of the service which is to be
     *      removed
     * @param version The version of the service that is to be
     *      removed. If null is passed as version all the versions of the
     *      service are removed from the repository
     * @param handleID Description of the Parameter
     * @return An enumeration containing the ServiceHeader
     *      values of all the services removed
     * @exception FioranoException Description of the Exception
     */
    public Enumeration removeService(String serviceGUID, String version, boolean killRunningInstances, String handleID)
            throws FioranoException
    {
        try {
            Vector allRemovedServices = new Vector();

            if (version == null)
            {
                Enumeration versions = getAllServiceVersions(serviceGUID);
                while(versions.hasMoreElements())
                {
                    ServiceReference header = _removeService(serviceGUID, (String) versions.nextElement(), killRunningInstances, handleID);
                    if (header != null)
                        allRemovedServices.addElement(header);
                }
            }
            else
            {
                ServiceReference header = _removeService(serviceGUID, version, killRunningInstances, handleID);
                if (header != null)
                    allRemovedServices.addElement(header);
            }


            return allRemovedServices.elements();
        } catch (Exception e) {
//            logger.error(Bundle.class,Bundle.ERROR_REMOVING_SERVICE,serviceGUID,version,e);           //logging in FESStubManager
            throw new FioranoException("ERROR_REMOVING_SERVICE",e);
        }
    }

    private ServiceReference _removeService(String serviceGUID, String version, boolean killRunningInstances, String handleID)
            throws FioranoException, IOException
    {
        try {
            if (serviceGUID == null || version == null)
                throw new FioranoException("INVALID_GUID_VERSION");

            File fileVersion = new File(getServicePath(serviceGUID, version));
            if (!fileVersion.exists())
                throw new FioranoException("ERROR_SERVICE_VERSION_DOES_NOT_EXIST");

            ServiceReference header = removeServiceVersion(serviceGUID, version);

            //generateMicroServiceRepoUpdateEvent(serviceGUID, version, MicroServiceRepoUpdateEvent.SERVICE_REMOVED);
            MicroServiceRepoEventRaiser.generateServiceRepositoryEvent(serviceGUID, version, null,
                    MicroServiceRepoUpdateEvent.SERVICE_REMOVED, Event.EventCategory.INFORMATION, "");
            return header;
        } catch (FioranoException e) {
            throw e;
        }
    }


    /**
     * @param serviceGUID
     * @param version
     * @exception FioranoException
     */
    public void checkServiceResourceFiles(String serviceGUID, String version)
            throws FioranoException
    {
        Service sps = (Service) m_committedServiceVsProperties.get(getUniqueKey(serviceGUID, version));

        if (sps == null || sps.getDeployment() == null)
            return;
        Deployment deploymentInfo = sps.getDeployment();
        int currentOS = deploymentInfo.getOperatingSystem(System.getProperty("os.name"));

        Iterator enumResources = deploymentInfo.getResources().iterator();

        while (enumResources.hasNext())
        {
            Resource resource = (Resource) enumResources.next();
            if(!resource.isRequiredForConfiguration() && !resource.isRequiredForExecution())
                continue;
            if(!(resource.isOperatingSystemSupported(currentOS)))
                continue;
            String resName = resource.getName();
            String resFileName = getResourcePath(serviceGUID,version,resName);
                File resFile = new File(resFileName);
                if (!resFile.exists())
                    throw new FioranoException("ERROR_SERVICE_DETAILS_FETCH_ERROR");
                //LogHelper.getErrMessage(ILogModule.SERVICE_REPOSITORY, 102, resName, resFileName));
        }
        //todo: log at higher level all exceptions thrown here
        // check the icon file now
        // TODO:: Please uncomment the following lines for fixing bug: 9995.
        //
//        String iconFileName = sps.getServiceHeader().getIcon();
//
//        if(iconFileName == null)
//          return;
//
//        String iconFile = servicePath + File.separator + iconFileName;
//        File icon = new File(iconFile);
//        if (!icon.exists())
//            throw new FioranoException(SPErrorCodes.ERROR_SERVICE_DETAILS_FETCH_ERROR,
//                    "The icon file mentioned in ServiceDescriptor.xml: "+iconFileName
//                    + "does not exist in the Service folder: "+iconFile);
    }

    /**
     *  Returns an enumeration of ServicePropertySheet objects for the services
     *  that match the search criteria.
     *
     * @param searchContext The search criteria
     * @return Enumeration of ServicePropertySheet
     */
    public Enumeration searchServices(ServiceSearchContext searchContext)
    {
        Enumeration _enum = getAllServicesInRepository();
        Vector vec = new Vector();
        String author = searchContext.getAuthor();
        String name = searchContext.getName();
        String category = searchContext.getCategory();

        while (_enum.hasMoreElements())
        {
            Service sps = (Service) _enum.nextElement();
            // check for the author, criteria and name one by one.
            // * wildcard search is allowed.
            boolean authorMatch = false;
            boolean nameMatch = false;
            boolean categoryMatch = false;
            // check if any of the author matches
            String[] authEum = sps.getAuthors();

            for(int i=0; i<authEum.length; i++)
            {
                String auth = authEum[i];

                if (ComponentRepositoryUtil.compare(author, auth))
                {
                    authorMatch = true;
                    break;
                }
            }

            //check if the name matches
            String spsName = sps.getDisplayName();

            if (ComponentRepositoryUtil.compare(name, spsName))
                nameMatch = true;

            // check if the category matches
            Iterator catEum = sps.getCategories().iterator();

            while (catEum.hasNext())
            {
                String cat = (String) catEum.next();

                if (ComponentRepositoryUtil.compare(category, cat))
                {
                    categoryMatch = true;
                    break;
                }
            }

            if (authorMatch && categoryMatch && nameMatch)
                vec.addElement(sps);
        }
        return vec.elements();
    }



    private String getUniqueKey(String serviceGUID, String version)
            throws FioranoException
    {
        String modifiedVersion;

        try
        {
            float modVersion = Float.parseFloat(version);

            modifiedVersion = "" + modVersion;
        }
        catch (NumberFormatException e)
        {
            throw new FioranoException("INVALID_SERVICE_VERSION_SPECIFIED", e);
            //LogHelper.getErrMessage(ILogModule.SERVICE_REPOSITORY, 63, serviceGUID, version));
        }
        return serviceGUID + ";" + modifiedVersion;
    }

    private String getUniqueKey(String serviceGUID, float version)
    {
        return serviceGUID + ";" + version;
    }


    /**
     *  Given the service path and resource data the partial resource is
     *  updated.If the resource directory path does not exist in the partial
     *  folder it is created before writing data to the resource file.
     *
     * @param serviceGUID Description of the Parameter
     * @param version Description of the Parameter
     * @param resName Description of the Parameter
     * @param startByte Description of the Parameter
     * @param bytes Description of the Parameter
     * @exception IOException Description of the Exception
     */
    private void updatePartialResourceData(String serviceGUID, String version,
                                           String resName, long startByte, byte[] bytes)
            throws IOException
    {
//        String strDirectoryPath = ComponentRepositoryUtil.getDirectoryPath(resName);
        String fileToWrite = getPartialResourceFolder(serviceGUID,version) + File.separator;

//        if (strDirectoryPath != null)
//            fileToWrite=fileToWrite+ strDirectoryPath;

        // file folders need to be created
        File directories = new File(fileToWrite);

        if (!directories.exists())
        {
            directories.mkdirs();
        }
        fileToWrite = fileToWrite + File.separator + resName;

        File fo = new File(fileToWrite);
        boolean append = ((startByte > 0) ? true : false);
        OutputStream fos = new FileOutputStream(fo, append);
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        if ((startByte >= 0) && (bytes != null))
        {
            bos.write(bytes);
        }
        fos.close();
        bos.close();
       
    }

    private void moveResourceFromPartialToCompleted(String serviceGUID, String version, String resName)
            throws FioranoException
    {
        try {
            File src = new File(getPartialResourceFolder(serviceGUID,version) + File.separator + resName);
            File target = new File(getCompletedResourceFolder(serviceGUID,version) + File.separator + resName);
            src.renameTo(target);
        } catch (Exception e) {
            //if (TifTrace.ServiceRepository > 5)
            //    LogHelper.log(ILogModule.SERVICE_REPOSITORY, e);

            throw new FioranoException("ERROR_MOVING_RESOURCE",e);
        }
    }

    /**
     *  Description of the Method
     *
     * @param serviceGUID Description of the Parameter
     * @param version Description of the Parameter
     * @exception FioranoException Description of the Exception
     */
    private void moveCompletedResourceFilesToRepository(String serviceGUID, String version)
            throws FioranoException
    {
        try
        {
            File src = new File(getCompletedResourceFolder(serviceGUID,version));
            File tgt = new File(getServicePath(serviceGUID,version));
            ComponentRepositoryUtil.moveChildrenToTgtFolder(src,tgt);
        }
        catch(Exception e)
        {
            throw new FioranoException("ERROR_MOVING_RESOURCES",e);
            //throw new FioranoException(ComponentErrorCodes.FILE_READ_WRITE_ERROR,e);
        }
    }

    /**
     *  Loads all committed services from the repository in the in memory
     *  Hashtable
     */
    public boolean reloadRepository() throws FioranoException {

        m_committedServiceVsProperties.clear();
        m_nonCommittedServiceVsProperties.clear();

        try {
            boolean toReturn = true;

            // Get all the files in the component reporitoy
            File[] allServices = new File(COMPONENTS_REPOSITORY_FOLDER).listFiles();
            //  Load all services stored in this directory
            for (int i = 0; i < allServices.length; ++i)
                if (allServices[i].isDirectory())
                    toReturn = loadAllVersionsOfService(allServices[i].getName());
            return toReturn;
        } catch (Exception e) {
//            logger.error(Bundle.class,Bundle.IOERROR_RELOAD_COMPONENT_REPOSITORY,e);         //logging in FESStubManager
            throw new FioranoException(e);
        }
    }


    /**
     *  Loads all the versions of the service
     *
     * @param serviceGUID Description of the Parameter
     * @return  boolean
     * @throws FioranoException
     */
    private boolean loadAllVersionsOfService(String serviceGUID) throws FioranoException {
        try {
            File serviceHome = new File(getServiceRootPath(serviceGUID));

            // get all the versions
            File[] allVersions = serviceHome.listFiles();
            boolean toReturn = true;

            //  Load all services stored in this directory
            for (int i = 0; i < allVersions.length; ++i)
            {
                File srvDir = allVersions[i]; //The name of this dir should be the version of the service
                if (srvDir.isDirectory())
                    toReturn = loadServiceVersion(serviceGUID, srvDir.getName());
            }

            return toReturn;
        } catch (Exception e) {
            throw new FioranoException("ERROR_LOADING_ALL_VERSIONS",e);
        }
    }


    /**
     *  Loads a committed service version from the repository in the in-memory
     *  Hashtable
     *
     * @param serviceGUID Description of the Parameter
     * @param version Description of the Parameter
     * @return
     */
    private boolean loadServiceVersion(String serviceGUID, String  version) throws FioranoException {
        // check for the ServiceDescriptor.xml file and if its available
        // then get the list of resources of this service

        File serviceVersionHome;
        String serviceDirName;


        try {
            serviceVersionHome = new File(getServicePath(serviceGUID,version));
            serviceDirName = serviceVersionHome.getParentFile().getName();
        } catch (Exception e) {
            throw new FioranoException(e);
            //throw new FioranoException("error resolving service version path",e);
        }

        Service sps;

        try
        {
            sps = getServicePropertySheet(serviceGUID,version);
            if(sps == null)
                return false;
        }
        catch (Exception e)
        {
            //if(TifTrace.ServiceRepository > TraceLevels.Debug)
            //    e.printStackTrace();
            //Log error in reading service descriptor file
            //LogHelper.logErr(ILogModule.SERVICE_REPOSITORY, 4,serviceGUID,version,e.toString());
            e.printStackTrace();
            return false;
        }

        String guidOfThisService = sps.getGUID();
        // check for the GUID directory we came into is same as the GUID mentioned in the SPS
        if (!serviceDirName.equalsIgnoreCase(guidOfThisService))
        {
            //LogHelper.logErr(ILogModule.SERVICE_REPOSITORY, 7, guidOfThisService,version, serviceDirName);
            return false;
        }

        // check if the version number provided in the sps matches with the version home of service
        String versionNum = sps.getVersion() + "";

        if (!versionNum.equalsIgnoreCase(serviceVersionHome.getName()))
        {
            //LogHelper.logErr(ILogModule.SERVICE_REPOSITORY, 8,guidOfThisService,versionNum);
            return false;
        }

        try {
// Check for the existance of Complete and Partial folder.
            // If these exist, the service has not
            // been published yet
            File compFile = new File(getCompletedResourceFolder(serviceGUID,version));
            File partFile = new File(getPartialResourceFolder(serviceGUID, version));

            // if both the complete and partial folders exist for the specified service version,
            // the service is stored as an un-commited service.
            //todo what it one of these folders only exist
            if (compFile.exists() && partFile.exists())
            {
                m_nonCommittedServiceVsProperties.put(getUniqueKey(
                                sps.getGUID(),
                                sps.getVersion()),
                        sps);
            }
            else
            {
                // None of the temp folders exist, so add it to the commited properties
                m_committedServiceVsProperties.put(getUniqueKey(
                                sps.getGUID(),
                                sps.getVersion()),
                        sps);
            }
        } catch (Exception e) {
            throw new FioranoException("ERROR_LOADING_SERVICE_VERSION",e);
            //throw new FioranoException("error loading service version : "+sps.getGUID()+":"+sps.getVersion(),e);
        }

        // All components loaded successfully.
        return true;
    }


    /**
     * This function sorts the vector of ServiceHeader objects according to
     *  their version number
     *
     * @param vectorOfServiceHeader Vector containing objects of ServiceHeader
     * @return Vector containing objects of ServiceHeader
     *                               according to their version number
     */
    private Vector sortAccordingToVersionNumber(Vector vectorOfServiceHeader)
    {
        Vector toReturn = new Vector();
        float toSort[] = new float[vectorOfServiceHeader.size()];
        Enumeration _enum = vectorOfServiceHeader.elements();
        Hashtable tempHT = new Hashtable();

        int i = 0;

        while (_enum.hasMoreElements())
        {
            ServiceReference hdr = (ServiceReference) _enum.nextElement();
            float versionNum = hdr.getVersion();

            tempHT.put(new Float(versionNum), hdr);
            toSort[i++] = versionNum;
        }
        Arrays.sort(toSort);

        for (i = 0; i < toSort.length; i++)
        {
            ServiceReference header = (ServiceReference) tempHT.get(new Float(toSort[i]));

            toReturn.add(header);
        }
        return toReturn;
    }


    /**
     *  Description of the Method
     *
     * @param serviceGUID Description of the Parameter
     * @param version Description of the Parameter
     * @return Description of the Return Value
     * @throws FioranoException
     */
    private ServiceReference removeServiceVersion(String serviceGUID, String version) throws FioranoException {
        // check for the ServiceDescriptor.xml file and if its available
        // then get the list of resources of this service

        File serviceVersionHome;
        Service sps = null;
            serviceVersionHome = new File(getServicePath(serviceGUID,version));
            try {
                sps = getServicePropertySheet(serviceGUID,version);
            } catch (Exception e) {
                //ignore
            }

        try
        {
            serviceVersionHome.delete();

            //delete parent dir if empty
            if(serviceVersionHome.getParentFile().list().length==0)
                serviceVersionHome.getParentFile().delete();
        }
        catch (Exception fse)
        {
            throw new FioranoException("ERROR_DELETE_SERVICE",fse);
            //LogHelper.logErr(ILogModule.SERVICE_REPOSITORY, 24, serviceGUID, version); ---->log at higher level
            //throw new FioranoException("error removing service version : " + serviceGUID + ":" + version,fse);
        }

        try
        {
            // remove if an un-committed service
            m_nonCommittedServiceVsProperties.remove(getUniqueKey(serviceGUID,
                    version));
        }
        catch (FioranoException te)
        {
            //LogHelper.log(ILogModule.SERVICE_REPOSITORY, 25, te);
           // logger.error(Bundle.class,Bundle.INVALID_SPS_2,serviceGUID,version);
        }

        try
        {
            // remove if a committed service
            m_committedServiceVsProperties.remove(getUniqueKey(serviceGUID,
                    version));
        }
        catch (FioranoException te)
        {
            //LogHelper.log(ILogModule.SERVICE_REPOSITORY, 25, te);
           // logger.error(Bundle.class,Bundle.INVALID_SPS_2,serviceGUID,version);
        }

        return sps != null ? new ServiceReference(sps) : null;
    }

    /**
     *  Commits a service. Once a service is committed, it can be used by anyone
     *  who accesses the service provider
     *
     * @param serviceGUID The GUID of the service that needs to be
     *      committed
     * @param version The version of the service that needs to be
     *      committed
     * @param stage
     * @return ServiceHeader of the service that is
     *      committed
     * @exception FioranoException Exception
     */
    private ServiceReference commit(String serviceGUID, String version, String stage, String handleID)
            throws FioranoException
    {
        try {
            if (m_committedServiceVsProperties.get(getUniqueKey(serviceGUID, version)) != null)
                throw new FioranoException("ERROR_SERVICE_COMMIT_ERROR");
            //LogHelper.getErrMessage(ILogModule.SERVICE_REPOSITORY, 52, serviceGUID, version));

            if (m_nonCommittedServiceVsProperties.get(getUniqueKey(serviceGUID, version)) == null)
                throw new FioranoException("SERVICE_NOT_PRESENT");

            // modify the service's stage and rewrite the SPS
            if (!stage.equalsIgnoreCase(Deployment.DEVELOPMENT)
                    && stage.equalsIgnoreCase(Deployment.PRODUCT)
                    && stage.equalsIgnoreCase(Deployment.QA))
                throw new FioranoException("ERROR_SERVICE_SAVE_ERROR");
            //LogHelper.getErrMessage(ILogModule.SERVICE_REPOSITORY, 53, stage));

            String servicePath = getServicePath(serviceGUID, version);

            // do a resource check here. All the resources mentioned in the
            // ServiceDescriptor.xml file should be present in the COMPLETED
            // FILES FOLDER.

            try {
                checkServiceResources(serviceGUID, version);
                moveCompletedResourceFilesToRepository(serviceGUID, version);
                new File(servicePath + File.separator + TEMP_DOWNLOAD_DIR).delete();
            }
            catch (Exception e) {
                //if (TifTrace.ServiceRepository > 5)
                //    e.printStackTrace();

                throw new FioranoException("ERROR_SERVICE_SAVE_ERROR", e);
                //LogHelper.getErrMessage(ILogModule.SERVICE_REPOSITORY, 54, serviceGUID, version), e);
            }

            Service sps;

            try {
                sps = getServicePropertySheet(serviceGUID, version);
            }
            catch (Exception e) {
                //if (TifTrace.ServiceRepository > 5)
                //    LogHelper.log(ILogModule.SERVICE_REPOSITORY, e);
                throw new FioranoException("ERROR_SERVICE_DETAILS_FETCH_ERROR", e);
                //LogHelper.getErrMessage(ILogModule.SERVICE_REPOSITORY, 103, serviceGUID, version), e);
            }

            Deployment dep = sps.getDeployment();

            if (dep == null)
                dep = new Deployment();
            dep.setLabel(stage);
            sps.setDeployment(dep);

            // write back the SPS again after modifying the Deployment stage
            try {
                saveServiceDescriptor(sps);
            }
            catch (Exception e) {
                //if (TifTrace.ServiceRepository > 5)
                //    LogHelper.log(ILogModule.SERVICE_REPOSITORY, e);

                throw new FioranoException("ERROR_SERVICE_SAVE_ERROR", e);
                //LogHelper.getErrMessage(ILogModule.SERVICE_REPOSITORY, 56, serviceGUID, version), e);
            }

            // remove this from on committed services and add it to committed service
            m_nonCommittedServiceVsProperties.remove(getUniqueKey(serviceGUID, version));
            // add it to committed services
            m_committedServiceVsProperties.put(getUniqueKey(serviceGUID, version), sps);
            MicroServiceRepoEventRaiser.generateServiceRepositoryEvent(serviceGUID, version, null,
                    MicroServiceRepoUpdateEvent.SERVICE_REGISTERED, Event.EventCategory.INFORMATION, "");
            
            return new ServiceReference(sps);
        } catch (FioranoException e) {
            throw e;
        }
    }

    private void checkServiceResources(String serviceGUID, String version)
            throws FioranoException
    {
        Service sps = (Service) m_nonCommittedServiceVsProperties.get(getUniqueKey(serviceGUID, version));

        if (sps == null || sps.getDeployment() == null)
            return;
        Deployment deploymentInfo = sps.getDeployment();
        Iterator enumResources = deploymentInfo.getResources().iterator();

        while (enumResources.hasNext())
        {
            Resource resource = (Resource) enumResources.next();
            String resName = resource.getName();

            String completedResourceFolder = getCompletedResourceFolder(serviceGUID, version);
            String resPath = ComponentRepositoryUtil.resolve(completedResourceFolder, resName, favorites);
            try {
                File resFile = new File(resPath);
                if (!resFile.exists())
                    throw new FioranoException("SERVICE_CORRUPTED");
                //LogHelper.getErrMessage(ILogModule.SERVICE_REPOSITORY, 58, resName));
            } catch (Exception e) {
                throw new FioranoException("ERROR_READING_RESOURCE_FILE",e);
            }
        }

        // check the icon file now
        String iconFileName = sps.getIcon16();
        if (iconFileName == null)
            return;

        File icon = null;
        try {
            icon = getIcon(serviceGUID, version, iconFileName); //first check in normal path
            if (!icon.exists())        //then check in completed folder
            {
                String completedResourceFolder = getCompletedResourceFolder(serviceGUID, version);
                String resPath = ComponentRepositoryUtil.resolve(completedResourceFolder, iconFileName, favorites);

                icon = new File(resPath);

                if (!icon.exists())
                    throw new FioranoException("SERVICE_CORRUPTED");
                //LogHelper.getErrMessage(ILogModule.SERVICE_REPOSITORY, 59));
            }
        } catch (Exception e) {
            throw new FioranoException("ERROR_READING_ICON",e);
        }

        //todo - why icon32 is not being checked?


//        String completedResourceFolder = getCompletedResourceFolder(serviceGUID, version);
//        String resPath = ComponentRepositoryUtil.resolve(completedResourceFolder, iconFileName, favorites);
//
//
//        try {
//            File icon = new File(resPath);
//
//            if (!icon.exists())
//                throw new FioranoException(SPErrorCodes.SERVICE_CORRUPTED, LogHelper.getErrMessage(ILogModule.SERVICE_REPOSITORY, 59));
//        } catch (FileSystemException e) {
//            throw new FioranoException("error reading icon",e);
//        }
    }




    private void generateMicroServiceRepoUpdateEvent(String serviceGUID,
                                                        String serviceVersion,
                                                        String serviceStatus) throws FioranoException {
        generateMicroServiceRepoUpdateEvent(serviceGUID, serviceVersion, null, serviceStatus);
    }

    private void generateMicroServiceRepoUpdateEvent(String serviceGUID,
                                                        String serviceVersion,
                                                        String resourceName,
                                                        String serviceStatus) throws FioranoException{
        //Update Meta-INF Whenever the service is updated
        boolean toDelete = serviceStatus.equalsIgnoreCase(MicroServiceRepoUpdateEvent.SERVICE_REMOVED);

        //bug 4193 fix
        MicroServiceRepoUpdateEvent event = new MicroServiceRepoUpdateEvent();

        event.setServiceGUID(serviceGUID);
        event.setServiceVersion(serviceVersion);
        event.setResourceName(resourceName);
        event.setServiceStatus(serviceStatus);
        event.setEventCategory(Event.EventCategory.INFORMATION);
        event.setEventDescription("Updating service");
        event.setEventGenerationDate(System.currentTimeMillis());
        event.setEventStatus(serviceStatus);

        m_eventsManager.onEvent(event);
    }

    private File getFile(String serviceGUID, String version, String resource) throws IOException {
        if (version == null || version.trim().equals(""))
            version = getHighestVersionOfService(serviceGUID);

        String servicePath = getServicePath(serviceGUID ,version);

        String resourceFile = servicePath + File.separator + resource;
        return new File(resourceFile);
    }

    /*
    * Fetch the size of the resource
    */
    public long getSize(String serviceGUID, String version, String resource) throws FioranoException {
        try {
            File file = getFile(serviceGUID, version, resource);
            if(file.exists())
                return file.length();
            else return -1;
        } catch (IOException e) {
//            logger.error(Bundle.class,Bundle.ERROR_GET_RES_SIZE,resource,serviceGUID,version);         //logging in FESStubManager
            throw new FioranoException("ERROR_READING_FILE_SIZE",e);
        }
    }

    /*
    * Fetch the Last modified date on the resource
    */
    public long getLastModified(String serviceGUID, String version, String resource) throws FioranoException {
        try {
            File file = getFile(serviceGUID, version, resource);
            if(file.exists())
                return file.lastModified();
            else return -1;
        } catch (IOException e) {
//            logger.error(Bundle.class,Bundle.ERROR_GET_LAST_MODF_DATE,resource,serviceGUID,version,ExceptionUtil.getMessage(e));
            throw new FioranoException("ERROR_GET_LAST_MODF_DATE", e) ;
        }
    }

    /*
    * Returns all the Service GUIDs as a enumeration
    */
    public Enumeration getAllServiceGUIDs() throws FioranoException{
        return getGUIDS(m_committedServiceVsProperties);
    }

    public int getServicesCount() {
        int count = 0;
        count += m_committedServiceVsProperties.size();
        count += m_nonCommittedServiceVsProperties.size();
        return count;
    }

    /*
    * Returns all the Service GUIDs as a enumeration
    */
    public Enumeration getAllUnCommittedServiceGUIDs() throws FioranoException{
        return getGUIDS(m_nonCommittedServiceVsProperties);
    }

    /*******************************************************************************************/
    //@END Public interface
    /*******************************************************************************************/






    /*******************************************************************************************/
    //@START Private Helper Methods
    /*******************************************************************************************/

    //-----------------------------------------------------------------------------------
    //  Util functions for removing hardcoded paths
    //  - Prasanth

    private String getCompletedResourceFolder(String serviceGUID, String version)
    {
        return getServicePath(serviceGUID, version)
                + File.separator + TEMP_DOWNLOAD_DIR
                + File.separator + MicroServiceConstants.COMPLETE_RESOURCE_LOCATION;
    }

    private String getPartialResourceFolder(String serviceGUID, String version)
    {
        return getServicePath(serviceGUID, version)
                + File.separator + TEMP_DOWNLOAD_DIR
                + File.separator + MicroServiceConstants.PARTIAL_RESOURCE_LOCATION;
    }

    private File getServiceDescriptor(String serviceGUID, String version) {
        String path = getServicePath(serviceGUID, version)
                + File.separator + MicroServiceConstants.SERVICE_DESCRIPTOR_FILE_NAME;
        return new File(path);
    }

    private String getResourcePath(String serviceGUID, String version, String resName)
            throws FioranoException
    {
        String resFilePath = "";
        String servicePath = getServicePath(serviceGUID, version);

        boolean bFind = true;

        //If the service is uncommited, see if we can get the resource from the completed folder
        if (m_nonCommittedServiceVsProperties.containsKey(getUniqueKey(serviceGUID, version)))
        {
            //find the resource in the completed folder
            resFilePath = getCompletedResourceFolder(serviceGUID, version) + File.separator + resName;

            try {
                File f = new File(resFilePath);
                if(f.exists())
                    bFind = false;
            } catch (Exception e) {
                throw new FioranoException("FILE_SYSTEM_ERROR_RESOLVING_RESOURCE_PATH",e);
            }
        }

        //  If the service is commited OR
        //  If it is uncommited but the resource is not modified (ie not present in the completed folder)
        //  get the resource from the default location
        if(bFind)
        {
            resFilePath = ComponentRepositoryUtil.resolve(servicePath, resName, favorites);
        }
        return resFilePath;
    }

    private Service getServicePropertySheet(String serviceGUID, String version)
            throws FioranoException,IOException
    {
        File servDescFile = getServiceDescriptor(serviceGUID,version);
        if (!servDescFile.exists())
        {
            //Service descriptor not found for this version.
            //LogHelper.logErr(ILogModule.SERVICE_REPOSITORY, 3, serviceGUID, version);
            //logger.error(Bundle.class,Bundle.SERVICE_DESCRIPTOR_NOT_PRESENT,serviceGUID,version);
            return null;
        }
        Service service = null;
        try {
            service = ServiceParser.readService(servDescFile);
        } catch (FioranoException e) {
            throw new FioranoException("ERROR_PARSING_SERVICE_DESCRIPTOR",e);
        }
        return service;
    }

    private File fetchResourceFile(String serviceGUID, String version, String resName)
            throws FioranoException
    {
            File resFile;

            String resFilePath = getResourcePath(serviceGUID,version,resName);
            resFile = new File(resFilePath);
            if(!resFile.exists())
            {
                throw new FioranoException("RESOURCE_FILE_NOTPRESENT");
                //LogHelper.getErrMessage(ILogModule.SERVICE_REPOSITORY, 10,resName, serviceGUID,version));
            }
            return resFile;
    }

    private String getServicePath(String serviceGUID, String version)
    {
        return COMPONENTS_REPOSITORY_FOLDER + File.separator + serviceGUID + File.separator + version;
    }

    private String getServiceRootPath(String serviceGUID)
    {
        return COMPONENTS_REPOSITORY_FOLDER + File.separator + serviceGUID;
    }


    private File getIcon(String serviceGUID, String version, String icon) {
        String path =  getServicePath(serviceGUID,version) + File.separator + icon;
        return new File(path);
    }

    private Enumeration getAllServiceVersions(String serviceGUID)
            throws FioranoException
    {
            if(serviceGUID==null)
                throw new FioranoException("ERROR_GETTING_SERVICE_VERSION");

            File serviceRoot = new File(getServiceRootPath(serviceGUID));
            File[] allVersions = serviceRoot.listFiles();


            Vector guids = new Vector();

            for (int i = 0; allVersions != null && i < allVersions.length; ++i)
            {
                if (allVersions[i].exists())
                {
                    guids.add(allVersions[i].getName());
                }
            }

            return guids.elements();
    }

    private void saveServiceDescriptor(Service sps) throws FioranoException {
        try {
            //Change the "lastModified" time for the service since we are updating the service
            sps.setLastModifiedDate(new Date());
            File fo = getServiceDescriptor(sps.getGUID(), ""+sps.getVersion());
            ServiceParser.writeService(sps,fo.getParentFile());
        } catch (Exception e) {
            throw new FioranoException("FILE_SYSTEM_ERROR_WRITING_SERVICE_DESCRIPTOR", e);
        }
    }

    // - end of util functions
    //-----------------------------------------------------------------------------------------------

    private Enumeration getGUIDS(Hashtable table){
        TreeSet set = new TreeSet();
        Enumeration eenum = table.keys();
        while(eenum.hasMoreElements()){
            String str = (String)eenum.nextElement();
            set.add(str.substring(0, str.indexOf(";")));
        }
        return Collections.enumeration(set);
    }

    /**
     * Unique Id is GUID:version. Retrieve the GUID from the the key.
     * @param key
     * @return String
     */
    private String getGUIDfromUniqueKey(String key)
    {
        return key.substring(0, key.indexOf(";"));
    }

    private boolean isPresentInNewDeployment(Resource oldresource, Deployment newDeployment)
    {
        // Comparing resources in this manner as number of resources would be a small number.
        Iterator resources = newDeployment.getResources().iterator();
        while(resources.hasNext())
        {
            Resource res = (Resource) resources.next();
            if(res.getName().equalsIgnoreCase(oldresource.getName()))
                return true;
        }
        return false;

    }
}

