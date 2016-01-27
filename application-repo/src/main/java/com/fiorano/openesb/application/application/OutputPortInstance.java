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
import com.fiorano.openesb.application.aps.OutPortInst;
import com.fiorano.openesb.application.aps.PortInst;
import com.fiorano.openesb.utils.exception.FioranoException;
import com.fiorano.openesb.utils.FioranoStaxParser;
import org.apache.commons.lang3.StringUtils;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * This class is a DMI representation of output port of a service instance .It contains methods to access information
 * about the output port of a service such as port name and DTD/XSD for the output port.
 *
 * @author  FSTPL
 * @version  10
 */
public class OutputPortInstance extends PortInstance{
    /**
     * element outputport-instance in event process xml
     */
    public static final String ELEM_OUTPUT_PORT_INSTANCE = "outputport-instance";

    public int getObjectID(){
        return DmiObjectTypes.NEW_OUTPUT_PORT_INSTANCE;
    }

    /*-------------------------------------------------[ timeToLive ]---------------------------------------------------*/
    /**
     * element publisher in event process xml
     */
    public static final String ELEM_PUBLISHER = "publisher";
    /**
     * element messages in event process xml
     */
    public static final String ELEM_MESSAGES = "messages";
    /**
     * element time-to-live in event process xml
     */
    public static final String ATTR_TIME_TO_LIVE = "time-to-live";

    private long timeToLive = 0;

    /**
     * Returns time limit after which the received message is lost. 0 millisecond means infinite.
     * @return long Time limit
     */
    public long getTimeToLive(){
        return timeToLive;
    }

    /**
     * Sets time limit for messages received at this output port
     * @param timeToLive time limit to be set
     */
    public void setTimeToLive(long timeToLive){
        this.timeToLive = timeToLive;
    }

    /*-------------------------------------------------[ CompressMessages ]---------------------------------------------------*/
    /**
     * attribute compress
     */
    public static final String ATTR_COMPRESS_MESSAGES = "compress";

    private boolean compressMessages = false;

    /**
     * Specifies whether compression is enabled for messages received at this output port
     * @return boolean true if compression is enabled
     * @deprecated
     */
    public boolean isCompressMessages(){
        return compressMessages;
    }

    /**
     * Sets a boolean specifying whether messages received at this output port should be compressed
     * @param compressMessages boolean specifying whether messages received at this output port should be compressed
     */
    public void setCompressMessages(boolean compressMessages){
        this.compressMessages = compressMessages;
    }

    /*-------------------------------------------------[ Priority ]---------------------------------------------------*/
    /**
     * attribute priority
     */
    public static final String ATTR_PRIORITY = "priority";

    private int priority = 4;

    /**
     * Returns JMS message priority
     * @return int message priority
     */
    public int getPriority(){
        return priority;
    }

    /**
     * Sets JMS message priority
     * @param priority priority to be set
     */
    public void setPriority(int priority){
        this.priority = priority;
    }

    /*-------------------------------------------------[ persistent  ]---------------------------------------------------*/
    /**
     * attribute persistent
     */
    public static final String ATTR_PERSISTENT = "persistent";

    private boolean persistent  = true;

    /**
     * Returns whether the messages from this outport are persistent
     * @return persistence of the message sent from the port
     */
    public boolean isPersistent(){
        return persistent;
    }

    /**
     * Sets a boolean specifying persistence of messages at this output port
     * @param persistent boolean
     */
    public void setPersistent(boolean persistent){
        this.persistent = persistent;
    }

    /*-------------------------------------------------[ publisher-config-name ]---------------------------------------------------*/
    /**
     * element publisher-config-name in event process xml
     */
    public static final String ELEM_PUBLISHER_CONFIG_NAME = "publisher-config-name";

    private String publisherConfigName;

    /**
     * Returns configuration name of this publisher instance
     * @return Configuration name of this publisher instance
     */
    public String getPublisherConfigName(){
        return publisherConfigName;
    }

    /**
     * Sets configuration name of this publisher instance
     * @param publisherConfigName configuration name
     */
    public void setPublisherConfigName(String publisherConfigName){
        this.publisherConfigName = publisherConfigName;
    }

    /*-------------------------------------------------[ ApplicationContextTransformation ]---------------------------------------------------*/

    private Transformation applicationContextTransformation;

    /**
     * Gets ApplicationContextTransformation
     * @return Transformation of Application Context
     */
    public Transformation getApplicationContextTransformation(){
        return applicationContextTransformation;
    }

    /**
     * Sets Application Context transformation
     * @param applicationContextTransformation Transformation of Application context
     */
    public void setApplicationContextTransformation(Transformation applicationContextTransformation){
        this.applicationContextTransformation = applicationContextTransformation;
    }
    

    /*-------------------------------------------------[ To XML ]---------------------------------------------------*/

    /*
     * <outputport-instance>
     *      ...super-class...
     *      <jms>
     *          ...super-class...
     *          <publisher>
     *              <messages time-to-leave="int"? compress="boolean"? priority="int"?/>?
     *          </publisher>
     *      </jms>
     *      ...super-class...
     *      ...message-transformation?...
     * </outputport-instance>
     */

    protected void toJXMLString(XMLStreamWriter writer, boolean writeSchema) throws XMLStreamException, FioranoException{
        toJXMLString(writer, ELEM_OUTPUT_PORT_INSTANCE, writeSchema);
    }

    protected void toJXMLString(XMLStreamWriter writer) throws XMLStreamException, FioranoException{
        toJXMLString(writer, true);
    }

    protected void toJXMLString_2(XMLStreamWriter writer, boolean writeSchema) throws XMLStreamException, FioranoException{
        if (writeSchema || publisherConfigName == null) {             // We need to write port properties to stream when passing application launch packet to peer         //
            writer.writeStartElement(ELEM_PUBLISHER);
            {
                writer.writeStartElement(ELEM_MESSAGES);
                {
                    if(timeToLive!=0)
                        writer.writeAttribute(ATTR_TIME_TO_LIVE, String.valueOf(timeToLive));
                    if(compressMessages)
                        writer.writeAttribute(ATTR_COMPRESS_MESSAGES, String.valueOf(compressMessages));
                    if(priority!=4)
                        writer.writeAttribute(ATTR_PRIORITY, String.valueOf(priority));
                    if(persistent)
                        writer.writeAttribute(ATTR_PERSISTENT, String.valueOf(persistent));
                }
                writer.writeEndElement();
            }
            writer.writeEndElement();
        } else {
            writer.writeStartElement(ELEM_PUBLISHER_CONFIG_NAME);
            {
                writer.writeAttribute(ATTR_NAME, publisherConfigName);
            }
            writer.writeEndElement();
        }
    }

    protected void toJXMLString_3(XMLStreamWriter writer, boolean writeSchema) throws XMLStreamException, FioranoException{
        if(applicationContextTransformation!=null)
            applicationContextTransformation.toJXMLString(writer, Transformation.ELEM_TRANSFORMATION, writeSchema);
    }

    /*-------------------------------------------------[ From XML ]---------------------------------------------------*/

    protected void populate(FioranoStaxParser cursor) throws XMLStreamException, FioranoException{
        populate(cursor, ELEM_OUTPUT_PORT_INSTANCE);
    }

    protected void populate_2(FioranoStaxParser cursor) throws XMLStreamException, FioranoException{
        String elemName = cursor.getLocalName();
        if(ELEM_PUBLISHER.equals(elemName)){
            populatePublisherConfiguration(cursor);
        }else if(ELEM_PUBLISHER_CONFIG_NAME.equals(elemName)){
            publisherConfigName = cursor.getAttributeValue(null, ATTR_NAME);
        }
    }

    /**
     * Sets Publisher configuration
     *
     * @param cursor  FioranStaxParser for parsing
     * @throws XMLStreamException
     * @throws FioranoException
     */
    public void populatePublisherConfiguration(FioranoStaxParser cursor) throws XMLStreamException, FioranoException {
        if(cursor.markCursor(ELEM_PUBLISHER)){
            while(cursor.nextElement()){
                if(ELEM_MESSAGES.equals(cursor.getLocalName())){
                    timeToLive = getLongAttribute(cursor, ATTR_TIME_TO_LIVE, 0);
                    compressMessages = getBooleanAttribute(cursor, ATTR_COMPRESS_MESSAGES, false);
                    priority = getIntegerAttribute(cursor, ATTR_PRIORITY, 4);
                    persistent = getBooleanAttribute(cursor, ATTR_PERSISTENT, false);
                }
            }

            cursor.resetCursor();
        }
    }

    protected void populate_3(FioranoStaxParser cursor) throws XMLStreamException, FioranoException{
        if(Transformation.ELEM_TRANSFORMATION.equals(cursor.getLocalName())){
            applicationContextTransformation = new Transformation();
            applicationContextTransformation.setFieldValues(cursor);
        }
    }


    /*-------------------------------------------------[ Migration ]---------------------------------------------------*/
    /**
     * Converts to new DMI structure
     * @param that old PortInstance DMI
     */
    public void convert_2(PortInst that){
        timeToLive = that.getMessageTTL();
        compressMessages = that.isCompressionEnabled();
        priority = that.getMessagePriority();
    }

    public void convert_3(OutPortInst that){
        if(!StringUtils.isEmpty(that.getContextXSL())){
            applicationContextTransformation = new Transformation();
            applicationContextTransformation.convert(that);
        }
    }


    /*-------------------------------------------------[ Other Methods ]---------------------------------------------------*/

    public void reset(){
        super.reset();

        timeToLive = 0;
        compressMessages = false;
        priority = 4;
        applicationContextTransformation = null;
        publisherConfigName = null;
    }
}
