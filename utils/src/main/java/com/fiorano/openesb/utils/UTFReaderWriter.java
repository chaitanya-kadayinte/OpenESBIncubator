package com.fiorano.openesb.utils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class UTFReaderWriter
{
    public static void writeUTF(DataOutput dos, String toWrite)
            throws IOException
    {
        byte[] byteArr = null;
        if (toWrite != null)
        {
            byteArr = toWrite.getBytes("UTF-8");
            dos.writeInt(byteArr.length);
            dos.write(byteArr);
        }
        else
            dos.writeInt(-1);

    }

    public static String readUTF(DataInput dis)
            throws IOException
    {
        byte[] byteArr = null;
        int len = dis.readInt();
        if (len == -1)
            return null;
        byteArr = new byte[len];
        dis.readFully(byteArr);
        return new String(byteArr, "UTF-8");
    }

}
