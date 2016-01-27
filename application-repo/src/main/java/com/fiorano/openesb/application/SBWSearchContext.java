/**
 * Copyright (c) 1999-2007, Fiorano Software Technologies Pvt. Ltd. and affiliates.
 * Copyright (c) 2008-2015, Fiorano Software Pte. Ltd. and affiliates.
 *
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Fiorano Software ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * enclosed with this product or entered into with Fiorano.
 */
package com.fiorano.openesb.application;

import com.fiorano.openesb.utils.exception.FioranoException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Prasanth
 * Date: Jul 13, 2007
 * Time: 3:28:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class SBWSearchContext extends DmiObject {

    /*---------------------------- peer name --------------------------------------*/

    private String peerName = "";

     public String getPeerName() {
        return peerName;
    }

    public void setPeerName(String peerName) {
        this.peerName = peerName;
    }

    /*---------------------------- Application GUID -------------------------------*/

    private String appGUID = "";

    public String getAppGUID() {
        return appGUID;
    }

    public void setAppGUID(String appGUID) {
        this.appGUID = appGUID;
    }

    /*---------------------------- Application Version -------------------------------*/

    private String appVersion = "";

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    /*---------------------------- Service Instance -------------------------------*/

    private String serviceInstance = "";

    public String getServiceInstance() {
        return serviceInstance;
    }

    public void setServiceInstance(String serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    /*---------------------------- Status -------------------------------*/

    private String status = "";

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /*---------------------------- Document ID -------------------------------*/

    private String documentID = "";

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    /*---------------------------- Work Flow Instance ID -------------------------------*/

    private String workFlowInstanceID = "";

    public String getWorkFlowInstanceID() {
        return workFlowInstanceID;
    }

    public void setWorkFlowInstanceID(String workFlowInstanceID) {
        this.workFlowInstanceID = workFlowInstanceID;
    }

    /*---------------------------- User Defined Document ID-------------------------------*/

    private String userDocID = "";

    public String getUserDocID() {
        return userDocID;
    }

    public void setUserDocID(String userDocID) {
        this.userDocID = userDocID;
    }

    /*---------------------------- Start Date -------------------------------------*/

    private Date startDate;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /*---------------------------- End Date ---------------------------------------*/

    private Date endDate;

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /*---------------------------- Port Name --------------------------------------*/

    private String portName = "";

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    /*---------------------------- Message Text --------------------------------------*/

    private String messageText = "";

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    /*---------------------------- Message Header --------------------------------------*/

    private String messageHeader;

    public String getMessageHeader() {
        return messageHeader;
    }

    public void setMessageHeader(String messageHeader) {
        this.messageHeader = messageHeader;
    }

    /*---------------------------- Message Attachment --------------------------------------*/

    private String messageAttachment;

    public String getMessageAttachment() {
        return messageAttachment;
    }

    public void setMessageAttachment(String messageAttachment) {
        this.messageAttachment = messageAttachment;
    }


    /*---------------------------- Application List for which user does not have view permissions--------------------------------------*/

    private ArrayList apps;

    public void setApps(ArrayList apps) {
        this.apps = apps;
    }

    public ArrayList getApps() {
        return apps;
    }

    /*--------------------------------------- Implementation of DMIObject --------------------------------------------*/

    /**
     * This method returns the ID of this object.
     *
     * @return the id of this object.
     * @since Tifosi2.0
     */
    public int getObjectID() {
        return DmiObjectTypes.SBW_SEARCH_CONTEXT;
    }

    /**
     * This method resets the values of the data members of the object. This
     * may possibly be used to help the Dmifactory reuse Dmi objects.
     *
     * @since Tifosi2.0
     */
    public void reset() {
        peerName = "";
        appGUID = "";
        appVersion = "";
        serviceInstance = "";
        status = "";
        documentID = "";
        workFlowInstanceID = "";
        userDocID = "";
        startDate = null;
        endDate = null;
        portName = "";
        messageText = "";
    }

    /**
     * This method tests whether this <code>DmiObject</code> object has the
     * required(mandatory) fields set. This method must be called before
     * inserting values in the database.
     *
     * @throws com.fiorano.openesb.utils.exception.FioranoException
     *          if the object is not valid
     * @since Tifosi2.0
     */
    public void validate() throws FioranoException {
        // All parameters are optional
    }



    /*------------------------------------------ Overrides of DMIObject ----------------------------------------------*/

    /**
     * This method writes this <code>SBWSearchContext</code> object to the
     * specified output stream object.
     *
     * @param out DataOutput object on which to write
     * @throws IOException if an error occurs while converting data and
     *                             writing it to a binary stream.
     * @see #fromStream(DataInput, int)
     * @since Tifosi2.0
     */
    public void toStream(DataOutput out, int versionNo)
            throws IOException {
        super.toStream(out, versionNo);

        writeUTF(out, peerName);
        writeUTF(out, appGUID);
        writeUTF(out, appVersion);
        writeUTF(out, serviceInstance);
        writeUTF(out, status);
        writeUTF(out, documentID);
        writeUTF(out, workFlowInstanceID);
        writeUTF(out, userDocID);
        writeUTF(out, portName);
        writeUTF(out, messageText);

        if (startDate != null) {
            out.writeInt(1);
            out.writeLong(startDate.getTime());
        } else {
            out.writeInt(0);
        }

        if (endDate != null) {
            out.writeInt(1);
            out.writeLong(endDate.getTime());
        } else {
            out.writeInt(0);
        }

    }

    /**
     * This method reads this <code>EventSearchContext</code> object from the
     * specified input stream object.
     *
     * @param is Specify the DataInput object
     * @throws IOException if error occurs while reading bytes or while
     *                     converting them into specified Java primitive type.
     * @see #toStream(DataOutput, int)
     * @since Tifosi2.0
     */
    public void fromStream(DataInput is, int versionNo)
            throws IOException {
        super.fromStream(is, versionNo);

        peerName = readUTF(is);
        appGUID = readUTF(is);
        appVersion = readUTF(is);
        serviceInstance = readUTF(is);
        status = readUTF(is);
        documentID = readUTF(is);
        workFlowInstanceID = readUTF(is);
        userDocID = readUTF(is);
        portName = readUTF(is);
        messageText = readUTF(is);

        int val = is.readInt();
        if (val == 1) {
            startDate = new Date(is.readLong());
        }
        val = is.readInt();
        if (val == 1) {
            endDate = new Date(is.readLong());
        }
    }
}
