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

import com.fiorano.openesb.utils.StreamPumper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author FSIPL
 * @version 1.0
 * @created January 3, 2006
 * @bundle XSD_NOT_PRESENT=Could not find {0} in the classpath
 */
public class CommonSchemas{
    public final static String ERROR_XSD;

    static{
        String resourceName = "fiorano/fault.xsd";
        URL url = CommonSchemas.class.getClassLoader().getResource(resourceName);
        if(url==null){
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if(loader!=null)
                url = loader.getResource(resourceName);
        }
        if(url==null)
            url = ClassLoader.getSystemResource(resourceName);

        if(url==null){
            System.err.println( "XSD_NOT_PRESENT "+" fiorano/fault.xsd");
            try {
                url = new File(System.getProperty("FIORANO_HOME")+"/xml-catalog/fiorano/fault.xsd").toURI().toURL();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try{
            new StreamPumper(url.openStream(), bout, true).run();
        }catch(IOException ex){
            ex.printStackTrace();
        }
        ERROR_XSD = bout.toString();
    }
}
