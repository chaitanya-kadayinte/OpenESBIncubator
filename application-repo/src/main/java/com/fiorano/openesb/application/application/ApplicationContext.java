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
package com.fiorano.openesb.application.application;

import com.fiorano.openesb.application.DmiObjectTypes;
import com.fiorano.openesb.application.MapThreadLocale;
import com.fiorano.openesb.utils.exception.FioranoException;
import com.fiorano.openesb.utils.FioranoStaxParser;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * This class is a DMI representation of application context of an event process.
 * @author FSTPL
 * @version 10
 */
public class ApplicationContext extends SchemaInstance{
    /**
     * element application-context in event process xml
     */
    public static final String ELEM_APPLICATION_CONTEXT = "application-context";

    /**
     * Returns ID of this object. This is used internally to identify different types of DMI objects.
     * @return the id of this object.
     */
    public int getObjectID(){
        return DmiObjectTypes.NEW_APPLICATION_CONTEXT;
    }

    /*-------------------------------------------------[ XMLIzation ]---------------------------------------------------*/

    /*
     * <application-context>
     *      ...super-class...
     * </application-context>
     */

    protected void toJXMLString(XMLStreamWriter writer, boolean writeSchema) throws XMLStreamException, FioranoException{
        MapThreadLocale.getInstance().getMap().put(ServiceInstance.ELEM_SERVICE_INSTANCE, "APPLICATION_CONTEXT");
        toJXMLString(writer, ELEM_APPLICATION_CONTEXT, writeSchema);
    }


    protected void toJXMLString(XMLStreamWriter writer) throws XMLStreamException, FioranoException {
        toJXMLString(writer, true);
    }

    protected void populate(FioranoStaxParser cursor) throws XMLStreamException, FioranoException{
        MapThreadLocale.getInstance().getMap().put(ServiceInstance.ELEM_SERVICE_INSTANCE, "APPLICATION_CONTEXT");
        populate(cursor, ELEM_APPLICATION_CONTEXT);
    }
}
