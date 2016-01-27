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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * An Extension to DmiObject whose streaming methods use GZIP Inflator of XML
 *
 * @author Santhosh Kumar T
 */
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
