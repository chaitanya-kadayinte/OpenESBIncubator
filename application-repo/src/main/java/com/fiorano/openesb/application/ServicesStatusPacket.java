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

import java.util.Vector;

/**
 *  Description of the Class
 *
 * @author Administrator
 * @created June 10, 2002
 * @version 2.0
 */
public class ServicesStatusPacket extends DmiObject
{
    private Vector  m_vecSrvStates = new Vector();

    /**
     *  Gets the serviceStates attribute of the ServicesStatusPacket object
     *
     * @return The serviceStates value
     */
    public Vector getServiceStates()
    {
        return m_vecSrvStates;
    }

    /**
     *  Gets the objectID attribute of the AckPacket object
     *
     * @return The objectID value
     */
    public int getObjectID()
    {
        return DmiObjectTypes.SERVICES_STATUS_PACKET;
    }

    /**
     *  Adds a feature to the ServiceStatus attribute of the
     *  ServicesStatusPacket object
     *
     * @param packet The feature to be added to the ServiceStatus attribute
     */
    public void addServiceStatus(ServiceStatus packet)
    {
        m_vecSrvStates.add(packet);
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


    /**
     *  Description of the Method
     *
     * @param os Description of the Parameter
     * @param versionNo
     * @exception java.io.IOException Description of the Exception
     */
    public void toStream(java.io.DataOutput os, int versionNo)
        throws java.io.IOException
    {
        super.toStream(os, versionNo);

        int size = m_vecSrvStates.size();

        os.writeInt(size);
        for (int i = 0; i < size; i++)
        {
            ServiceStatus status = (ServiceStatus) m_vecSrvStates.elementAt(i);

            status.toStream(os, versionNo);
        }
    }


    /**
     *  Description of the Method
     *
     * @param is Description of the Parameter
     * @param versionNo
     * @exception java.io.IOException Description of the Exception
     */
    public void fromStream(java.io.DataInput is, int versionNo)
        throws java.io.IOException
    {
        super.fromStream(is, versionNo);

        int size = is.readInt();

        for (int i = 0; i < size; i++)
        {
            ServiceStatus status = new ServiceStatus();

            status.fromStream(is, versionNo);
            m_vecSrvStates.add(status);
        }
    }

}
