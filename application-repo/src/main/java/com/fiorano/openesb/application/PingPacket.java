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

import java.io.*;


/**
 *  Class represents a Ping Packet which is sent by Tifosi Peer Servers(FPS) at
 *  regular intervals to Tifosi Enterprise Server(TES). These ping packet are
 *  used by TES to maintain information about alive FPS.
 *
 * @author Administrator
 * @created 23rd July 2002
 * @version 1.0
 */

public class PingPacket extends DmiObject
{
    private String  m_pingSource;


    /**
     *  Constructor for the PingPacket object
     */
    public PingPacket()
    {
        m_pingSource = null;
    }

    /**
     *  Constructor for the PingPacket object
     *
     * @param pingSource Node from which ping packet is generated.
     */
    public PingPacket(String pingSource)
    {
        m_pingSource = pingSource;
    }

    /**
     *  Gets the objectID attribute of the PingPacket object
     *
     * @return The objectID value
     */
    public int getObjectID()
    {
        return DmiObjectTypes.PING_PACKET;
    }

    /**
     *  Gets the pingSource attribute of the PingPacket object
     *
     * @return The pingSource value
     */
    public String getPingSource()
    {
        return m_pingSource;
    }

    /**
     *  Gets the bytes attribute of the PingPacket object
     *
     * @param versionNo
     * @return The bytes value
     */
    public byte[] getBytes(int versionNo)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try
        {
            toStream(dos, versionNo);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return null;
        }
        return bos.toByteArray();
    }

    /**
     *  Sets the pingSource attribute of the PingPacket object
     *
     * @param nodeName The new pingSource value
     */
    public void setPingSource(String nodeName)
    {
        m_pingSource = nodeName;
    }

    /**
     *  Check for the validity of the Ping packet.
     *
     * @exception FioranoException thrown if Ping packet found invalid.
     */
    public void validate()
        throws FioranoException
    {
        if (m_pingSource == null)
            throw new FioranoException("ping source is null");
    }

    /**
     *  resets the ping packet to default values.
     */
    public void reset()
    {
        m_pingSource = null;
    }

    /**
     *  Writes PingPacket to DataOutputStream.
     *
     * @param dos DataOutput Stream
     * @param versionNo
     * @exception IOException thrown in case of error
     */
    public void toStream(DataOutput dos, int versionNo)
        throws IOException
    {
        writeUTF(dos, m_pingSource);
    }

    /**
     *  Reads PingPacket from DataInput Stream.
     *
     * @param dis DataInput Stream.
     * @param versionNo
     * @exception IOException thrown if error occurs
     */
    public void fromStream(DataInput dis, int versionNo)
        throws IOException
    {
        m_pingSource = readUTF(dis);
    }

}
