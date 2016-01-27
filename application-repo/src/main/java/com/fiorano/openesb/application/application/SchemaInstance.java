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
import com.fiorano.openesb.utils.exception.FioranoException;
import com.fiorano.openesb.application.service.Schema;
import com.fiorano.openesb.utils.FioranoStaxParser;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * This class extends DMI representation of Schema by defining its value
 * @author FSTPL
 * @version 10
 */
public class SchemaInstance extends Schema{
    /**
     * Element schema-instance in event process xml
     */
    public static final String ELEM_SCHEMA_INSTANCE = "schema-instance";

    public int getObjectID(){
        return DmiObjectTypes.NEW_SCHEMA_INSTANCE;
    }

    /*-------------------------------------------------[ value ]---------------------------------------------------*/
    /**
     * Element value in event process xml
     */
    public static final String ELEM_VALUE = "value";

    private String value;

    /**
     * Returns value of schema instance
     * @return String - value of schema instance
     */
    public String getValue(){
        return value;
    }

    /**
     * Sets value of this schema instance
     * @param value Value to be set
     */
    public void setValue(String value){
        this.value = value;
    }

    /*-------------------------------------------------[ To XML ]---------------------------------------------------*/

    /*
     * <schema-instance>
     *      ...super-class...
     *      <value>string</value>?
     * </schema-instance>
     */

    protected void toJXMLString(XMLStreamWriter writer, boolean writeSchema) throws XMLStreamException, FioranoException{
        toJXMLString(writer, ELEM_SCHEMA_INSTANCE, writeSchema);
    }


    protected void toJXMLString(XMLStreamWriter writer) throws XMLStreamException, FioranoException {
        toJXMLString(writer, true);
    }

    protected void toJXMLString_1(XMLStreamWriter writer) throws XMLStreamException, FioranoException{
        writeElement(writer, ELEM_VALUE, value);
    }

    /*-------------------------------------------------[ From XML ]---------------------------------------------------*/

    protected void populate(FioranoStaxParser cursor) throws XMLStreamException,FioranoException{
        value = null;
        populate(cursor, ELEM_SCHEMA_INSTANCE);
    }

    // subclasses can add additional content using this
    protected void populate_1(FioranoStaxParser cursor) throws XMLStreamException, FioranoException{
        if(ELEM_VALUE.equals(cursor.getLocalName()))
            value = cursor.getTextContent();
    }

    /*-------------------------------------------------[ Migration ]---------------------------------------------------*/
    /**
     * Converts to new DMI
     * @param that old DMI
     */
    public void convert_1(com.fiorano.openesb.application.aps.ApplicationContext that){
        value = that.getDefaultInstance();
    }

    /*-------------------------------------------------[ Other Methods ]---------------------------------------------------*/

    public void reset(){
        super.reset();
        value = null;
    }
}
