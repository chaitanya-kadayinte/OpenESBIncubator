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

/**
 *  Description of the Class
 *
 * @author Sachin
 * @created June 16, 2003
 * @version 2.0
 */
public class RuntimeArgsReq extends DmiObject
{
    private String  m_strServInstName;
    private String  m_strAppName;
    private String  m_strConnectionID;


    /**
     *  Constructor for the RuntimeArgsReq object
     */
    public RuntimeArgsReq()
    {
    }

    /**
     *  Gets the servInstName attribute of the RuntimeArgsReq object
     *
     * @return The servInstName value
     */
    public String getServInstName()
    {
        return m_strServInstName;
    }

    /**
     *  Gets the appName attribute of the RuntimeArgsReq object
     *
     * @return The appName value
     */
    public String getAppName()
    {
        return m_strAppName;
    }


    /**
     *  Gets the connectionID attribute of the RuntimeArgsReq object
     *
     * @return The connectionID value
     */
    public String getConnectionID()
    {
        return m_strConnectionID;
    }

    /**
     *  Gets the objectID attribute of the LoginInfo object
     *
     * @return The objectID value
     */
    public int getObjectID()
    {
        return DmiObjectTypes.RUNTIME_ARGS_REQ;
    }


    /**
     *  Sets the servInstName attribute of the RuntimeArgsReq object
     *
     * @param servInstName The new servInstName value
     */
    public void setServInstName(String servInstName)
    {
        m_strServInstName = servInstName;
    }

    /**
     *  Sets the appName attribute of the RuntimeArgsReq object
     *
     * @param appName The new appName value
     */
    public void setAppName(String appName)
    {
        m_strAppName = appName;
    }

    /**
     *  Sets the connectionID attribute of the RuntimeArgsReq object
     *
     * @param connectionID The new connectionID value
     */
    public void setConnectionID(String connectionID)
    {
        m_strConnectionID = connectionID;
    }

    /**
     *  Description of the Method
     *
     * @param os Description of the Parameter
     * @param versionNo Description of the Parameter
     * @exception java.io.IOException Description of the Exception
     */
    public void toStream(java.io.DataOutput os, int versionNo)
        throws java.io.IOException
    {
        super.toStream(os, versionNo);
        writeUTF(os, m_strAppName);
        writeUTF(os, m_strServInstName);
        writeUTF(os, m_strConnectionID);
    }

    /**
     *  Description of the Method
     *
     * @param is Description of the Parameter
     * @param versionNo Description of the Parameter
     * @exception java.io.IOException Description of the Exception
     */
    public void fromStream(java.io.DataInput is, int versionNo)
        throws java.io.IOException
    {
        super.fromStream(is, versionNo);
        m_strAppName = readUTF(is);
        m_strServInstName = readUTF(is);
        m_strConnectionID = readUTF(is);
    }


    /**
     *  Description of the Method
     *
     * @exception FioranoException Description of the Exception
     */
    public void validate()
        throws FioranoException
    {
    }

    /**
     *  Description of the Method
     */
    public void reset()
    {
    }

}
