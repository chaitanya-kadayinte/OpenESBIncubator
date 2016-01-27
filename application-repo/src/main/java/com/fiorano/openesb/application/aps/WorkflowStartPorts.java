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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Contains information on all input ports in a workflow as a vector of
 * <code>WfInPort</code> objects.
 *
 * @author Deepak Nohwal.
 * @created January 10, 2002
 * @version 1.0
 * @see com.fiorano.openesb.application.DmiObject
 * @see com.fiorano.openesb.application.aps.WfInPort
 * @see com.fiorano.openesb.application.aps.ApplicationPropertySheet
 * @since Tifosi2.0
 */

public class WorkflowStartPorts extends DmiObject
{
    Vector          m_inPorts = new Vector();


    /**
     *  This is the constructor of the <code>WorkflowStartPorts</code> class.
     *
     * @since Tifosi2.0
     */
    public WorkflowStartPorts()
    {
    }


    /**
     *  This method gets an enumeration of <code>WfInPort</code> from this object of
     *  <code>WorkflowStartPorts</code>.
     *
     * @return Enumeration of objects of WfInPort
     * @see #addInPort(WfInPort)
     * @since Tifosi2.0
     */
    public Enumeration getInPorts()
    {
        return m_inPorts.elements();
    }


    /**
     *  This method returns the ID of this object.
     *
     * @return the id of this object.
     * @since Tifosi2.0
     */
    public int getObjectID()
    {
        return DmiObjectTypes.WF_START_PORTS;
    }


    /**
     *  This method sets all the fieldValues of this object of
     *  <code>WorkflowStartPorts</code>, using the specified XML string.
     *
     * @param startElement
     * @exception FioranoException if an error occurs while parsing the
     *      XMLString
     * @since Tifosi2.0
     */
    public void setFieldValues(Element startElement)
        throws FioranoException
    {
//        Document doc = XMLUtils.getDOMDocumentFromXML(xmlString);
//        Element startElement = doc.getDocumentElement();

        if (startElement != null)
        {
            NodeList list = startElement.getChildNodes();
            Node child = null;

            for (int i = 0; i < list.getLength(); i++)
            {
                child = list.item(i);

                String nodeName = child.getNodeName();

                if (nodeName.equalsIgnoreCase("WfInPort"))
                {
                    WfInPort inPort = new WfInPort();

                    inPort.setFieldValues((Element) child);
                    m_inPorts.add(inPort);
                }
            }
        }

        validate();
    }

     protected void populate(FioranoStaxParser cursor) throws XMLStreamException, FioranoException
     {

        //Set cursor to the current DMI element. You can use either markCursor/getNextElement(<element>) API.
        if(cursor.markCursor(APSConstants.WORKFLOW_START_PORTS)){

            // Get Child Elements
            while(cursor.nextElement()){

                String nodeName = cursor.getLocalName();


                
                if(nodeName.equalsIgnoreCase(APSConstants.WORKFLOW_IN_PORT)){
                    WfInPort inPort = new WfInPort();
                    inPort.setFieldValues(cursor);
                    m_inPorts.add(inPort);
                }
            }
        }
        validate();
    }

    /**
     *  This method adds the specified object of <code>WfInPort</code> to this object
     *  of <code>WorkflowStartPorts</code>.
     *
     * @param inPort object of WfInPort
     * @see #getInPorts()
     * @since Tifosi2.0
     */
    public void addInPort(WfInPort inPort)
    {
        m_inPorts.add(inPort);
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
     *  This method tests whether this object of <code>WorkflowStartPorts</code>
     *  has the required(mandatory) fields set, before inserting values in
     *  the database.
     *
     * @exception FioranoException if the object is not valid
     * @since Tifosi2.0
     */
    public void validate()
        throws FioranoException
    {
        if (m_inPorts == null)
        {
            throw new FioranoException(DmiErrorCodes.ERR_INVALID_ARGUMENT_ERROR);
        }

        Enumeration enums = m_inPorts.elements();

        while (enums.hasMoreElements())
        {
            WfInPort inPort = (WfInPort) enums.nextElement();

            inPort.validate();
        }
    }


    /**
     *  This method writes this object of <code>WorkflowStartPorts</code>
     *  to the specified output stream object.
     *
     * @param out DataOutput object
     * @param versionNo
     * @exception IOException if an error occurs while converting data and
     *                         writing it to a binary stream.

     * @since Tifosi2.0
     */
    public void toStream(DataOutput out, int versionNo)
        throws IOException
    {
        super.toStream(out, versionNo);

        if (m_inPorts != null)
        {
            int length = m_inPorts.size();

            out.writeInt(length);

            Enumeration inPorts = m_inPorts.elements();

            while (inPorts.hasMoreElements())
            {
                WfInPort inPort = (WfInPort) inPorts.nextElement();

                inPort.toStream(out, versionNo);
            }
        }
        else
            out.writeInt(0);
    }


    /**
     *  This method reads this object <code>WorkflowStartPorts</code> from the
     *  specified object of input stream.
     *
     * @param is DataInput object
     * @param versionNo
     * @exception IOException if an error occurs while reading bytes or while
     *                         converting them into specified Java primitive type.

     * @since Tifosi2.0
     */
    public void fromStream(DataInput is, int versionNo)
        throws IOException
    {
        super.fromStream(is, versionNo);

        int tempInt = 0;

        if ((tempInt = is.readInt()) != 0)
        {
            for (int i = 0; i < tempInt; i++)
            {
                WfInPort inPort = new WfInPort();

                inPort.fromStream(is, versionNo);
                m_inPorts.add(inPort);
            }
        }
    }

    /**
     * This utility method gets the String representation of this object
     * of <code>WorkflowStartPorts</code>.
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
        strBuf.append("Work Flow In Port Details ");
        strBuf.append("[");
        if (m_inPorts != null)
        {
            strBuf.append("Port = ");
            for (int i = 0; i < m_inPorts.size(); i++)
            {
                strBuf.append((i + 1) + ". ");
                strBuf.append(m_inPorts.elementAt(i));
                strBuf.append(", ");
            }
        }
        strBuf.append("]");
        return strBuf.toString();
    }


    /**
     *  Returns the XML string equivalent of this object.
     *
     * @param document the input Document object
     * @return element node
     * @exception FioranoException if an error occurs while creating the element node.
     */
    Node toJXMLString(Document document)
        throws FioranoException
    {
        Node root0 = (Node) document.createElement("WorkflowStartPorts");

        if (m_inPorts != null && m_inPorts.size() > 0)
        {
            Enumeration enums = m_inPorts.elements();

            while (enums.hasMoreElements())
            {
                WfInPort port = (WfInPort) enums.nextElement();
                Node pcData = port.toJXMLString(document);

                root0.appendChild(pcData);
            }
        }

        return root0;
    }

    public void toJXMLString(XMLStreamWriter writer) throws XMLStreamException
    {
        //Start WorkflowStartPorts
        writer.writeStartElement("WorkflowStartPorts");

        if (m_inPorts != null && m_inPorts.size() > 0)
        {
            Enumeration enums = m_inPorts.elements();

            while (enums.hasMoreElements())
            {
                WfInPort port = (WfInPort) enums.nextElement();
                port.toJXMLString(writer);
            }
        }

        //End WorkflowStartPorts
        writer.writeEndElement();
    }
}
