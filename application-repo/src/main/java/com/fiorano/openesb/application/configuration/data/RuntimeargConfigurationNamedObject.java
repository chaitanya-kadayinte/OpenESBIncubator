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

package com.fiorano.openesb.application.configuration.data;

import com.fiorano.openesb.application.DmiObjectTypes;
import com.fiorano.openesb.utils.exception.FioranoException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * This class is used to create named objects of runtime arguments configuration
 * @author FSTPL
 * @version 10
 *
 */
public class RuntimeargConfigurationNamedObject extends NamedObject {
    private String serviceGUID;
    private String serviceVersion;

    /**
     * Default constructor
     */
    public RuntimeargConfigurationNamedObject() {}

    /**
     * Constructor to initialize values for objects of this class
     * @param name Name
     * @param label Label
     * @param objectCategory Object category
     * @param serviceGUID Service GUID
     * @param serviceVersion Service version
     */
    public RuntimeargConfigurationNamedObject(String name, Label label, ObjectCategory objectCategory, String serviceGUID, String serviceVersion) {
        super(name, label, objectCategory);
        this.serviceGUID = serviceGUID;
        this.serviceVersion = serviceVersion;
    }

    /**
     * Sets the service GUID
     * @param serviceGUID Service GUID
     */
    public void setServiceGUID(String serviceGUID) {
        this.serviceGUID = serviceGUID;
    }

    /**
     * Sets the service version
     * @param serviceVersion Version
     */
    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    /**
     * Returns the Service GUID
     * @return String - Service GUID
     */
    public String getServiceGUID() {
        return serviceGUID;
    }

    /**
     * Returns the version for this service
     * @return String - Version
     */
    public String getServiceVersion() {
        return serviceVersion;
    }

    /**
     * Returns the object category of the objects of this class
     * @return ObjectCategory - Object category
     */
    public ObjectCategory getObjectCategory() {
        return ObjectCategory.RUNTIME_ARG_CONFIGURATION;
    }

    /**
     * Sets the object category
     * @param objectCategory Object category
     * @throws UnsupportedOperationException Exception thrown when an unsupported operation is called
     */
    public void setObjectCategory(ObjectCategory objectCategory) throws UnsupportedOperationException {
        if(objectCategory == null || !objectCategory.equals(ObjectCategory.RUNTIME_ARG_CONFIGURATION))
            throw new UnsupportedOperationException("OBJECT_CATEGORY_READ_ONLY");
    }

    /**
     * Returns ID of this object.
     *
     * @return ID of this object.
     * @since Tifosi2.0
     */
    public int getObjectID() {
        return DmiObjectTypes.RUNTIMEARG_CONFIGURATION_NAMED_OBJECT;
    }

    /**
     * Resets the values of the data members of the object. This
     * may possibly be used to help the Dmifactory reuse Dmi objects.
     *
     * @since Tifosi2.0
     */
    public void reset() {
        super.reset();
        serviceGUID = null;
        serviceVersion = null;
    }

    /**
     * Tests whether this <code>DmiObject</code> object has the
     * required(mandatory) fields set. This method must be called before
     * inserting values in the database.
     *
     * @throws com.fiorano.openesb.utils.exception.FioranoException If the object is not valid
     * @since Tifosi2.0
     */
    public void validate() throws FioranoException {
        super.validate();
    }

    /**
     * Writes this object to specified output stream <code>out</code>
     *
     * @param out       Output stream
     * @param versionNo Version
     * @throws IOException If an error occurs while writing to stream
     */
    public void toStream(DataOutput out, int versionNo) throws IOException {
        super.toStream(out, versionNo);
        writeUTF(out, serviceGUID);
        writeUTF(out, serviceVersion);
    }

    /**
     * Reads this object from specified stream <code>is</code>
     *
     * @param is        Input stream
     * @param versionNo Version
     * @throws IOException If an error occurs while reading from stream
     */
    public void fromStream(DataInput is, int versionNo) throws IOException {
        super.fromStream(is, versionNo);
        serviceGUID = readUTF(is);
        serviceVersion = readUTF(is);
    }
}
