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

package com.fiorano.openesb.application.aps;

import com.fiorano.openesb.application.*;
import com.fiorano.openesb.utils.exception.FioranoException;
import com.fiorano.openesb.utils.FioranoStaxParser;
import com.fiorano.openesb.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Hashtable;

/**
 *  Represents monitored modules for event that can be defined in a service. The
 *  modules contain the name, trace level and option to generate an alert for
 *  the event. Modules are basically defined with the purpose of monitoring a
 *  set of activities which are defined as the module itself. Object of <code> Monitor</code>
 *  stores all the modules of a service in a vector.
 *
 * @author Administrator
 * @created March 8, 2002
 * @version 2.0
 * @see com.fiorano.openesb.application.DmiObject
 * @see com.fiorano.openesb.application.aps.Monitor
 * @see com.fiorano.openesb.application.aps.ApplicationPropertySheet
 * @since Tifosi2.0
 */
public class ApsEventModule extends DmiObject
{
    String          m_strModuleName;
    int             m_iTraceLevel;
    boolean         m_bIsAlert;

    /**
     *  This is called to construct object of <code>ApsEventModule</code>
     *
     * @since Tifosi2.0
     */
    public ApsEventModule()
    {
    }

    /**
     *  This method gets the name of service module represented by this object
     *  of <code>ApsEventModule</code>.
     *
     * @return The name of this module
     * @see #setName(String)
     * @since Tifosi2.0
     */
    public String getName()
    {
        return m_strModuleName;
    }

    /**
     *  This method gets the tracelevel for the service module represented by
     *  this object of <code>ApsEventModule</code>
     *
     * @return The traceLevel value
     * @see #setTraceLevel(int)
     * @since Tifosi2.0
     */
    public int getTraceLevel()
    {
        return m_iTraceLevel;
    }

    /**
     *  This method finds whether or not the alert gets generated for this event
     *  module.
     *
     * @return true, if it generates alert, false otherwise.
     * @see #setGenerateAlert(boolean isAlert)
     * @since Tifosi2.0
     */

    public boolean isGenerateAlert()
    {
        return m_bIsAlert;
    }

    /**
     *  This API returns the id of this object.
     *
     * @return the id of this object.
     * @since Tifosi2.0
     */
    public int getObjectID()
    {
        return DmiObjectTypes.APS_EVENT_MODULE;
    }

    /**
     *  This method sets the specified string as name of service module,
     *  represented by this object of <code>ApsEventModule</code>.
     *
     * @param moduleName The string to be set as name of this module
     * @see #getName()
     * @since Tifosi2.0
     */
    public void setName(String moduleName)
    {
        m_strModuleName = moduleName;
    }

    /**
     *  This method sets the specified integer as tracelevel for the service
     *  module, represented by this object of <code>ApsEventModule</code>
     *
     * @param traceLevel integer to be set as traceLevel for this module.
     * @see #getTraceLevel()
     * @since Tifosi2.0
     */
    public void setTraceLevel(int traceLevel)
    {
        m_iTraceLevel = traceLevel;
    }

    /**
     *  This method sets whether or not this event module generates the alert.
     *
     * @param isAlert boolean if set to true, generates the alert.
     * @see #isGenerateAlert()
     * @since Tifosi2.0
     */

    public void setGenerateAlert(boolean isAlert)
    {
        m_bIsAlert = isAlert;
    }


    /**
     *  This method sets all the fieldValues of this object of <code>ApsEventModule</code>
     *  , using the specified XML string.
     *
     * @param moduleElement The new fieldValues value
     * @exception FioranoException if an error occurs while parsing the
     *      XMLString
     * @since Tifosi2.0
     */
    public void setFieldValues(Element moduleElement)
        throws FioranoException
    {
//        Document doc = XMLUtils.getDOMDocumentFromXML(xmlString);
//        Element moduleElement = doc.getDocumentElement();
        m_iTraceLevel = Integer.parseInt(moduleElement.getAttribute("traceLevel"));
        m_strModuleName = moduleElement.getAttribute("name");
        m_bIsAlert = new Boolean(moduleElement.getAttribute("generateAlert")).booleanValue();
        validate();
    }

    protected void populate(FioranoStaxParser parser)throws XMLStreamException, FioranoException
    {

        if ( parser.markCursor(APSConstants.MODULE) )
        {
            Hashtable attributes = parser.getAttributes();
            m_iTraceLevel = Integer.parseInt((String)attributes.get(APSConstants.TRACE_LEVEL));//moduleElement.getAttribute("traceLevel"));
            m_strModuleName = (String)attributes.get(APSConstants.MODULE_NAME);//moduleElement.getAttribute("name");
            m_bIsAlert = XMLUtils.getStringAsBoolean((String)attributes.get(APSConstants.GENERATE_ALERT));//new Boolean(moduleElement.getAttribute("generateAlert")).booleanValue();
            {
                attributes.clear();
                attributes = null;
            }
            validate();

        }
    }

    /**
     *  Resets the values of the data members of this object. Not supported in
     *  this version.
     *
     * @since Tifosi2.0
     */
    public void reset()
    {
    }

    /**
     *  This method is called to write this object of <code>ApsEventModule</code>
     *  to the specified output stream object.
     *
     * @param out DataOutput object
     * @param clientVersionNo Description of the Parameter
     * @exception IOException if an error occurs while converting data and
     *      writing it to a binary stream.

     * @since Tifosi2.0
     */
    public void toStream(DataOutput out, int clientVersionNo)
        throws IOException
    {
        super.toStream(out, clientVersionNo);
        writeUTF(out, m_strModuleName);
        out.writeInt(m_iTraceLevel);
        out.writeBoolean(m_bIsAlert);
    }

    /**
     *  This is called to read this object <code>ApsEventModule</code> from the
     *  specified object of input stream.
     *
     * @param is DataInput object
     * @param clientVersionNo Description of the Parameter
     * @exception IOException if an error occurs while reading bytes or while
     *      converting them into specified Java primitive type.

     * @since Tifosi2.0
     */
    public void fromStream(DataInput is, int clientVersionNo)
        throws IOException
    {
        super.fromStream(is, clientVersionNo);
        m_strModuleName = readUTF(is);
        m_iTraceLevel = is.readInt();
        m_bIsAlert = is.readBoolean();
    }


    /**
     *  This method tests whether this object of <code>ApsEventModule</code> has
     *  the required(mandatory) fields set, before inserting values in the
     *  database.
     *
     * @exception FioranoException if the object is not valid
     * @since Tifosi2.0
     */
    public void validate()
        throws FioranoException
    {
        if (m_strModuleName == null || m_strModuleName.equalsIgnoreCase(""))
            throw new FioranoException(DmiErrorCodes.ERR_INVALID_ARGUMENT_ERROR);
    }

    /**
     *  This utility method is used to get the String representation of this
     *  object of <code>ApsEventModule</code>.
     *
     * @return The String representation of this object.
     * @since Tifosi2.0
     */
    public String toString()
    {
        String baseString = super.toString();
        StringBuffer strBuf = new StringBuffer();

        strBuf.append(baseString);
        strBuf.append("");
        strBuf.append("ApsEventModule Details ");
        strBuf.append("[");
        strBuf.append("Name = ");
        strBuf.append(m_strModuleName);
        strBuf.append(", ");
        strBuf.append("GenerateAlert = ");
        strBuf.append(m_bIsAlert);
        strBuf.append(", ");
        strBuf.append("Trace Level = ");
        strBuf.append(String.valueOf(m_iTraceLevel));
        strBuf.append("]");
        return strBuf.toString();
    }

    /**
     *  Returns the xml string equivalent of this object
     *
     * @param document the input Document object
     * @return element node
     * @exception FioranoException if an error occurs while creating the element
     *      node.
     */
    Node toJXMLString(Document document)
        throws FioranoException
    {
        Node root0 = (Node) document.createElement("Module");

        ((Element) root0).setAttribute("name", m_strModuleName);
        ((Element) root0).setAttribute("traceLevel", String.valueOf(m_iTraceLevel));
        ((Element) root0).setAttribute("generateAlert", "" + m_bIsAlert);

        return root0;
    }

    public void toJXMLString(XMLStreamWriter writer) throws XMLStreamException
    {
        //Start Module
        writer.writeStartElement("Module");

        writer.writeAttribute("name", m_strModuleName);
        writer.writeAttribute("traceLevel", String.valueOf(m_iTraceLevel));
        writer.writeAttribute("generateAlert", "" + m_bIsAlert);

        //End Module
        writer.writeEndElement();

    }

}
