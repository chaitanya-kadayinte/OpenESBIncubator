package com.fiorano.openesb.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * Created by Janardhan on 3/3/2016.
 */
public class ConfigReader {
    public static void readConfigFromProperties(File configFile, Object configObject) throws IOException, InvocationTargetException, IllegalAccessException {
       if(!configFile.exists()){
           return;
       }
        Properties properties = new Properties();
        FileInputStream inStream = null;
        try{
            inStream = new FileInputStream(configFile);
            properties.load(inStream);
            Method[] methods = configObject.getClass().getMethods();
            for (Method m : methods) {
                if (m.getName().startsWith("set") && !m.getName().startsWith("setClass")) {
                    String key = m.getName().substring(m.getName().indexOf("set")+3);
                    if(properties.get(key)!=null){
                        m.invoke(configObject, properties.getProperty(key));
                    }
                }
            }
        }finally {
            if(inStream!=null){
                inStream.close();
            }
        }

    }
}
