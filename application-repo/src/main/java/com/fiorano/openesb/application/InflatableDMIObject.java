package com.fiorano.openesb.application;

import com.fiorano.openesb.utils.exception.FioranoException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public abstract class InflatableDMIObject extends DmiObject{
    public void fromStream(java.io.DataInput is, int versionNo) throws IOException{
        byte bytes[] = new byte[is.readInt()];
        is.readFully(bytes);
        GZIPInputStream zin = new GZIPInputStream(new ByteArrayInputStream(bytes));
        try{
            setFieldValues(zin);
        } catch(FioranoException ex){
            throw (IOException)new IOException().initCause(ex);
        }
    }

    public void toStream(java.io.DataOutput out, int versionNo) throws IOException{
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        GZIPOutputStream zout = new GZIPOutputStream(bout);
        try{
            toXMLString(zout);
            zout.close();

            byte[] bytes = bout.toByteArray();
            out.writeInt(bytes.length);
            out.write(bytes);
        }catch(FioranoException ex){
            throw (IOException)new IOException().initCause(ex);
        }
    }
}
