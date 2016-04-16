
package com.fiorano.openesb.application;

import com.fiorano.openesb.utils.StreamPumper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

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
            try {
                url = new File(System.getProperty("karaf.base")+"/xml-catalog/fiorano/fault.xsd").toURI().toURL();
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
